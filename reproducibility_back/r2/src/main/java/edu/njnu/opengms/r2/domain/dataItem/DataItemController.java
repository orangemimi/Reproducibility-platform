package edu.njnu.opengms.r2.domain.dataItem;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.njnu.opengms.common.enums.ResultEnum;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
import edu.njnu.opengms.r2.domain.folder.Folder;
import edu.njnu.opengms.r2.domain.folder.FolderRepository;
import edu.njnu.opengms.r2.domain.folder.FolderService;
import edu.njnu.opengms.r2.domain.scenario.ResourceCollection;
import edu.njnu.opengms.r2.domain.scenario.Scenario;
import edu.njnu.opengms.r2.domain.scenario.ScenarioRepository;
import edu.njnu.opengms.r2.domain.scenario.ScenarioService;
import edu.njnu.opengms.r2.domain.scenario.dto.UpdateResourceScenarioDTO;
import edu.njnu.opengms.r2.remote.DataContainerService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @Author ：Zhiyi
 * @Date ：2023/8/23 17:08
 * @modified By：
 * @version: 1.0.0
 */

@RestController
@RequestMapping(value = "/data")
public class DataItemController {
//    @Autowired
//    DataItemRepository dataItemRepository;

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


    //    @RequestMapping(value = "", method = RequestMethod.POST)
//    public JsonResult uploadMultipleData(@JwtTokenParser(key = "userId") String userId, @RequestBody AddDataItemDTO add) throws IOException, ServletException {
//        DataItem dataItem = new DataItem();
//        add.convertTo(dataItem);
//        dataItem.setContributorId(userId);
//        return ResultUtils.success(dataItemRepository.insert(dataItem));
//    }
    @RequestMapping(value = "/getDataItems", method = RequestMethod.POST)
    public JsonResult getDataItems(@RequestBody List<String> itemIds) {
        List<DataItem> DataItems = dataItemRepository.findAllByIdIn(itemIds);
        return ResultUtils.success(DataItems);
    }

//    @RequestMapping(value = "/getDataItemsByScenarioId", method = RequestMethod.POST)
//    public JsonResult getDataItemsByScenarioId(@RequestBody String scenarioId) {
//        // 先通过scenarioId获取Folder信息，然后通过folder中的dataList获取所有的dataItem
//        Folder folder =  (Folder)folderService.getFolderByScenarioId(scenarioId);
//
//        return ResultUtils.success(folder);
//    }

    // 在线表格编辑中，保存为新的文档
    @RequestMapping(value = "/saveAsNewDocument/{fileSize}/{storedFolderId}", method = RequestMethod.POST)
    public JsonResult saveAsNewDocument(HttpServletRequest request, @JwtTokenParser(key = "userId") String userId, @PathVariable String storedFolderId, @PathVariable String fileSize) throws IOException, ServletException {
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;

        String historyJson = multiRequest.getParameter("history");
        String[] historyArray = historyJson.split(",");
        List<String> history = Arrays.asList(historyArray);
        String notes = multiRequest.getParameter("notes");

        return ResultUtils.success(dataItemService.SaveDocument(request, userId, storedFolderId, fileSize, history, notes));
    }

    // 在线表格编辑中，替换原文档
    @RequestMapping(value = "/replaceDocument/{fileSize}/{storedFolderId}", method = RequestMethod.POST)
    public JsonResult replaceDocument(HttpServletRequest request, @JwtTokenParser(key = "userId") String userId, @PathVariable String storedFolderId, @PathVariable String fileSize) throws IOException, ServletException {
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;

        String historyJson = multiRequest.getParameter("history");
        String[] historyArray = historyJson.split(",");
        List<String> history = Arrays.asList(historyArray);
        String notes = multiRequest.getParameter("notes");
//        System.out.println(history + notes);
        return ResultUtils.success(dataItemService.replaceDocument(request, userId, storedFolderId, fileSize, history, notes));
//        return null;
    }


    @RequestMapping(value = "/uploadFileForm/{fileSize}/{storedFolderId}", method = RequestMethod.POST)
    public JsonResult uploadMultipleData(HttpServletRequest request, @JwtTokenParser(key = "userId") String userId, @PathVariable String storedFolderId, @PathVariable String fileSize) throws IOException, ServletException {
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

        if (storedFolderId.equals("intermediate")) {
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

            Folder returnFolder1 = folderService.updataDataList(storedFolderId, resultData.id);
            String scenarioFolder = dataItemService.setScenrioResource(storedFolderId);

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
            return ResultUtils.success(folder);
        }

    }

}
