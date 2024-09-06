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

public class InstallRequiresWebSocketHandler extends TextWebSocketHandler {

    private DockerClient dockerClient;

    public InstallRequiresWebSocketHandler(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String containerId = message.getPayload();
        try {
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd("pip", "install", "-r", "/app/requirements.txt")
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
                                session.sendMessage(new TextMessage(log));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            super.onNext(frame);
                        }
                    }).awaitCompletion();

            session.sendMessage(new TextMessage("Installation completed"));
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            try {
                session.sendMessage(new TextMessage("Error installing dependencies: " + e.getMessage()));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
