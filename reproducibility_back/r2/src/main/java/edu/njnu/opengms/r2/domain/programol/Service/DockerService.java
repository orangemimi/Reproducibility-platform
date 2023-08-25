package edu.njnu.opengms.r2.domain.programol.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import edu.njnu.opengms.r2.domain.programol.Dao.DockerDao;
import edu.njnu.opengms.r2.domain.programol.executeParam;
import edu.njnu.opengms.r2.zaqizaba.MyExecStartResultCallback;
import edu.njnu.opengms.r2.zaqizaba.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author TRY
 * @Date 2023/4/6 14:04
 * @Version 1.0
 */
@Service
public class DockerService {
    @Autowired
    DockerDao dockerDao;
    @Autowired
    DockerClient dockerClient;
    int startport=60000;
    @Autowired
    WebSocketServer webSocketService;

    String exeId;



    // 创建容器
    public String createContainer(String imageName,String containerName,String volumnPath,String workDir){
        PortBinding portBinding = PortBinding.parse(startport+":"+startport);
        Volume workVolume=new Volume("/"+workDir);
//        volumnPath="E:\\workspace\\62c59b2fa5c524973a4d5cc4\\test";
        volumnPath.replace("\\","/");
        Bind workBind=new Bind(volumnPath,workVolume);
        List<Bind> bindList=new ArrayList<Bind>();
        bindList.add(workBind);
        List<Volume> volumeList=new ArrayList<Volume>();
        volumeList.add(workVolume);

        // 创建容器
        CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
                .withName(containerName)
                .withStdinOpen(true)
                .withTty(true)
                .withWorkingDir("/"+workDir)
                .withExposedPorts(ExposedPort.tcp(startport))
                .withPortBindings(portBinding)
                .withBinds(bindList)
                .withVolumes(volumeList)
                .exec();
//        CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(imageName)
//                .withName(projectId)
//                .withExposedPorts(ExposedPort.tcp(startport))
//                .withBinds(bindList)
//                .withWorkingDir("/"+projectName)
//                .withVolumes(volumeList)
//                .withCmd("/bin/bash")
//                .withStdinOpen(true)
//                .withTty(true)
//                .withPortBindings(portBinding);
        String containerId = container.getId();
        startport++;
        return containerId;
    }
    public boolean checkstate(String containerId){
        boolean ifrunning=false;
        List<Container> containers = dockerClient.listContainersCmd().exec();
        System.out.println(containers);
        for (Container container : containers) {
            if (container.getId().equals(containerId)) {
                ifrunning=true;
            }
        }
        return ifrunning;
    }
    // 启动容器
    public void startContainer(String containerId) throws DockerException, InterruptedException {
        dockerClient.startContainerCmd(containerId).exec();
    }

    //下载依赖文件
    public void downloadDep(String containerId,String projectId) throws InterruptedException {
        String[] cmd = {"/bin/bash", "-c", "pip install -r requirements.txt"};
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withTty(true)
                    .withAttachStdin(true)
                    .withCmd(cmd)
                    .exec();
        MyExecStartResultCallback callback=new MyExecStartResultCallback(projectId,webSocketService);
            dockerClient.execStartCmd(execCreateCmdResponse.getId()).exec(callback).awaitCompletion();
    }
    //下载依赖文件
    public void downloadDep(String containerId) throws InterruptedException {
        String[] cmd = {"/bin/bash", "-c", "pip install -r requirements.txt"};
        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(true)
                .withAttachStdin(true)
                .withCmd(cmd)
                .exec();
        dockerClient.execStartCmd(execCreateCmdResponse.getId()).exec((new ExecStartResultCallback(System.out, System.err))).awaitCompletion();
    }

    //运行
    public void rundemo(String containerId,String projectId) throws InterruptedException {
        Map<String,String> map=new HashMap<String,String>();
        String[] cmd = {"/bin/bash", "-c", "python main.py"};
        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withTty(true)
                    .withAttachStdin(true)
                    .withCmd(cmd)
                    .exec();
        MyExecStartResultCallback callback=new MyExecStartResultCallback(projectId,webSocketService);
        exeId=execCreateCmdResponse.getId();

        dockerClient.execStartCmd(exeId).exec(callback).awaitCompletion();
    }
    //运行
    public void rundemo(String containerId, String userId, String pyFileName, List<executeParam> executeParams, String workDir) throws InterruptedException {
        Map<String,String> map=new HashMap<String,String>();
        String executeOrder="python "+pyFileName;
        for (int i = 0; i < executeParams.size(); i++) {
            executeParam executeParam = executeParams.get(i);
            String pre = executeParam.getPre();
            String value = executeParam.getValue();
            executeOrder=executeOrder+" "+pre+" "+value;
        }
        String[] cmd = {"/bin/bash", "-c", executeOrder};
        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(true)
                .withAttachStdin(true)
                .withCmd(cmd)
                .exec();
//        MyExecStartResultCallback callback=new MyExecStartResultCallback(userId,webSocketService);
        exeId=execCreateCmdResponse.getId();
        dockerClient.execStartCmd(exeId).exec((new ExecStartResultCallback(System.out, System.err))).awaitCompletion();
    }


    // 停止容器
    public void stopContainer(String containerId) throws DockerException, InterruptedException {
        dockerClient.stopContainerCmd(containerId).exec();
    }

    // 删除容器
    public void removeContainer(String containerId) throws DockerException, InterruptedException {
        RemoveContainerCmd removeContainerCmd = dockerClient.removeContainerCmd(containerId);
        removeContainerCmd.withForce(true).exec();
    }


    public void stopdemo(String containerId, String projectId) throws InterruptedException {
        String[] cmd = {"/bin/bash", "-c", "kill -INT `pidof python`"};

        // 执行命令
        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(true)
                .withAttachStdin(true)
                .withCmd(cmd)
                .exec();
        MyExecStartResultCallback callback=new MyExecStartResultCallback(projectId,webSocketService);
        dockerClient.execStartCmd(execCreateCmdResponse.getId()).exec(callback).awaitCompletion();
    }
}
