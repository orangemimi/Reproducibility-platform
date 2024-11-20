package edu.njnu.opengms.r2.domain.environmentalConfiguration;

import cn.hutool.json.JSONObject;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

}
