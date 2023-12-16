//package edu.njnu.opengms.r2.zaqizaba;
//
//import com.github.dockerjava.api.model.Frame;
//import com.github.dockerjava.api.model.StreamType;
//import com.github.dockerjava.core.command.ExecStartResultCallback;
//import org.springframework.web.socket.TextMessage;
//
//public class MyExecStartResultCallback extends ExecStartResultCallback {
//    WebSocketServer webSocketService;
//
//    private String projectId;
//
//    public MyExecStartResultCallback(String projectId, WebSocketServer webSocketService) {
//        super(System.out, System.err);
//        this.projectId=projectId;
//        this.webSocketService=webSocketService;
//    }
//
//    @Override
//    public void onNext(Frame frame) {
//        if (frame.getStreamType()== StreamType.STDOUT) {
//            String message = new String(frame.getPayload());
//            TextMessage textMessage = new TextMessage(message);
//            webSocketService.sendMessageByProject(projectId,message);
//        } else if (frame.getStreamType()== StreamType.STDERR) {
//            String message = new String(frame.getPayload());
//            TextMessage textMessage = new TextMessage(message);
//            webSocketService.sendMessageByProject(projectId,message);
//        }
//        super.onNext(frame);
//    }
//}
