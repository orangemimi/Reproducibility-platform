package edu.njnu.opengms.r2.zaqizaba;

import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.StreamType;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import org.springframework.web.socket.TextMessage;

/**
 * @Author TRY
 * @Date 2023/4/12 16:02
 * @Version 1.0
 */
public class MyExecStartResultCallback extends ExecStartResultCallback {
    WebSocketServer webSocketService;

    private String projectId;

    public MyExecStartResultCallback(String projectId,WebSocketServer webSocketService) {
        super(System.out, System.err);
        this.projectId=projectId;
        this.webSocketService=webSocketService;
    }

    @Override
    public void onNext(Frame frame) {
        if (frame.getStreamType()== StreamType.STDOUT) {
            String message = new String(frame.getPayload());
            TextMessage textMessage = new TextMessage(message);
            webSocketService.sendMessageByProject(projectId,message);
        } else if (frame.getStreamType()== StreamType.STDERR) {
            String message = new String(frame.getPayload());
            TextMessage textMessage = new TextMessage(message);
            webSocketService.sendMessageByProject(projectId,message);
        }
        super.onNext(frame);
    }



}
