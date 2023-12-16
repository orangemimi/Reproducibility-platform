//package edu.njnu.opengms.r2.domain.codingol;
//
//import com.github.dockerjava.api.DockerClient;
//import com.github.dockerjava.api.command.CreateContainerResponse;
//import com.github.dockerjava.api.model.ExposedPort;
//import com.github.dockerjava.api.model.Frame;
//import com.github.dockerjava.core.command.LogContainerResultCallback;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.nio.charset.StandardCharsets;
//
//@Service
//public class PythonExecutionService {
//
//    @Autowired
//    DockerClient dockerClient;
////    int startport = 60000;
//
//    public String executePythonCodeInDocker(String pythonCode) throws Exception {
//        // docker镜像名称
////        String imageName = "python:3.9.18-bullseye";
//        String imageName = "pythonplus:1.0";
//
//
//        // 创建Docker容器，启动，执行Python代码，获取结果，然后停止和删除容器
//        String containerId = createAndRunPythonContainer(imageName, pythonCode);
//        String result = getContainerLogs(containerId);
//        stopAndRemoveContainer(containerId);
//
//        return result;
//    }
//    public String createAndRunPythonContainer(String imageName, String pythonCode) throws Exception {
//        CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
//                .withCmd("python", "-c", pythonCode)
////                .withName("test") // 容器名称
//                .withStdinOpen(true) // 打开标准输入流
//                .withTty(true) // 使用终端
//                .withWorkingDir("/") // 工作目录
////                .withExposedPorts(ExposedPort.tcp(startport)) // 暴露端口
////                .withPortBindings(portBinding) // 端口映射
////                .withBinds(bindList) // 卷挂载
////                .withVolumes(volumeList) // 卷
//                .exec();
//        System.out.println("in");
//        String containerId = container.getId();
//        dockerClient.startContainerCmd(containerId).exec();
//
//        return containerId;
//    }
//
//    public String getContainerLogs(String containerId) {
//        StringBuilder result = new StringBuilder();
//        try {
//            LogContainerResultCallback callback = new LogContainerResultCallback() {
//                @Override
//                public void onNext(Frame item) {
//                    // 将日志帧转换为字符串
//                    String logMessage = new String(item.getPayload(), StandardCharsets.UTF_8); // 使用UTF-8编码
//                    // 处理每个日志帧，通常是将其添加到结果字符串中
//                    result.append(logMessage);
//                }
//            };
//
//            dockerClient.logContainerCmd(containerId)
//                    .withStdOut(true)
//                    .withStdErr(true)
//                    .withFollowStream(true)
//                    .exec(callback)
//                    .awaitCompletion();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result.toString();
//    }
//
//    public void stopAndRemoveContainer(String containerId) {
//        try {
//
////            dockerClient.stopContainerCmd(containerId).exec();
//            dockerClient.removeContainerCmd(containerId).withForce(true).exec();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
