package edu.njnu.opengms.r2.domain.codingol;

import cn.hutool.json.JSONObject;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import edu.njnu.opengms.common.enums.ResultEnum;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
import edu.njnu.opengms.r2.domain.dataItem.AddDataItemDTO;
import edu.njnu.opengms.r2.domain.dataItem.DataItem;
import edu.njnu.opengms.r2.domain.dataItem.DataItemRepository;
import edu.njnu.opengms.r2.domain.folder.Folder;
import edu.njnu.opengms.r2.domain.folder.FolderRepository;
import edu.njnu.opengms.r2.domain.folder.FolderService;
import edu.njnu.opengms.r2.domain.scenario.ResourceCollection;
import edu.njnu.opengms.r2.domain.scenario.Scenario;
import edu.njnu.opengms.r2.domain.scenario.ScenarioRepository;
import edu.njnu.opengms.r2.domain.scenario.dto.UpdateResourceScenarioDTO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class PythonExecutionService {

    @Autowired
    DockerClient dockerClient;

    @Autowired
    DataItemRepository dataItemRepository;

    @Autowired
    FolderService folderService;

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    ScenarioRepository scenarioRepository;


    private static final String UPLOAD_API_URL = "http://221.224.35.86:38083/data";
    private static final String DATA_FOLDER_PATH = "E:/dockerData/works";

    public String executePythonCodeInDocker(String pythonCode, String folderId, String userId) throws Exception {
        // docker镜像名称
        String imageName = "pythonplus:1.0";


        // 创建Docker容器，启动，执行Python代码，获取结果，转移并上传文件，然后停止和删除容器
        String containerId = createAndRunPythonContainer(imageName, pythonCode);
        String result = getContainerLogs(containerId);
        getContainerFileContent(containerId, folderId, userId, "./works");
        stopAndRemoveContainer(containerId);

        return result;
    }

    public String createAndRunPythonContainer(String imageName, String pythonCode) throws Exception {

        CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
                .withCmd("python", "-c", pythonCode)
//                .withName("test") // 容器名称
                .withStdinOpen(true) // 打开标准输入流
                .withTty(true) // 使用终端
                .withWorkingDir("/works") // 工作目录
//                .withExposedPorts(ExposedPort.tcp(startport)) // 暴露端口
//                .withPortBindings(portBinding) // 端口映射
//                .withBinds(bindList) // 卷挂载
//                .withVolumes(volumeList) // 卷
                .exec();
        String containerId = container.getId();

/*      // 获取容器的当前工作目录
        InspectContainerResponse inspectContainerResponse = dockerClient.inspectContainerCmd(containerId).exec();
        String containerWorkingDir = inspectContainerResponse.getConfig().getWorkingDir();
        // 输出容器的当前工作目录
        System.out.println("Container working directory: " + containerWorkingDir);*/

        dockerClient.startContainerCmd(containerId).exec();


        return containerId;
    }

    // 拿到执行后打印的内容
    public String getContainerLogs(String containerId) {
        StringBuilder result = new StringBuilder();
        try {
            LogContainerResultCallback callback = new LogContainerResultCallback() {
                @Override
                public void onNext(Frame item) {
                    // 将日志帧转换为字符串
                    String logMessage = new String(item.getPayload(), StandardCharsets.UTF_8); // 使用UTF-8编码
                    // 处理每个日志帧，通常是将其添加到结果字符串中
                    result.append(logMessage);
                }
            };
            dockerClient.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .withFollowStream(true)
                    .exec(callback)
                    .awaitCompletion();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return result.toString();
    }

    // 停止并删除容器，
    public void stopAndRemoveContainer(String containerId) {
        try {
//            dockerClient.stopContainerCmd(containerId).exec();
            dockerClient.removeContainerCmd(containerId).withForce(true).exec();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 将容器中的文件内容复制到宿主机上
    public void getContainerFileContent(String containerId, String folderId, String userId, String filePath) throws IOException {
        try {
            Process process = Runtime.getRuntime().exec("docker cp " + containerId + ":" + filePath + " E:\\dockerData");
            // 等待命令执行完成
            int exitCode = process.waitFor();
            // 等待进程完成
            if (exitCode == 0) {
                List<Map<String, String>> uploadedFilesInfo = uploadFiles();
                for (Map<String, String> fileInfo : uploadedFilesInfo) {
                    String fileName = fileInfo.get("fileName");
                    String url = fileInfo.get("url");
                    String size = fileInfo.get("size");
                    updateDataBase(url, folderId, userId, fileName, size);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); // 这里可以根据实际情况处理异常
        }
    }

    // 读取和捕获数据文件
    public List<Map<String, String>> uploadFiles() {
        File dataFolder = new File(DATA_FOLDER_PATH);

        if (!dataFolder.exists()) {
            // 处理文件夹不存在的情况
            return new ArrayList<>();
        }

        File[] files = dataFolder.listFiles();
        List<Map<String, String>> uploadedFilesInfo = new ArrayList<>();

        for (File file : files) {
            try {
                Map<String, String> fileInfo = uploadFileToServer(file);
                uploadedFilesInfo.add(fileInfo);
            } catch (IOException e) {
                e.printStackTrace(); // 处理上传失败的情况
            }
        }

        deleteFilesInFolder(dataFolder);

        return uploadedFilesInfo;
    }

    // 将捕获的文件夹上传到服务器
    private Map<String, String> uploadFileToServer(File file) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // 获取文件大小（字节）
        String fileSizeWithUnit = getFileSizeWithUnit(file.length());

        // 获取文件名
        String fileName = file.getName();
        // 构建请求体
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("datafile", new FileSystemResource(file));
        body.add("name", fileName);

        // 发送请求
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                UPLOAD_API_URL,
                HttpMethod.POST,
                requestEntity,
                JSONObject.class
        );

        // 处理响应
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String url = responseEntity.getBody().getJSONObject("data").getStr("id");
            Map<String, String> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("url", url);
            result.put("size", fileSizeWithUnit);
            return result; // 返回服务器返回的URL
        } else {
            // 处理上传失败的情况
            throw new RuntimeException("File upload failed. Status code: " + responseEntity.getStatusCodeValue());
        }
    }

    // 根据数据容器中返回的url，更新本地数据库
    private void updateDataBase(String url, String storedFolderId, String userId, String fullName, String fileSize) {

        //add to dataItem in mongodb
        int lastDotIndex = fullName.lastIndexOf('.');
        String filename;
        String suffix;
        if (lastDotIndex != -1 && lastDotIndex < fullName.length() - 1) {
            // 获取文件名和拓展名
            filename = fullName.substring(0, lastDotIndex);
            suffix = fullName.substring(lastDotIndex);

        } else {
            filename = fullName;
            suffix = "txt";
        }
        AddDataItemDTO add = AddDataItemDTO.builder()
                .contributorId(userId)
                .name(filename)
                .suffix(suffix)
                .value("http://221.224.35.86:38083/data/" + url)
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

        Folder returnFolder1 = folderService.updataDataList(storedFolderId, resultData.getId());
        String scenarioFolder = setScenrioResource(storedFolderId);

        Folder folder = folderRepository.findById(scenarioFolder).orElseThrow(MyException::noObject);
        String tagId = folder.getTagId();

        Scenario scenario = scenarioRepository.findById(tagId).orElse(null);
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

//        return ResultUtils.success(folder);

    }

    // 删除本机文件夹里面的临时文件
    private void deleteFilesInFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
    }

    // 文件大小计算方法
    private String getFileSizeWithUnit(long fileSizeInBytes) {
        if (fileSizeInBytes < 1024) {
            return fileSizeInBytes + "Bytes";
        } else {
            double fileSize = fileSizeInBytes;
            String[] units = {"KB", "MB", "GB"};

            for (String unit : units) {
                fileSize /= 1024.0;
                if (fileSize < 1024) {
                    return String.format("%.2f%s", fileSize, unit);
                }
            }

            return fileSizeInBytes / 1024.0 / 1024.0 / 1024.0 + "GB"; // 如果文件大小非常大，使用GB
        }
    }

    public String setScenrioResource(String storedFolderId) {
        Folder folder = folderRepository.findById(storedFolderId).orElseThrow(MyException::noObject);
        String object;
        String tagId = folder.getTagId();
        if (tagId == null) {
            if (folder.getLevel() > 2) {
                object = setScenrioResource(folder.getParent());
            } else {
                object = null;
            }
        } else {
            object = folder.getId();
        }
        return object;
    }
}
