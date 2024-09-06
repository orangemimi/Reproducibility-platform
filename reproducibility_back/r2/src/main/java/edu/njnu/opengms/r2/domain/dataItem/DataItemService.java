package edu.njnu.opengms.r2.domain.dataItem;

import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.enums.ResultEnum;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Author ：Guangjin
 * @Date ：2024/1/16 21:31
 * @modified By：
 * @version: 1.0.0
 */

@Service
public class DataItemService {

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

    // replace更新文档内容
    public String replaceDocument(HttpServletRequest request, String userId, String storedFolderId, String fileSize, List<String> history, String notes) throws IOException, ServletException {
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        Part part = multiRequest.getPart("file");
        String header = part.getHeader("Content-Disposition");
        String filename_withsuffix = header.substring(header.indexOf("filename=\"") + 10, header.lastIndexOf("\""));//filename=" (整个字符串长度为10，所以要加10)
        String filename = FilenameUtils.getBaseName(filename_withsuffix);
        String suffix = "." + FilenameUtils.getExtension(filename_withsuffix);
        File file = File.createTempFile(part.getName(), suffix);//创建临时文件
        FileUtils.copyInputStreamToFile(part.getInputStream(), file);
        FileSystemResource resource = new FileSystemResource(file);      //临时文件

        String url = uploadToDataContainer(request);

        if (storedFolderId.equals("intermediate")) {
            return "http://112.4.132.6:8083/data/" + url;
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
                    .history(history)
                    .notes(notes)
                    .build();
            DataItem dataItem = new DataItem();
            add.convertTo(dataItem);
            DataItem resultData = dataItemRepository.insert(dataItem);

            Folder returnFolder1 = folderService.replaceDataList(storedFolderId, resultData.id, history.get(0));
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


            return "ok";
        }
    }


    // Save as为新的内容
    public String SaveDocument(HttpServletRequest request, String userId, String storedFolderId, String fileSize, List<String> history, String notes) throws IOException, ServletException {
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        Part part = multiRequest.getPart("file");
        String header = part.getHeader("Content-Disposition");
        String filename_withsuffix = header.substring(header.indexOf("filename=\"") + 10, header.lastIndexOf("\""));//filename=" (整个字符串长度为10，所以要加10)
        String filename = FilenameUtils.getBaseName(filename_withsuffix);
        String suffix = "." + FilenameUtils.getExtension(filename_withsuffix);
        File file = File.createTempFile(part.getName(), suffix);//创建临时文件
        FileUtils.copyInputStreamToFile(part.getInputStream(), file);
        FileSystemResource resource = new FileSystemResource(file);      //临时文件

        String url = uploadToDataContainer(request);


        if (storedFolderId.equals("intermediate")) {
            return "http://112.4.132.6:8083/data/" + url;
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
                    .history(history)
                    .notes(notes)
                    .build();
            DataItem dataItem = new DataItem();
            add.convertTo(dataItem);
            DataItem resultData = dataItemRepository.insert(dataItem);

            Folder returnFolder1 = folderService.updataDataList(storedFolderId, resultData.id);
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


//            return ResultUtils.success(folder);
            return "ok";
        }
    }

    // 将数据上传到数据容器
    public String uploadToDataContainer(HttpServletRequest request) throws IOException, ServletException {
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        Part part = multiRequest.getPart("file");
        String header = part.getHeader("Content-Disposition");
        String filename_withsuffix = header.substring(header.indexOf("filename=\"") + 10, header.lastIndexOf("\""));//filename=" (整个字符串长度为10，所以要加10)
        String filename = FilenameUtils.getBaseName(filename_withsuffix);
        String suffix = "." + FilenameUtils.getExtension(filename_withsuffix);
        File file = File.createTempFile(part.getName(), suffix);//创建临时文件
        FileUtils.copyInputStreamToFile(part.getInputStream(), file);
        FileSystemResource resource = new FileSystemResource(file);      //临时文件

        //upload to data container
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("serverNode", "china");
        form.add("userId", "reproducibility");
        form.add("datafile", resource);
        form.add("name", filename);
        form.add("origination", "reproducibility");
        String urlStr = "http://" + dataContainer + ":8083/data";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONObject> jsonObjectResponseEntity = restTemplate.postForEntity(urlStr, form, JSONObject.class);
        if (!jsonObjectResponseEntity.getStatusCode().is2xxSuccessful()) {
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
        String url = jsonObjectResponseEntity.getBody().getJSONObject("data").getStr("id");//获得上传数据的URL
        return url;
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
