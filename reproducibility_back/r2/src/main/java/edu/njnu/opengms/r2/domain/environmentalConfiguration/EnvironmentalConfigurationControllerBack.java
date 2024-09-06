//package edu.njnu.opengms.r2.domain.environmentalConfiguration;
//
//import edu.njnu.opengms.common.utils.JsonResult;
//import edu.njnu.opengms.common.utils.ResultUtils;
//import edu.njnu.opengms.r2.annotation.JwtTokenParser;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.*;
//
//
//@RestController
//@RequestMapping(value = "/envs")
//public class EnvironmentalConfigurationControllerBack {
//
//    @Autowired
//    PythonEnvironmentalService pythonEnvironmentalService;
//
//    // 定义静态常量来存储工作目录的路径
//    private static final String WORKING_DIRECTORY = "E:\\code\\docker\\workDirectory";
//
//
//    // 分步式做法1、生成dockerfile
//    @GetMapping("/py38dockerFile")
//    public JsonResult CreatePython38Dockerfile() throws IOException {
//
//        // 生成Dockerfile文件并保存在requirementsSavePath中
//        String dockerfileSavePath = WORKING_DIRECTORY+"\\Dockerfile";
//        pythonEnvironmentalService.getDockerfile(dockerfileSavePath);
//        // 获取更新的工作目录，供前端渲染
//        List<FileItem> directoryList = pythonEnvironmentalService.listDirectory(WORKING_DIRECTORY);
//
//        return ResultUtils.success(directoryList);
//    }
//
//    // 分步式做法2、接收数据、代码等文件
//    @PostMapping("/uploadDataAndCode")
//    public JsonResult uploadDataAndCode(@RequestParam("files") List<MultipartFile> files ,@RequestParam("path") String path ) throws IOException {
//
//        //上传数据到工作目录
//        //上传代码到工作目录
//        //(可选)上传依赖库到工作目录
//        pythonEnvironmentalService.uploadFilesToWorkingDirectory(files,path);
//        // 将上传到data文件夹的数据，同时上传到数据容器
//
//        // 获取更新的工作目录，供前端渲染
//        List<FileItem> directoryList = pythonEnvironmentalService.listDirectory(WORKING_DIRECTORY);
//        return ResultUtils.success(directoryList);
//    }
//    // 分步式做法3（可选）、生成默认依赖库
//    @GetMapping("/createDependency")
//    public JsonResult createDependency() throws IOException {
//
//        // 获取Python脚本的依赖项
//        Set<String> originDependencies = pythonEnvironmentalService.extractPythonDependencies();
//        // 替换别名
//        Set<String> dependencies = pythonEnvironmentalService.replaceInstallationName(originDependencies);
//        // 存储requirements.txt的位置
//        String requirementsSavePath = WORKING_DIRECTORY+"\\requirements.txt";
//        // 根据依赖项生成requirements.txt并保存到savePath中
//        pythonEnvironmentalService.getRequirementsTxt(dependencies,requirementsSavePath);
//
//        // 获取更新的工作目录，供前端渲染
//        List<FileItem> directoryList = pythonEnvironmentalService.listDirectory(WORKING_DIRECTORY);
//        return ResultUtils.success(directoryList);
//    }
//
//    // 分步式做法4（可选）、编辑工作目录中的文件：数据、代码、依赖等文本文件，先获取文本内容
//    @PostMapping("/editUploadedFile")
//    public JsonResult editUploadedFile(@RequestParam("filePath") String lastFilePath){
//        String content = pythonEnvironmentalService.editUploadedFile(lastFilePath);
//
//        return ResultUtils.success(content);
//    }
//
//    // 分步式做法5（可选）、将编辑后的内容写入文件
//    @PostMapping("/SaveFileContent")
//    public JsonResult SaveFileContent(@RequestParam("fileContent") String content , @RequestParam("filePath") String lastFilePath){
//        String state = pythonEnvironmentalService.SaveFileContent(content,lastFilePath);
//
//        return ResultUtils.success(state);
//    }
//
//    // 分步式做法6、生成镜像
//    @GetMapping("/buildImage")
//    public JsonResult buildImage(){
//
//        //生成镜像
//        String imageName = "py38tempimage";
//        String output = pythonEnvironmentalService.buildImage(imageName);
//
//        return ResultUtils.success(output);
//    }
//
//    // 分步式做法7、生成容器并执行代码
//    @PostMapping("/runContainer")
//    public JsonResult runContainer(@RequestParam("scriptName") String scriptName) throws IOException {
//        // 镜像名
//        String imageName = "py38tempimage";
//        // 生成容器并执行
//        String output = pythonEnvironmentalService.runContainer(scriptName,imageName);
//
//        // 获取更新的工作目录，供前端渲染
//        List<FileItem> directoryList = pythonEnvironmentalService.listDirectory(WORKING_DIRECTORY);
//        Map<String, Object> dataMap = new HashMap<>();
//        dataMap.put("output", output);
//        dataMap.put("directoryList", directoryList);
//        return ResultUtils.success(dataMap);
//    }
//
//    // 分步式做法8、删除容器，删除镜像,清空工作目录
//    @GetMapping("/deleteContainerAndImage")
//    public JsonResult deleteContainerAndImage(){
//        String imageName = "py38tempimage";
//        //删除容器
//        //删除镜像
//        String dockerState = pythonEnvironmentalService.deleteContainerAndImage(imageName);
//
//        //清空工作目录
//        String filesState = pythonEnvironmentalService.cleanWorkingDirectory();
//        return ResultUtils.success(dockerState+filesState);
//    }
//
//    // 灵活接口，读取工作目录文件
//    @GetMapping("/getWorkDirectory")
//    public JsonResult getDirectory() throws IOException {
//
//        List<FileItem> directoryList = pythonEnvironmentalService.listDirectory(WORKING_DIRECTORY);
//
//        return ResultUtils.success(directoryList);
//    }
//
//    // 灵活接口，上传文件名称和文件夹ID，就能上传数据容器，并更新数据库（folder和dataItem)
//    @PostMapping("/uploadAndUpdateOutput")
//    public JsonResult uploadAndUpdateOutput(@RequestBody Map<String, Object> requestBody,@JwtTokenParser(key = "userId") String userId) throws IOException {
//        List<String> outputNames = (List<String>)requestBody.get("outputName");
//        String folderId = (String)requestBody.get("folderId");
//        String dataFilePath = WORKING_DIRECTORY + "\\data";
//        List<Object> allDataItems = new ArrayList<>();
//        for (String outputName : outputNames) {
//            File file = new File(dataFilePath, outputName);
//            String fileSize = pythonEnvironmentalService.formatFileSize(file);
//            JsonResult result = pythonEnvironmentalService.uploadFile(file , userId,folderId,fileSize);
//            if (result != null && result.getData() != null) {
//                // 假设result.getData()返回的是一个包含数据的Map，并且我们要获取的属性名为"data"
//                Object dataItem = result.getData();
//                if (dataItem != null) {
//                    allDataItems.add(dataItem); // 将data属性的内容添加到列表中
//                }
//            }
//        }
//        // 将收集到的所有数据项封装为一个Map，并返回
//        Map<String, Object> response = new HashMap<>();
//        response.put("dataList", allDataItems); // 假设我们把列表命名为"dataList"
//        return ResultUtils.success(response);
//    }
//}
