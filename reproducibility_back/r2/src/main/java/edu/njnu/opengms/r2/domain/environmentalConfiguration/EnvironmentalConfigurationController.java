package edu.njnu.opengms.r2.domain.environmentalConfiguration;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.exception.DockerClientException;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;


@RestController
@RequestMapping(value = "/envs")
public class EnvironmentalConfigurationController {

    @Autowired
    PythonEnvironmentalService pythonEnvironmentalService;

    // 定义静态常量来存储工作目录的路径
    private static final String WORKING_DIRECTORY = "E:\\code\\docker\\workDirectory";

    @Autowired
    DockerClient dockerClient;

    // 分步式做法1、根据基础镜像生成容器-生成工作目录
    @PostMapping("/createContainer")
    public JsonResult createContainer(@RequestParam("scenarioId") String scenarioId,@RequestParam("env") String image) {
        // 检查WORKING_DIRECTORY+"\\scenarioId"这个文件夹是否存在，如果不存在就创建一个
        String fullDirectoryPath = WORKING_DIRECTORY + File.separator + scenarioId;
        File directory = new File(fullDirectoryPath);
        if (!directory.exists()) {
            boolean isCreated = directory.mkdirs();
            if (!isCreated) {
                return ResultUtils.error("Failed to create directory");
            }
        }
        // 检查data子文件夹是否存在，如果不存在就创建一个
        File dataDirectory = new File(fullDirectoryPath + File.separator + "data");
        if (!dataDirectory.exists()) {
            boolean isCreated = dataDirectory.mkdirs();
            if (!isCreated) {
                return ResultUtils.error("Failed to create data directory");
            }
        }


        // 生成容器，并绑定卷挂载目录
        try {
            CreateContainerResponse container = dockerClient.createContainerCmd(image)
                    .withCmd("tail", "-f", "/dev/null")
                    .withHostConfig(new HostConfig().withBinds(new Bind(fullDirectoryPath, new Volume("/app"))))
                    .exec();
            dockerClient.startContainerCmd(container.getId()).exec();
            return ResultUtils.success(container.getId());
        }catch(DockerException | DockerClientException e){
            System.out.println(e.getMessage());
            return ResultUtils.error("Error occurred while creating or starting the container: " + e.getMessage());
        }
    }


    // 分步式做法2、接收数据、代码等文件
    @PostMapping("/uploadDataAndCode")
    public JsonResult uploadDataAndCode(@RequestParam("files") List<MultipartFile> files ,@RequestParam("path") String relativePath ,@RequestParam("scenarioId") String scenarioId) throws IOException {

        String fullRelativeId = "//"+ scenarioId + relativePath;
        //上传数据到工作目录
        //上传代码到工作目录
        //(可选)上传依赖库到工作目录
        pythonEnvironmentalService.uploadFilesToWorkingDirectory(files,fullRelativeId);
        // 将上传到data文件夹的数据，同时上传到数据容器

        // 获取更新的工作目录，供前端渲染
        List<FileItem> directoryList = pythonEnvironmentalService.listDirectory(WORKING_DIRECTORY+"//"+ scenarioId);
        return ResultUtils.success(directoryList);
    }
    // 分步式做法3（可选）、生成默认依赖库
    @PostMapping("/createDependency")
    public JsonResult createDependency(@RequestParam("scenarioId") String scenarioId) throws IOException {

        // 获取Python脚本的依赖项
        Set<String> originDependencies = pythonEnvironmentalService.extractPythonDependencies(scenarioId);
        // 替换别名
        Set<String> dependencies = pythonEnvironmentalService.replaceInstallationName(originDependencies);
        // 存储requirements.txt的位置
        String requirementsSavePath = WORKING_DIRECTORY+"\\"+scenarioId+"\\requirements.txt";
        // 根据依赖项生成requirements.txt并保存到savePath中
        pythonEnvironmentalService.getRequirementsTxt(dependencies,requirementsSavePath);

        // 获取更新的工作目录，供前端渲染
        List<FileItem> directoryList = pythonEnvironmentalService.listDirectory(WORKING_DIRECTORY+"//"+ scenarioId);
        return ResultUtils.success(directoryList);
    }

    // 分步式做法4（可选）、编辑工作目录中的文件：数据、代码、依赖等文本文件，先获取文本内容
    @PostMapping("/editUploadedFile")
    public JsonResult editUploadedFile(@RequestParam("filePath") String lastFilePath , @RequestParam("scenarioId") String scenarioId){
        String content = pythonEnvironmentalService.editUploadedFile("\\"+scenarioId+lastFilePath);

        return ResultUtils.success(content);
    }

    // 分步式做法5（可选）、将编辑后的内容写入文件
    @PostMapping("/SaveFileContent")
    public JsonResult SaveFileContent(@RequestParam("fileContent") String content , @RequestParam("filePath") String lastFilePath, @RequestParam("scenarioId") String scenarioId){
        String state = pythonEnvironmentalService.SaveFileContent(content,"\\"+scenarioId+lastFilePath);

        return ResultUtils.success(state);
    }

    // 分步式做法6、根据requirements.txt安装依赖
    @PostMapping("/installRequires")
    public JsonResult installRequires(@RequestParam("containerId") String containerId){
        try {
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd("pip", "install", "-r", "/app/requirements.txt")
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .exec();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            dockerClient.execStartCmd(execCreateCmdResponse.getId())
                    .exec(new ExecStartResultCallback(outputStream, System.err))
                    .awaitCompletion();

            return ResultUtils.success(outputStream.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
            return ResultUtils.error("Error installing dependencies: " + e.getMessage());
        }
    }

    // 分布式做法7、允许用户多次使用该容器，并依次执行用户指定的脚本代码
    @PostMapping("/executeScript")
    public JsonResult executeScript(@RequestParam("containerId") String containerId,@RequestParam("scriptName") String scriptName,@RequestParam("scenarioId") String scenarioId) throws IOException {
        try {
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd("python", "/app/" + scriptName)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .exec();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            dockerClient.execStartCmd(execCreateCmdResponse.getId())
                    .exec(new ExecStartResultCallback(outputStream, System.err))
                    .awaitCompletion();

            List<FileItem> directoryList = pythonEnvironmentalService.listDirectory(WORKING_DIRECTORY+"//"+scenarioId);
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("output",outputStream.toString());
            dataMap.put("directoryList",directoryList);
            dataMap.put("state",200);
            return ResultUtils.success(dataMap);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            List<FileItem> directoryList = pythonEnvironmentalService.listDirectory(WORKING_DIRECTORY+"//"+scenarioId);
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("output",e.getMessage());
            dataMap.put("directoryList",directoryList);
            dataMap.put("state",40000);
            return ResultUtils.success(dataMap);
        }
    }

    // 分步式做法8、删除容器，删除镜像,清空工作目录
    @PostMapping("/destroyContainer")
    public JsonResult destroyContainer(@RequestParam("containerId") String containerId){
        // 关闭并删除容器
        String containerState = "";
        try{
            dockerClient.stopContainerCmd(containerId).exec();
            dockerClient.removeContainerCmd(containerId).exec();
            containerState = "容器已成功移除";
        }catch (Exception e){
            containerState = "容器删除失败，错误原因： " +e.getMessage();
        }


        //清空工作目录
        String filesState = pythonEnvironmentalService.cleanWorkingDirectory();
        return ResultUtils.success(containerState+filesState);
    }

    // 灵活接口，读取工作目录文件
    @PostMapping("/getWorkDirectory")
    public JsonResult getDirectory(@RequestParam("scenarioId") String scenarioId) throws IOException {

        List<FileItem> directoryList = pythonEnvironmentalService.listDirectory(WORKING_DIRECTORY+"//"+scenarioId);

        return ResultUtils.success(directoryList);
    }

    // 灵活接口，上传文件名称和文件夹ID，就能上传数据容器，并更新数据库（folder和dataItem)
    @PostMapping("/uploadAndUpdateOutput")
    public JsonResult uploadAndUpdateOutput(@RequestBody Map<String, Object> requestBody,@JwtTokenParser(key = "userId") String userId) throws IOException {
        List<String> outputNames = (List<String>)requestBody.get("outputName");
        String folderId = (String)requestBody.get("folderId");
        String containerId  = (String)requestBody.get("containerId");
        String dataFilePath = WORKING_DIRECTORY +"\\" + containerId+ "\\data";
        List<Object> allDataItems = new ArrayList<>();
        for (String outputName : outputNames) {
            File file = new File(dataFilePath, outputName);
            String fileSize = pythonEnvironmentalService.formatFileSize(file);
            JsonResult result = pythonEnvironmentalService.uploadFile(file , userId,folderId,fileSize);
            if (result != null && result.getData() != null) {
                // 假设result.getData()返回的是一个包含数据的Map，并且我们要获取的属性名为"data"
                Object dataItem = result.getData();
                if (dataItem != null) {
                    allDataItems.add(dataItem); // 将data属性的内容添加到列表中
                }
            }
        }
        // 将收集到的所有数据项封装为一个Map，并返回
        Map<String, Object> response = new HashMap<>();
        response.put("dataList", allDataItems); // 假设我们把列表命名为"dataList"
        return ResultUtils.success(response);
    }
}
