package edu.njnu.opengms.r2.domain.environmentalConfiguration;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import edu.njnu.opengms.common.enums.ResultEnum;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.domain.dataItem.AddDataItemDTO;
import edu.njnu.opengms.r2.domain.dataItem.DataItem;
import edu.njnu.opengms.r2.domain.dataItem.DataItemRepository;
import edu.njnu.opengms.r2.domain.dataItem.DataItemService;
import edu.njnu.opengms.r2.domain.folder.Folder;
import edu.njnu.opengms.r2.domain.folder.FolderRepository;
import edu.njnu.opengms.r2.domain.folder.FolderService;
import edu.njnu.opengms.r2.domain.scenario.ResourceCollection;
import edu.njnu.opengms.r2.domain.scenario.Scenario;
import edu.njnu.opengms.r2.domain.scenario.ScenarioRepository;
import edu.njnu.opengms.r2.domain.scenario.dto.UpdateResourceScenarioDTO;
import edu.njnu.opengms.r2.remote.DataContainerService;
import edu.njnu.opengms.r2.remote.ManagerServerFeign;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PythonEnvironmentalService {

    private Map<String, String> replacementMap = new HashMap<>();

    // 定义静态常量来存储工作目录的路径
    private static final String WORKING_DIRECTORY = "E:\\code\\docker\\workDirectory\\";



    @Value("${storages.local.path}")
    String pathString;

    @Value("${dataContainer}")
    private String dataContainer;

    @Autowired
    private ObjectMapper objectMapper;// 用于解析和生成 JSON

    @Autowired
    DockerClient dockerClient;

    @Autowired
    DataContainerService remoteDataContainerService;

    @Autowired
    DataItemRepository dataItemRepository;

    @Autowired
    FolderService folderService;

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    ScenarioRepository scenarioRepository;

    @Autowired
    DataItemService dataItemService;

    @Autowired
    ManagerServerFeign managerServerFeign;

    // 提取Python脚本的依赖项
    public Set<String> extractPythonDependencies(String scenarioId){
        Set<String> dependencies = new HashSet<>();
        Set<String> localModules = new HashSet<>();
        try {
            // 扫描工作目录中的所有文件，记录所有本地模块的名称
            Files.walk(Paths.get(WORKING_DIRECTORY + "\\" + scenarioId))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".py"))
                    .forEach(path -> {
                        String fileName = path.getFileName().toString();
                        String moduleName = fileName.substring(0, fileName.lastIndexOf('.'));
                        localModules.add(moduleName);
                    });
            // 匹配import语句的正则表达式
            Pattern importPattern = Pattern.compile("^\\s*import\\s+(\\S+)");
            // 匹配from ... import ...语句的正则表达式
            Pattern fromImportPattern = Pattern.compile("^\\s*from\\s+(\\S+)\\s+import");
            // 扫描工作目录中的所有文件
            Files.walk(Paths.get(WORKING_DIRECTORY+"\\"+scenarioId))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".py"))
                    .forEach(path -> {
                        try (Stream<String> lines = Files.lines(path)) {
                            lines.forEach(line -> {
                                Matcher importMatcher = importPattern.matcher(line);
                                Matcher fromImportMatcher = fromImportPattern.matcher(line);

                                if (importMatcher.find()) {
                                    String matched = importMatcher.group(1);
                                    String dependency = matched.split("\\.")[0];
                                    if (!localModules.contains(dependency)) {
                                        dependencies.add(dependency);
                                    }
                                }

                                if (fromImportMatcher.find()) {
                                    String matched = fromImportMatcher.group(1);
                                    String dependency = matched.split("\\.")[0];
                                    if (!localModules.contains(dependency)) {
                                        dependencies.add(dependency);
                                    }
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dependencies;
    }

    // 将导入别名和安装别名不一样的库做替换
    public Set<String> replaceInstallationName(Set<String> originName){
        Set<String> replacedName = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader("./pythonInstallName.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    replacementMap.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        for (String str : originName) {
//            System.out.println(str);
            String newName = replacementMap.getOrDefault(str,str);
            replacedName.add(newName);
        }
        return replacedName;
    }

    // 将提取的依赖变成requirements.txt
    public void getRequirementsTxt(Set<String> requirements, String savePath){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(savePath))) {
            for (String requirement : requirements) {
                writer.write(requirement);
                writer.newLine(); // 换行
            }
            System.out.println("Requirements have been written to file: " + savePath);
        } catch (IOException e) {
            System.err.println("Error writing requirements to file: " + e.getMessage());
        }
    }

    // 生成Dockerfile文件
    public void getDockerfile(String savePath) {
        String dockerfileContent =
                "# 使用官方Python运行时作为父镜像\n" +
                "FROM python:3.8-slim\n" +
                "\n" +
                "# 设置工作目录\n" +
                "WORKDIR /app\n" +
                "\n" +
                "# 将当前目录内容复制到位于/app中的容器中\n" +
                "COPY . /app\n" +
                "\n" +
                "# 安装requirements.txt中指定的任何所需包\n" +
                "RUN pip install --no-cache-dir -r requirements.txt\n" +
                "\n" +
                "# 使端口80可供此容器外的环境使用\n" +
                "EXPOSE 80\n" +
                "\n" +
                "# 定义环境变量\n" +
                "ENV NAME World\n";
//                "\n" +
//                "# 在容器启动时运行app.py\n" +
//                "CMD [\"python\", \"app.py\"]";

        try {
            // 创建文件对象
            File dockerfile = new File(savePath);

            // 创建文件写入对象
            FileWriter fileWriter = new FileWriter(dockerfile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // 写入内容
            bufferedWriter.write(dockerfileContent);

            // 关闭写入流
            bufferedWriter.close();

            System.out.println("Dockerfile 已成功生成并保存到路径：" + savePath);
        } catch (IOException e) {
            System.out.println("生成 Dockerfile 失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    // 构建docker镜像并生成容器
    public void buildImageAndContainer(){
        try {
            // 设置第一个命令：构建Docker镜像
            ProcessBuilder buildProcessBuilder = new ProcessBuilder("docker", "build", "-t", "test0419", ".");

            // 设置工作目录为 "E:\\code\\docker\\test"
            buildProcessBuilder.directory(new File("E:\\code\\docker\\test"));

            // 启动构建镜像的命令并等待其完成
            Process buildProcess = buildProcessBuilder.start();
            buildProcess.waitFor();

            // 读取并打印出构建镜像的输出
            printProcessOutput(buildProcess);

            // 检查构建是否成功
            if (buildProcess.exitValue() == 0) {
                // 设置第二个命令：运行Docker容器
                ProcessBuilder runProcessBuilder = new ProcessBuilder("docker", "run", "-v", "E:/code/docker/test:/app", "-p", "80:80", "test0419");

                // 启动运行容器的命令
                Process runProcess = runProcessBuilder.start();

                // 读取并打印出运行容器的输出
                printProcessOutput(runProcess);

                // 可以在这里等待容器运行的进程结束，或者根据需要进行其他操作
                // runProcess.waitFor();
            } else {
                System.out.println("Docker image build failed.");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    // 输出打印内容的私有方法
    private static String printProcessOutput(Process process) throws IOException {
        String output;
        String errorInfo;
        // 读取并打印标准输出
        output = readAndPrint(process.getInputStream(), "");
        // 读取并打印标准错误
        errorInfo = readAndPrint(process.getErrorStream(), "");
        return output+errorInfo;
    }
    private static String readAndPrint(InputStream inputStream, String prefix) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(prefix).append(line).append(System.lineSeparator());
                System.out.println(prefix + line); // 同时打印到控制台
            }
        }
        return output.toString();
    }

    // 分步式做法6、生成镜像
    public String buildImage(String ImageName){
        try {
            // 设置第一个命令：构建Docker镜像
            ProcessBuilder buildProcessBuilder = new ProcessBuilder("docker", "build", "-t", ImageName, ".");

            // 设置工作目录
            buildProcessBuilder.directory(new File(WORKING_DIRECTORY));

            // 启动构建镜像的命令并等待其完成
            Process buildProcess = buildProcessBuilder.start();
            buildProcess.waitFor();

            // 读取并打印出构建镜像的输出
            String output = printProcessOutput(buildProcess);
            return output;
        } catch (IOException | InterruptedException e) {
//            System.out.println(e);
            return e.getMessage();
//            e.printStackTrace();
        }
    }



    // 分布式做法6、根据build的image生成容器，并执行
    public String runContainer(String scriptName, String imageName){
        try {
            // 设置第二个命令：运行Docker容器
            ProcessBuilder runProcessBuilder = new ProcessBuilder("docker", "run", "-v", WORKING_DIRECTORY + ":/app", "-p", "80:80", imageName,"python",scriptName);

            // 启动运行容器的命令
            Process runProcess = runProcessBuilder.start();

            // 读取并打印出运行容器的输出
            String output = printProcessOutput(runProcess);
            return output;
            // 可以在这里等待容器运行的进程结束，或者根据需要进行其他操作
            // runProcess.waitFor();
        } catch (IOException e) {
//            System.out.println(e);
            return e.getMessage();

//            e.printStackTrace();
        }
    }


    // 将上传的文件存储到工作目录中
    public void uploadFilesToWorkingDirectory(List<MultipartFile> files ,String lastPath) throws IOException {
        // 创建工作目录
        Path workDir = Paths.get(WORKING_DIRECTORY);
        if (!Files.exists(workDir)) {
            Files.createDirectories(workDir);
        }
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    // 获取文件名
                    String fileName = file.getOriginalFilename();
                    // 创建文件路径
                    String absolutePath = WORKING_DIRECTORY + lastPath + File.separator + fileName;
                    Path path = Paths.get(absolutePath);
                    // 转存文件
                    Files.write(path, file.getBytes());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
//                    return "File upload failed: " + e.getMessage();
                }
            }
        }
//        return "You successfully uploaded " + files.size() + " files!";
    }


    // 删除容器和镜像
    public String deleteContainerAndImage(String imageName){
        // 定义一个线程池用于执行删除操作
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // 异步获取容器 ID 列表并删除容器
        executorService.submit(() -> {
            try {
                List<String> containerIds = new ArrayList<>();
                String command = "docker ps -a -q --filter ancestor=" + imageName;
                ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
                Process process = processBuilder.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    containerIds.add(line.trim());
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    System.err.println("Command executed with error. Exit code: " + exitCode);
                    return ;
                }

                // 删除容器
                for (String containerId : containerIds) {
                    String deleteContainerCommand = "docker rm -f " + containerId;
                    executeCommand(deleteContainerCommand);
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 异步删除镜像
        executorService.submit(() -> {
            // 等待容器被删除
            try {
                TimeUnit.SECONDS.sleep(5); // 等待5秒，可以根据实际情况调整
                // 同样，如果你使用的是Docker，可以使用以下命令：
                String deleteImageCommand = "docker rmi " + imageName;
                executeCommand(deleteImageCommand);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 关闭线程池
        executorService.shutdown();

        return "镜像与容器已删除";
    }

    // 清除工作目录内容
    public String cleanWorkingDirectory(){
        Path workingDirectoryPath = Paths.get(WORKING_DIRECTORY);
        try {
            deleteDirectoryRecursively(workingDirectoryPath);
            return "工作目录及其所有内容已被删除: " + WORKING_DIRECTORY;
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    // 清空文件夹中非文件夹的方法，同时通过递归清除文件夹
    private static void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.notExists(path)) {
            // 如果路径不存在，则不需要删除
            return;
        }
        if (Files.isDirectory(path)) {
            // 如果是目录，则获取目录中的所有条目
            Files.list(path).forEach(child -> {
                try {
                    // 对每个条目递归调用此方法
                    deleteDirectoryRecursively(child);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        // 删除当前文件或目录（此时它应该是空的）
        if (!path.equals(Paths.get(WORKING_DIRECTORY))&&!path.equals(Paths.get(WORKING_DIRECTORY+"\\data"))) {
            Files.delete(path);
        }
    }


    // 用来执行cmd命令的方法
    private void executeCommand(String command) {
        try {
            // 使用ProcessBuilder执行命令
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            Process process = processBuilder.start();

            // 调用已有的方法来打印输出
            printProcessOutput(process);

            // 等待进程结束
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                // 处理非零退出码，可能表示命令执行出错
                System.err.println("Command executed with error. Exit code: " + exitCode);
            }
        } catch (IOException e) {
            // 处理命令执行过程中可能遇到的IO异常
            e.printStackTrace();
        } catch (InterruptedException e) {
            // 如果waitFor()方法被中断，重新设置中断状态并处理
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } catch (Exception e) {
            // 处理命令字符串分割可能出现的异常
            e.printStackTrace();
        }
    }

    // 访问本地目录文件夹的方法
    public List<FileItem> listDirectory(String directory) throws IOException {

        List<FileItem> fileItems = getChildrenDirectory(directory);

        List<FileItem> wholeFileItems = new ArrayList<>();
        wholeFileItems.add(new FileItem("workDirectory", fileItems, true));

        return wholeFileItems;
    }
    private List<FileItem> getChildrenDirectory(String directory) throws IOException {
        Path path = Paths.get(directory);
        List<FileItem> fileItems = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)){
            for (Path entry: stream) {
                boolean isDirectory = Files.isDirectory(entry);
                String name = entry.getFileName().toString();
                List<FileItem> children = isDirectory ? getChildrenDirectory(entry.toString()) : new ArrayList<>();
                fileItems.add(new FileItem(name, children, isDirectory));
            }
        }
        return fileItems;
    }


    public String editUploadedFile(String lastFilePath){
        String filePath = WORKING_DIRECTORY + lastFilePath;
        Path filePathPath = Paths.get(filePath).toAbsolutePath();
        if (!filePathPath.startsWith(Paths.get(WORKING_DIRECTORY).toAbsolutePath())) {
            // 路径遍历攻击检测
            return "Invalid file path.";
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(filePathPath, StandardCharsets.UTF_8);
            // System.lineSeparator()用来获取该系统上的分隔符
            String content = String.join(System.lineSeparator(), lines);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading file."+e.getMessage();
        }
    }
    public String SaveFileContent(String content , String lastFilePath){
        String filePath = WORKING_DIRECTORY + lastFilePath;
        Path filePathPath = Paths.get(filePath).toAbsolutePath();
        if (!filePathPath.startsWith(Paths.get(WORKING_DIRECTORY).toAbsolutePath())) {
            // 路径遍历攻击检测
            return "Invalid file path.";
        }
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8)) {
                writer.write(content);
            }
            return "ok";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading file."+e.getMessage();
        }
    }

    // 格式化文件大小
    public String formatFileSize(File file){
        if (file == null) {
            return "0B"; // 如果文件为null，返回0字节
        }
        long size = file.length(); // 获取文件大小，单位为字节
        if (size <= 0) return "0Bytes";
        if (size < 1024) return size + "Bytes";

        int exp = (int) (Math.log(size) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1)+""; // 确定前缀（KB, MB, GB等）
        return String.format("%.2f %sB", size / Math.pow(1024, exp), pre);
    }

    public JsonResult uploadFile(File file, String userId, String folderId, String fileSize) throws IOException {
        String filename_withsuffix = file.getName();
        String filename = FilenameUtils.getBaseName(filename_withsuffix);
        String suffix = "." + FilenameUtils.getExtension(filename_withsuffix);
        Map<String, String> dataInfo = new HashMap<>();
        dataInfo.put("name",filename_withsuffix);
        // 创建临时文件
        File tempFile = File.createTempFile(filename, suffix);
        // 将上传的文件复制到临时文件
        FileUtils.copyFile(file, tempFile);

        // 创建FileSystemResource对象
        FileSystemResource resource = new FileSystemResource(tempFile);

        // 构建表单数据
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("serverNode", "china");
        form.add("userId", userId); // 使用userId参数
        form.add("datafile", resource);
        form.add("name", filename);
        form.add("origination", "reproducibility");

        // 发送请求到数据容器
        String urlStr = "http://" + dataContainer + ":8083/data";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONObject> jsonObjectResponseEntity = restTemplate.postForEntity(urlStr, form, JSONObject.class);

        // 检查响应状态
        if (!jsonObjectResponseEntity.getStatusCode().is2xxSuccessful()) {
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }

        // 获取上传数据的URL
        String url = jsonObjectResponseEntity.getBody().getJSONObject("data").getStr("id");
        dataInfo.put("url","http://" + dataContainer + ":8083/data/"+url);
        if (folderId.equals("intermediate")) {
            return ResultUtils.success("http://112.4.132.6:8083/data/" + url);
        } else {
            AddDataItemDTO add = AddDataItemDTO.builder()
                    .contributorId(userId)
                    .name(filename)
                    .suffix(suffix)
                    .value("http://112.4.132.6:8083/data/" + url)
                    .fileSize(fileSize)
                    .isInitial(true)
                    .isIntermediate(false)
                    .isParameter(false)
                    .isReproduced(false)
                    .privacy("public")
                    .build();
            DataItem dataItem = new DataItem();
            add.convertTo(dataItem);
            DataItem resultData = dataItemRepository.insert(dataItem);
            dataInfo.put("id",resultData.getId());

            Folder returnFolder1 = folderService.updataDataList(folderId, resultData.getId());
            String scenarioFolder = dataItemService.setScenrioResource(folderId);

            Folder folder = folderRepository.findById(scenarioFolder).orElseThrow(MyException::noObject);
            String tagId = folder.getTagId();

            Scenario scenario = scenarioRepository.findByProjectId(tagId);
            ResourceCollection resourceCollectionUpdate = Optional.ofNullable(scenario)
                    .map(x -> x.getResourceCollection())
                    .map(x -> {
                        List<String> dataIdList = x.getDataList();
                        List<String> modelIdList = x.getModelList();
                        if (dataIdList != null) {
                            dataIdList.add(resultData.getId());
                        }
                        ResourceCollection resourceCollection = ResourceCollection.builder()
                                .modelList(modelIdList)
                                .dataList(dataIdList)
                                .build();
                        return resourceCollection;
                    })

                    .orElseGet(() -> {
                        List<String> dataIdList = new ArrayList<>();
                        dataIdList.add(resultData.getId());

                        return ResourceCollection.builder()
                                .dataList(dataIdList)
                                .build();

                    });

            UpdateResourceScenarioDTO updateResourceScenarioDTO = new UpdateResourceScenarioDTO();
            updateResourceScenarioDTO.setResourceCollection(resourceCollectionUpdate);
            updateResourceScenarioDTO.updateTo(scenario);
            scenarioRepository.save(scenario);
            return ResultUtils.success(dataInfo);
        }
    }

    public void downloadFile(String fileUrl,String scenarioId,String fileName) throws IOException {

        String downloadDirectory =WORKING_DIRECTORY + scenarioId + "\\data";

        // 创建下载目录（如果不存在）
        File directory = new File(downloadDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 下载文件并保存到指定目录
        Path targetPath = Paths.get(downloadDirectory, fileName);
        try (InputStream in = new URL(fileUrl).openStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public String savePythonFile(String scenarioId, String fileName, String code) throws IOException {
        // 构造文件保存路径
        String saveDirectory = Paths.get(WORKING_DIRECTORY, scenarioId).toString();
        File directory = new File(saveDirectory);
        if (!directory.exists()) {
            directory.mkdirs();  // 创建路径
        }

        // 创建 .py 文件并覆盖已有内容
        File pythonFile = new File(saveDirectory, fileName + ".py");
        try (FileWriter writer = new FileWriter(pythonFile, false)) {  // false 表示覆盖文件内容
            writer.write(code);  // 将代码写入文件
        }

        // 返回文件的保存路径
        return pythonFile.getPath();
    }

    public Map<String, Object> executeScript(String containerId, String scriptName, String scenarioId) throws IOException, InterruptedException {
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
        List<FileItem> directoryList = listDirectory(WORKING_DIRECTORY + "//" + scenarioId);

        // 分析 stderr 是否有错误
        String stdout = stdoutStream.toString();
        String stderr = stderrStream.toString();
        boolean hasErrors = false;


        for (String line : stderr.split("\\n")) {
            if (line.startsWith("ERROR:")) {
                hasErrors = true;
            }
        }

        // 准备返回数据
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("output", stdout);
        dataMap.put("directoryList", directoryList);
        dataMap.put("error", stderr);


        if (hasErrors) {
            // 如果有错误输出，返回错误信息
            dataMap.put("state", 40000);
        } else {
            // 正常情况下
            dataMap.put("state", 200);
        }

        return dataMap;
    }


    public String startTask(String userId,String scenarioId,String folderId, List<Map<String, Object>> modelNodes) {
        Optional<Scenario> optionalScenario = scenarioRepository.findById(scenarioId);
        Scenario scenario = optionalScenario.orElse(new Scenario());
        try {
            // 解析 flowData
            ObjectNode flowDataNode = scenario.getFlowData() == null
                    ? objectMapper.createObjectNode()
                    : (ObjectNode) objectMapper.readTree(scenario.getFlowData());

            // 获取或初始化 task 节点
            ObjectNode taskNode = (ObjectNode) flowDataNode.get("task");
            if (taskNode != null && "running".equals(taskNode.get("state").asText())) {
                taskNode.put("state", "init");
                String previousLog = taskNode.get("modelExecutionInfo").asText();
                taskNode.put("modelExecutionInfo", previousLog + "任务已终止，重新开始执行。\n");
            } else {
                taskNode = objectMapper.createObjectNode();
                taskNode.put("state", "running");
                taskNode.put("modelExecutionInfo", "-----已开始执行工作流任务，请稍后-----\n");
            }

            // 立即保存状态到数据库,否则怎么查询都是init
            flowDataNode.set("task", taskNode);
            scenario.setFlowData(flowDataNode.toString());
            scenarioRepository.save(scenario);

            // 模拟按顺序执行节点
            for (Map<String, Object> modelNode : modelNodes) {
                String nodeId = (String) modelNode.get("id");
                String nodeType = (String) modelNode.get("type");
                JsonNode dataNode = objectMapper.convertValue(modelNode.get("data"), JsonNode.class);
                String nodeName = dataNode.get("label").asText();

                // 记录节点开始执行的日志
                String currentLog = taskNode.get("modelExecutionInfo").asText() +
                        "已开始执行节点：" + nodeName + " 类型：" + nodeType + "\n";

                // 假设模型执行
                Map<String, Object> executionResult;
                switch (nodeType) {
                    case "model":
                        Thread.sleep(3000); // 模拟执行延迟
                        executionResult = new HashMap<>();
                        boolean result = executeModelNode(userId, modelNode, scenario, modelNodes);
                        if (result) {
                            executionResult.put("success", true);
                            executionResult.put("message", "模型运行成功，请在数据管理模块查看输出数据\n");
                        } else {
                            executionResult.put("success", false);
                            executionResult.put("message", "模型服务出错，请检查您传入的数据格式是否正确，有疑问请联系模型发布者或网站管理员\n");
                        }
                        break;
                    case "codeModel":
                        executionResult = executeCodeModelNode(folderId, modelNode, scenario, flowDataNode,modelNodes);
                        break;
                    default:
                        executionResult = new HashMap<>();
                        executionResult.put("success", false);
                        executionResult.put("message", "未知节点类型: " + nodeType);
                }


                if (!(Boolean) executionResult.get("success")) {
                    taskNode.put("state", "error");
                    taskNode.put("modelExecutionInfo", currentLog + "节点执行失败："+ executionResult.get("message") +"任务终止。\n");
                    flowDataNode.set("task", taskNode);
                    scenario.setFlowData(flowDataNode.toString());
                    scenarioRepository.save(scenario);
                    return "Task execution stopped due to error.";
                }

                // 记录成功日志
                currentLog += "节点：" + nodeName + " 执行成功。\n";
                taskNode.put("modelExecutionInfo", currentLog+"节点输出内容为："+executionResult.get("message") +"\n");

                // 保存当前状态到数据库
                flowDataNode.set("task", taskNode);
                scenario.setFlowData(flowDataNode.toString());
                scenarioRepository.save(scenario);
            }

            // 所有模型执行完成
            taskNode.put("state", "success");
            taskNode.put("modelExecutionInfo",
                    taskNode.get("modelExecutionInfo").asText() + "流程图所有模型均已执行完成。\n");

            // 更新 flowData 并保存
            flowDataNode.set("task", taskNode);
            scenario.setFlowData(flowDataNode.toString());
            scenarioRepository.save(scenario);

            return "Task execution completed successfully.";

        } catch (Exception e) {
            return "Error during task execution: " + e.getMessage();
        }
    }


    // 处理 model 类型节点
    private boolean executeModelNode(String userId,Map<String, Object> taskNode, Scenario scenario,List<Map<String, Object>> modelNodes) {
        try {
            System.out.println("开始执行 model 类型节点: " + taskNode.get("id"));

            // 1、先拿到模型的数据
            Map<String, Object> dataMap = (Map<String, Object>) taskNode.get("data");
            Map<String, Object> behaviorMap = (Map<String, Object>) dataMap.get("behavior");
            String md5 = (String)dataMap.get("md5");

            // 2、调用initTask，获取ip和port
            JSONObject modelInfo = managerServerFeign.getServiceTask(md5);
            JSONObject data = modelInfo.getJSONObject("data");
            // 获取 port 和 host
            String port = data.getStr("port");
            String host = data.getStr("host");
//            String pid = data.getStr("id");
            if (port == null || host == null) {
                throw new IllegalArgumentException("Missing critical fields: post or host");
            }

            // 3、包装param？？

            // 4、清洗参数，获取invokeForm
            // 4.1、填充inputs和outputs
            Map<String, Object> invokeForm = extractModelArguments(behaviorMap);
            // 4.2、填充port和host
            invokeForm.put("port", port);
            invokeForm.put("ip", host);
            invokeForm.put("username", userId);
            invokeForm.put("pid", md5);


            JSONObject invokeForm2 = new JSONObject(invokeForm);

            // 调用单个模型，开始invoke
            JSONObject invokeResponse = managerServerFeign.invoke(invokeForm2);
            String taskId = invokeResponse.getJSONObject("data").getStr("tid");
            if (taskId == null) {
                throw new IllegalArgumentException("Task ID is null");
            }
            Map<String, Object> refreshForm = Collections.unmodifiableMap(new HashMap<String, Object>() {{
                put("tid", taskId);
                put("ip", host);
                put("port", port);
            }});

            // 开始轮询，查询执行状态，当执行完成或者错误时结束
            long startTime = System.currentTimeMillis();
            long maxWaitTime = 60 * 60 * 1000; // 最大等待时间为 60 分钟
            while (true) {
                // 每隔一定时间轮询状态
                Thread.sleep(2000); // 轮询间隔 2 秒

                JSONObject refreshResponse = managerServerFeign.refresh(new JSONObject(refreshForm));
                Integer status = refreshResponse.getJSONObject("data").getInt("status");;

                if (status == null) {
                    throw new IllegalArgumentException("Status is null in response");
                }

                // 状态为 2 表示成功，-1 表示失败
                if (status == 2) {
                    System.out.println("模型运行成功: " + taskNode.get("id"));
                    // 提取并处理最终结果（可根据需求封装返回结果）
                    List<Map<String, Object>> outputsArray = (List<Map<String, Object>>) refreshResponse.getJSONObject("data").get("outputs");
                    List<Map<String, Object>> behaviorOutputs = (List<Map<String, Object>>) behaviorMap.get("outputs");
                    // 遍历 outputsArray
                    for (Map<String, Object> response : outputsArray) {
                        String event = (String) response.get("event");
                        String url = (String) response.get("url");
                        // 遍历 behaviorMap.outputs 并更新 value
                        for (Map<String, Object> output : behaviorOutputs) {
                            if (event.equals(output.get("name"))) {
                                output.put("value", url);
                                break; // 已找到匹配项，结束当前循环
                            }
                        }
                    }
                    updateOutputValuesToInputs(behaviorOutputs, modelNodes);
                    return true;
                } else if (status == -1) {
                    System.err.println("模型运行失败: " + taskNode.get("id"));
                    return false;
                }

                // 检查是否超时
                if (System.currentTimeMillis() - startTime > maxWaitTime) {
                    System.err.println("模型运行超时: " + taskNode.get("id"));
                    return false;
                }
            }
        } catch (Exception e) {
            System.err.println("执行 model 节点时出错: " + e.getMessage()+"错误原因：");
            e.printStackTrace();
            return false;
        }
    }

    // 清洗参数，返回 invokeForm
    Map<String, Object> extractModelArguments(Map<String, Object> behaviorMap) {
        // 初始化 invokeForm
        Map<String, Object> invokeForm = new HashMap<>();
        List<Map<String, Object>> inputs = new ArrayList<>();
        List<Map<String, Object>> outputs = new ArrayList<>();

        // 获取 inputs 和 parameters
        List<Map<String, Object>> inputsAndParameters = new ArrayList<>();
        if (behaviorMap.containsKey("inputs")) {
            inputsAndParameters.addAll((List<Map<String, Object>>) behaviorMap.get("inputs"));
        }
        if (behaviorMap.containsKey("parameters")) {
            inputsAndParameters.addAll((List<Map<String, Object>>) behaviorMap.get("parameters"));
        }

        // 清洗 inputs 和 parameters
        for (Map<String, Object> item : inputsAndParameters) {
            Map<String, Object> dataRelation = (Map<String, Object>) item.get("dataRelation");
            if (dataRelation != null && !String.valueOf(dataRelation.get("value")).isEmpty()) {
                Map<String, Object> detail = new HashMap<>();
                detail.put("statename", behaviorMap.get("name"));
                detail.put("event", item.get("name"));
                detail.put("tag", item.get("name"));
                detail.put("url", dataRelation.get("value"));
                inputs.add(detail);
            }
        }

        // 获取 outputs
        List<Map<String, Object>> outputList = (List<Map<String, Object>>) behaviorMap.get("outputs");
        for (Map<String, Object> item : outputList) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("statename", behaviorMap.get("name"));
            detail.put("event", item.get("name"));

            // 处理 template
            Map<String, Object> template = new HashMap<>();
            Map<String, Object> datasetItem = (Map<String, Object>) item.get("datasetItem");
            if ("external".equals(datasetItem.get("type"))) {
                template.put("type", "id");
                template.put("value", datasetItem.get("externalId") != null ? datasetItem.get("externalId") : "");
            } else {
                template.put("type", "none");
                template.put("value", "");
            }
            detail.put("template", template);
            outputs.add(detail);
        }

        // 将 inputs 和 outputs 放入 invokeForm
        invokeForm.put("inputs", inputs);
        invokeForm.put("outputs", outputs);

        return invokeForm;
    }

    // 处理 codeModel 类型节点
    public Map<String, Object> executeCodeModelNode(String folderId,Map<String, Object> taskNode,
                                                    Scenario scenario,ObjectNode flowDataNode,List<Map<String, Object>> modelNodes) {
        Map<String, Object> executionResult = new HashMap<>();
        try {
            System.out.println("开始执行 codeModel 类型节点: " + taskNode.get("id"));
            // 1. 初始化输出数据列表
            List<Map<String, String>> currentModelOutputs = new ArrayList<>();

            // 2. 解析代码，将引用的数据替换为真实数据相对路径
            Map<String, Object> dataMap = (Map<String, Object>) taskNode.get("data");
            Map<String, Object> behaviorMap = (Map<String, Object>) dataMap.get("behavior");

            String originCode = (String) behaviorMap.get("code");
            String replacedCode = processCode(originCode, taskNode, currentModelOutputs);

            // 3. 保存替换后的 Python 脚本到 Docker 容器工作目录
            String scriptFileName = dataMap.get("label") + "";
            String filePath = savePythonFile(scenario.getId(), scriptFileName, replacedCode);

            // 4. 根据输入声明，将输入数据挂载到 Docker 容器中
            // 先获取已经上传的文件列表
            List<Map<String, String>> containerFiles = getScenarioContainerFiles(flowDataNode);
            // 初始化一个空的文件下载列表
            List<Map<String, String>> urlList = new ArrayList<>();
            // 遍历任务节点的输入，检查文件是否已经存在于容器中
            List<Map<String, Object>> inputs = (List<Map<String, Object>>) behaviorMap.get("inputs");
            for (Map<String, Object> input : inputs) {
                // 获取文件的名称和 URL
                Map<String, String> dataRelation = (Map<String, String>) input.get("dataRelation");
                String fileName =  dataRelation.get("label");
                String url =  dataRelation.get("value");

                // 检查文件是否已经存在于 containerFiles 中
                boolean urlExists = containerFiles.stream().anyMatch(file -> file.get("url").equals(url));
                if (!urlExists) {
                    // 如果文件不存在，则将其添加到下载列表
                    Map<String, String> fileEntry = new HashMap<>();
                    fileEntry.put("fileName", fileName);
                    fileEntry.put("url", url);
                    urlList.add(fileEntry);
                }
            }
            // 如果有新增输入文件，就下载文件到容器工作目录
            for (Map<String, String> fileInfo : urlList){
                String fileUrl = fileInfo.get("url");
                String fileName = fileInfo.get("fileName");
                try {
                    downloadFile(fileUrl,scenario.getId(),fileName);
                } catch (Exception e) {
                    throw new RuntimeException("Error downloading file: " + fileName + " from URL: " + fileUrl);
                }
            }

            if (urlList != null && !urlList.isEmpty()) {
                // 将新上传的文件添加到containerFiles中。
                containerFiles.addAll(urlList);
                // 将更新后的 containerFiles 写回 flowDataNode
                ObjectMapper objectMapper = new ObjectMapper();
                ArrayNode updatedContainerFiles = objectMapper.valueToTree(containerFiles);
                ObjectNode flowDataNodeNew = (ObjectNode) objectMapper.readTree(scenario.getFlowData());
                flowDataNode.set("containerFiles", updatedContainerFiles);

                // 将更新后的 flowDataNode 保存到 scenario 中
                scenario.setFlowData(flowDataNodeNew.toString());
                scenarioRepository.save(scenario);
            }

            // 5. 执行 Docker 容器，获得打印内容
            Map<String, Object> ContainExecutionResult = executeScript(scenario.getContainerId(), scriptFileName+".py",scenario.getId());
            System.out.println("docker容器执行的输出内容："+ ContainExecutionResult);
            String state = String.valueOf(ContainExecutionResult.get("state"));
            String dockerOutput="";

            String error = (String) ContainExecutionResult.get("error");
            // 这里有问题，就是警告也会放到错误流里面返回，但是警告不会造成错误
            if ("200".equals(state)) {
                dockerOutput = (String) ContainExecutionResult.get("output");
                // 如果有警告内容，也加到输出里面
                if (error != null && !error.isEmpty()) {
                    dockerOutput += "\n" + error;
                }
            } else if ("40000".equals(state)) {

                executionResult.put("success", false);
                executionResult.put("message", error);
                return executionResult;
            }


            // 6. 上传结果文件到数据容器
            List<Map<String, String>> upLoadOutputsResults = new ArrayList<>();
            List<String> outputsNames = currentModelOutputs.stream()
                .map(output -> output.get("name"))
                .collect(Collectors.toList());

            for (String outputName : outputsNames) {
                Map<String, String> resultData = new HashMap<>();
                resultData.put("filePath", outputName);

                String fullPath = WORKING_DIRECTORY + File.separator + scenario.getId() + File.separator + "data" + File.separator + outputName;
                File file = new File(fullPath);

                if (!file.exists()) {
                    resultData.put("status", "failure");
                    resultData.put("message", "File not found: " + fullPath);
                    upLoadOutputsResults.add(resultData);
                    continue;
                }

                try {
                    String fileSize = formatFileSize(file);
                    JsonResult uploadFileResult = uploadFile(file, scenario.getId(), folderId, fileSize);
                    resultData.put("status", "success");
                    String url = ((Map<String, String>) uploadFileResult.getData()).get("url");
                    resultData.put("message", url);
                    // 将输出数据的 URL 绑定到对应 output
                    Map<String, Object> taskNodeData = (Map<String, Object>) taskNode.get("data");
                    Map<String, Object> taskNodeDataBehavior = (Map<String, Object>) taskNodeData.get("behavior");
                    List<Map<String, Object>> taskNodeOutputs = (List<Map<String, Object>>) taskNodeDataBehavior.get("outputs");
                    for (Map<String, Object> output : taskNodeOutputs) {
                        String outputNameInNode = (String) output.get("name");
                        String fileNameWithoutExtension = FilenameUtils.removeExtension(outputName);
                        if (fileNameWithoutExtension.equals(outputNameInNode)) {
                            output.put("value", url); // 绑定上传的 URL
                            break;
                        }
                    }
                } catch (IOException e) {
                    resultData.put("status", "failure");
                    resultData.put("message", "Failed to upload file: " + e.getMessage());
                    e.printStackTrace();
                } catch (MyException e) {
                    resultData.put("status", "failure");
                    resultData.put("message", "Error during file upload: " + e.getMessage());
                }
                upLoadOutputsResults.add(resultData);
            }

            // 7. 更新节点信息，传递到下一个节点的输入
            Map<String, Object> taskNodeData = (Map<String, Object>) taskNode.get("data");
            Map<String, Object> taskNodeDataBehavior = (Map<String, Object>) taskNodeData.get("behavior");
            List<Map<String, Object>> taskNodeOutputs = (List<Map<String, Object>>) taskNodeDataBehavior.get("outputs");

            updateOutputValuesToInputs(taskNodeOutputs, modelNodes);

            executionResult.put("success", true);
            executionResult.put("message", dockerOutput);
            return executionResult; // 执行成功
        } catch (Exception e) {
            System.err.println("执行 codeModel 节点时出错: " + e.getMessage());

            executionResult.put("success", false);
            executionResult.put("message", "");
            return executionResult;
        }
    }

    // 解析代码并替换特殊标记
    private String processCode(String code, Map<String, Object> node, List<Map<String, String>> currentModelOutputs) {
        // 定义正则表达式
        Pattern inputPattern = Pattern.compile("@\\(\\s*\"([^\"]*?)\"\\s*\\)");
        Pattern outputPattern = Pattern.compile("@\\(\\s*\"([^\"]*?)\"\\s*,\\s*\"([^\"]*?)\"\\s*\\)");

        // 用 StringBuffer 构造替换后的代码
        Matcher inputMatcher = inputPattern.matcher(code);
        StringBuffer intermediateCode = new StringBuffer();

        while (inputMatcher.find()) {
            String inputName = inputMatcher.group(1);
            String replacement = replaceInputMarker(inputName, node);
            inputMatcher.appendReplacement(intermediateCode, Matcher.quoteReplacement(replacement));
        }
        inputMatcher.appendTail(intermediateCode);

        Matcher outputMatcher = outputPattern.matcher(intermediateCode.toString());
        StringBuffer finalCode = new StringBuffer();

        while (outputMatcher.find()) {
            String outputLabel = outputMatcher.group(1);
            String fileName = outputMatcher.group(2);
            String replacement = replaceOutputMarker(outputLabel, fileName, currentModelOutputs);
            outputMatcher.appendReplacement(finalCode, Matcher.quoteReplacement(replacement));
        }
        outputMatcher.appendTail(finalCode);

        return finalCode.toString();
    }

    // 替换输入和参数标记
    private String replaceInputMarker(String inputName, Map<String, Object> node) {
        Map<String, Object> data = (Map<String, Object>) node.get("data");
        Map<String, Object> behavior = (Map<String, Object>) data.get("behavior");

        // 处理inputs
        List<Map<String, Object>> inputs = (List<Map<String, Object>>) behavior.get("inputs");
        for (Map<String, Object> input : inputs) {
            if (inputName.equals(input.get("name"))) {
                Map<String, Object> dataRelation = (Map<String, Object>) input.get("dataRelation");
                return "\"app/data/" + dataRelation.get("label") + "\"";
            }
        }

        // 处理parameters
        List<Map<String, Object>> parameters = (List<Map<String, Object>>) behavior.get("parameters");
        for (Map<String, Object> parameter : parameters) {
            if (inputName.equals(parameter.get("name"))) {
                Map<String, Object> dataRelation = (Map<String, Object>) parameter.get("dataRelation");
                return "\"" + dataRelation.get("value") + "\"";
            }
        }

        throw new IllegalArgumentException("输入标记未找到: " + inputName);
    }

    // 替换输出标记
    private String replaceOutputMarker(String outputLabel, String fileName, List<Map<String, String>> currentModelOutputs) {
        Map<String, String> outputInfo = new HashMap<>();
        outputInfo.put("label", outputLabel);
        outputInfo.put("name", fileName);
        currentModelOutputs.add(outputInfo);
        return "\"app/data/" + fileName + "\"";
    }

    // 将输出数据绑定到对应的输入数据中
    private void updateOutputValuesToInputs(List<Map<String, Object>> taskNodeOutputs, List<Map<String, Object>> modelNodes) {
        for (Map<String, Object> output : taskNodeOutputs) {
            String outputUrl = (String) output.get("value");
            List<String> equalData = (List<String>) output.get("equalData");

            if (equalData != null && outputUrl != null) {
                for (String inputId : equalData) {
                    for (Map<String, Object> targetNode : modelNodes) {
                        Map<String, Object> targetData = (Map<String, Object>) targetNode.get("data");
                        Map<String, Object> targetBehavior = (Map<String, Object>) targetData.get("behavior");
                        List<Map<String, Object>> equalInputs = (List<Map<String, Object>>) targetBehavior.get("inputs");

                        for (Map<String, Object> input : equalInputs) {
                            if (inputId.equals(input.get("id"))) {
                                Map<String, Object> dataRelation = (Map<String, Object>) input.get("dataRelation");
                                dataRelation.put("value", outputUrl);
                            }
                        }
                    }
                }
            }
        }
    }

    // 获取scenario的容器文件信息
    private List<Map<String, String>> getScenarioContainerFiles(ObjectNode flowDataNode) {
        // 检查flowDataNode是否包含"containerFiles"键，并且对应的值是一个数组
        if (flowDataNode.has("containerFiles") && flowDataNode.get("containerFiles").isArray()) {
            ArrayNode containerFilesArray = (ArrayNode) flowDataNode.get("containerFiles");
            List<Map<String, String>> containerFiles = new ArrayList<>();

            // 遍历数组中的每个元素
            for (JsonNode fileNode : containerFilesArray) {
                // 将JsonNode转换为Map<String, String>
                Map<String, String> fileMap = new HashMap<>();
                Iterator<String> fieldNames = fileNode.fieldNames();
                while (fieldNames.hasNext()) {
                    String fieldName = fieldNames.next();
                    JsonNode valueNode = fileNode.get(fieldName);
                    // 确保值是字符串类型
                    if (valueNode.isTextual()) {
                        fileMap.put(fieldName, valueNode.asText());
                    } else {
                        // 如果值不是字符串类型，可以转换为字符串，或者选择跳过
                        fileMap.put(fieldName, valueNode.toString());
                    }
                }
                // 将转换后的Map添加到列表中
                containerFiles.add(fileMap);
            }

            return containerFiles;
        }
        // 如果"containerFiles"键不存在或不是数组，则返回空列表
        return new ArrayList<>();
    }

    // 检查task运行的状态信息
    public Map<String, String> checkRunningFlow(String scenarioId) {
        Optional<Scenario> optionalScenario = scenarioRepository.findById(scenarioId);
        Scenario scenario = optionalScenario.orElse(new Scenario());
        if (scenario == null || scenario.getFlowData() == null) {
            Map<String, String> result = new HashMap<>();
            result.put("state", "init");
            result.put("modelExecutionInfo", null);
            return result;
        }
        try {
            JsonNode flowDataNode = objectMapper.readTree(scenario.getFlowData());
            JsonNode taskNode = flowDataNode.get("task");
            if (taskNode == null || !taskNode.isObject()) {
                Map<String, String> result = new HashMap<>();
                result.put("state", "init");
                result.put("modelExecutionInfo", null);
                return result;
            }
            String state = taskNode.get("state").asText("init");
            String modelExecutionInfo = taskNode.has("modelExecutionInfo") ? taskNode.get("modelExecutionInfo").asText(null) : null;
            Map<String, String> result = new HashMap<>();
            result.put("state", state);
            result.put("modelExecutionInfo", modelExecutionInfo);
            return result;
        } catch (Exception e) {
            System.out.println("Error parsing flowData: " + e.getMessage());
            Map<String, String> result = new HashMap<>();
            result.put("state", "init");
            result.put("modelExecutionInfo", null);
            return result;
        }
    }

}
