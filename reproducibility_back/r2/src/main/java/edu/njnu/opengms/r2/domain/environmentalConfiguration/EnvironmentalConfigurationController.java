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
import edu.njnu.opengms.common.exception.MyException;
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
                    // 容器持久化运行，这样可以多次使用某个容器调用不同的python脚本
                    .withCmd("tail", "-f", "/dev/null")
                    // 卷挂载，将宿主机文件夹与容器文件夹绑定起来
                    .withHostConfig(new HostConfig().withBinds(new Bind(fullDirectoryPath, new Volume("/app"))))
                    .exec();
            // 启动容器
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

    @PostMapping("/executeScript")
    public JsonResult executeScript(@RequestParam("containerId") String containerId,
                                    @RequestParam("scriptName") String scriptName,
                                    @RequestParam("scenarioId") String scenarioId) throws IOException {
        try {
            // 创建执行命令
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd("python", "/app/" + scriptName)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .exec();

            // 定义输出流
            ByteArrayOutputStream stdoutStream = new ByteArrayOutputStream();
            ByteArrayOutputStream stderrStream = new ByteArrayOutputStream();

            // 执行命令并捕获输出
            dockerClient.execStartCmd(execCreateCmdResponse.getId())
                    .exec(new ExecStartResultCallback(stdoutStream, stderrStream))
                    .awaitCompletion();

            // 获取目录列表
            List<FileItem> directoryList = pythonEnvironmentalService.listDirectory(WORKING_DIRECTORY + "//" + scenarioId);

            // 分析 stderr 是否有错误
            String stdout = stdoutStream.toString();
            String stderr = stderrStream.toString();

            // 准备返回数据
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("output", stdout);
            dataMap.put("directoryList", directoryList);

            if (!stderr.isEmpty()) {
                // 如果有错误输出，返回错误信息
                dataMap.put("error", stderr);
                dataMap.put("state", 40000);
            } else {
                // 正常情况下
                dataMap.put("state", 200);
            }

            return ResultUtils.success(dataMap);
        } catch (InterruptedException | IOException e) {
            // 捕获异常
            e.printStackTrace();
            List<FileItem> directoryList = pythonEnvironmentalService.listDirectory(WORKING_DIRECTORY + "//" + scenarioId);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("output", e.getMessage());
            dataMap.put("directoryList", directoryList);
            dataMap.put("state", 50000); // 返回更明确的错误状态
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

    // 灵活接口，安装单个包
    @PostMapping("/installSinglePackage")
    public JsonResult installSinglePackage(@RequestParam("containerId") String containerId , @RequestParam("packageInfo") String packageInfo){
        try {
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd("pip", "install", packageInfo)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .exec();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            dockerClient.execStartCmd(execCreateCmdResponse.getId())
                    .exec(new ExecStartResultCallback(outputStream, System.err))
                    .awaitCompletion();

            System.out.println("success");
            System.out.println(outputStream.toString());

            return ResultUtils.success(outputStream.toString());
        } catch (Exception e) {
            System.out.println("error");
            System.out.println(e.getMessage());

            e.printStackTrace();
            return ResultUtils.error("Error installing dependencies: " + e.getMessage());
        }
    }

    // 将urlList中对应的文件，上传到对应的服务器文件夹中，通过卷挂载同步到容器内
    @PostMapping("/downloadFilesToVolume")
    public JsonResult downloadFilesToVolume(@RequestParam String scenarioId, @RequestBody List<Map<String, String>> urlList){
        for (Map<String, String> fileInfo : urlList){
            String fileUrl = fileInfo.get("url");
            String fileName = fileInfo.get("fileName");
            try {
                pythonEnvironmentalService.downloadFile(fileUrl,scenarioId,fileName);
            } catch (Exception e) {
                return ResultUtils.error("Error downloading file: " + fileName + " from URL: " + fileUrl);
            }
        }
        return ResultUtils.success("Files downloaded successfully for container: " + scenarioId);
    }

    // 传入code内容，创建python脚本文件并保存到对应的路径
    @PostMapping("/savePythonCode")
    public JsonResult savePythonCode(@RequestParam("scenarioId") String scenarioId, @RequestParam("fileName") String fileName, @RequestParam("code") String code) {
        try {
            String filePath = pythonEnvironmentalService.savePythonFile(scenarioId, fileName, code);
            return ResultUtils.success("Python file saved successfully at: " + filePath);
        } catch (IOException e) {
            return ResultUtils.error("Failed to save Python file: " + e.getMessage());
        }
    }

    // 接收一个本地路径列表，将对应的文件全部上传到数据容器，并添加到数据库
    @PostMapping("/uploadFilesToDataContainer")
    public JsonResult uploadFilesToDataContainer(@JwtTokenParser(key = "userId") String userId,@RequestBody Map<String, Object> requestData ) {
        List<String> filePaths = (List<String>) requestData.get("fileNames");
        String folderId = (String) requestData.get("folderId");
        String scenarioId = (String) requestData.get("scenarioId");
        List<Map<String, String>> results = new ArrayList<>();

        for (String filePath : filePaths) {
            Map<String, String> resultData = new HashMap<>();
            resultData.put("filePath", filePath);

            String fullPath = WORKING_DIRECTORY + File.separator + scenarioId + File.separator +"data" +File.separator+ filePath;
            File file = new File(fullPath);

            if (!file.exists()) {
                resultData.put("status", "failure");
                resultData.put("message", "File not found: " + fullPath);
                results.add(resultData);
                continue;
            }

            try {
                String fileSize = pythonEnvironmentalService.formatFileSize(file);
                JsonResult result = pythonEnvironmentalService.uploadFile(file, userId, folderId, fileSize);
                resultData.put("status", "success");
                String url = ((Map<String, String>)result.getData()).get("url");
                resultData.put("message", url );
            } catch (IOException e) {
                resultData.put("status", "failure");
                resultData.put("message", "Failed to upload file: " + e.getMessage());
                e.printStackTrace();
            } catch (MyException e) {
                resultData.put("status", "failure");
                resultData.put("message", "Error during file upload: " + e.getMessage());
            }
            results.add(resultData);
        }
        return ResultUtils.success(results);
    }

}
