package edu.njnu.opengms.r2.domain.environmentalConfiguration;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import org.json.JSONObject;

/**
 * @version 1.0
 * @Author rgj
 * @Date 2024/11/12 17:39
 * @注释
 */
public class InstallSinglePackageWebSocketHandler extends TextWebSocketHandler {
    private DockerClient dockerClient;

    public InstallSinglePackageWebSocketHandler(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 解析传入的 JSON 数据
        JSONObject jsonMessage = new JSONObject(message.getPayload());
        String containerId = jsonMessage.getString("containerId");
        String packageInfo = jsonMessage.getString("packageInfo");

        try {
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd("pip", "install", packageInfo)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .exec();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            dockerClient.execStartCmd(execCreateCmdResponse.getId())
                    .exec(new ExecStartResultCallback(outputStream, System.err) {
                        @Override
                        public void onNext(Frame frame) {
                            try {
                                String log = new String(frame.getPayload(), StandardCharsets.UTF_8);
                                // 实时将安装日志推送到前端
                                session.sendMessage(new TextMessage(log));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            super.onNext(frame);
                        }
                    }).awaitCompletion();

            // 完成安装，发送成功消息
            session.sendMessage(new TextMessage("Installation of " + packageInfo + " completed"));
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            try {
                session.sendMessage(new TextMessage("Error installing package: " + e.getMessage()));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
