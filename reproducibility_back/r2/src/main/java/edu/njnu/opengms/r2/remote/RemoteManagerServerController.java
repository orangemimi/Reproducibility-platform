package edu.njnu.opengms.r2.remote;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.enums.ResultEnum;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
import edu.njnu.opengms.r2.domain.dataItem.AddDataItemDTO;
import edu.njnu.opengms.r2.domain.dataItem.DataItem;
import edu.njnu.opengms.r2.domain.dataItem.DataItemRepository;
import edu.njnu.opengms.r2.domain.model.support.State;
import edu.njnu.opengms.r2.domain.modelInstance.ModelInstance;
import edu.njnu.opengms.r2.domain.modelInstance.ModelInstanceService;
import edu.njnu.opengms.r2.domain.modelInstance.dto.UpdateModelInstanceDTO;
import edu.njnu.opengms.r2.utils.FunctionUtils;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.security.SecureRandom;
import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2020/12/29 10:56
 * @modified By：
 * @version: 1.0.0
 */
@RestController
@RequestMapping(value = "/managerServer")
public class RemoteManagerServerController {
    @Autowired
    ManagerServerFeign managerServerFeign;

    @Autowired
    FunctionUtils functionUtils;
//    @Autowired
//    IntegrateTaskInstanceRepository integrateTaskInstanceRepository;
//
//    @Autowired
//    FileItemRepository fileItemRepository;

    @Value("${managerServerIpAndPort}")
    private String managerServerIpAndPort;

    @Autowired
    DataItemRepository dataItemRepository;

    @Autowired
    ModelInstanceService modelInstanceService;

//    @Value("${wzpIpAndPort}:8084")
//    String wzpIpAndPort;

    @RequestMapping(value = "/getServiceTask/{pid}", method = RequestMethod.GET)
    JsonResult getServiceTask(@PathVariable String pid) {
        System.out.println("-------------");
        return ResultUtils.success(managerServerFeign.getServiceTask(pid));
    }

    @RequestMapping(value = "/initTask", method = RequestMethod.POST)
//step 1
    JsonResult createTask(@RequestBody JSONObject serviceJson) {
//        ip port pid username
        return ResultUtils.success(managerServerFeign.createTask(serviceJson));
    }

    @RequestMapping(value = "/invoke", method = RequestMethod.POST)
    JsonResult getServiceTask(@RequestBody JSONObject obj) {
        return ResultUtils.success(managerServerFeign.invoke(obj));
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    ModelInstance refresh(@RequestBody JSONObject instanceInitial, @JwtTokenParser(key = "userId") String userId) {
        ModelInstance modelInstance = new ModelInstance();
        if (!instanceInitial.get("status").equals(2)) {
            JSONObject object = managerServerFeign.refresh((JSONObject) instanceInitial.get("refreshForm"));
            List<JSONObject> statesInit = (List) instanceInitial.get("behavior");
            JSONObject refreshData = (JSONObject) object.get("data");
            String assType = "";
            String value = "";
            Double doubleValue = 0.00;
            if (instanceInitial.get("modelName").equals("StatisticsCalculation")) {
                JSONObject assessTypeObj = (JSONObject) statesInit.get(0).getJSONArray("parameters").get(4);
                JSONObject udx = (JSONObject) assessTypeObj.getJSONObject("datasetItem").getJSONArray("UdxDeclarationNew").get(0);
                assType = udx.getStr("parameterValue");

            }


            List<JSONObject> outList = (List<JSONObject>) refreshData.get("outputs");
            Boolean isOutEmpty = true;
            //IF status are different, update the instance
            if (!instanceInitial.get("status").equals(refreshData.get("status"))) {
                for (JSONObject out : outList) {

                    DataItem resultDataItem = new DataItem();
                    if ((!out.get("url").equals(null)) && (!out.get("url").equals(""))) {


                        isOutEmpty = false;
                        AddDataItemDTO add = AddDataItemDTO.builder()
                                .contributorId(userId)
                                .name((String) out.get("event"))
                                .suffix((String) out.get("suffix"))
                                .value((String) out.get("url"))
                                .isInitial(true)
                                .isIntermediate(true)
                                .isParameter(false)
                                .isReproduced(false)
                                .privacy("public")
                                .build();
                        DataItem dataItem = new DataItem();
                        add.convertTo(dataItem);
                        resultDataItem = dataItemRepository.insert(dataItem);
                        refreshData.put("dataItem", resultDataItem);
                    } else {
                        isOutEmpty = true;
                    }

                    for (JSONObject state : statesInit) {
                        //如果output有值，塞入instance，并更新
                        if (state.get("name").equals(out.get("statename"))) {
                            List<JSONObject> outInstanceList = (List<JSONObject>) state.get("outputs");
                            for (JSONObject outInstance : outInstanceList) {
                                if (outInstance.get("name").equals(out.get("event"))) {
                                    outInstance.put("value", out.get("url"));
                                    outInstance.put("dataId", resultDataItem.getId());

                                    //IF assessment, get the content of txt,
                                    String assessment = "";
                                    if (instanceInitial.get("modelName").equals("StatisticsCalculation")) {
                                        SecureRandom secureRandom = new SecureRandom();
                                        byte[] seed = secureRandom.generateSeed(16);

                                        UUID uuid = UUID.nameUUIDFromBytes(seed);

                                        functionUtils.downloadFileFromUrl((String) out.get("url"), "D:\\Downloads\\Platform\\" + uuid + ".txt");
                                        assessment = functionUtils.readFile("D:\\Downloads\\Platform\\" + uuid + ".txt");
                                        if (assType.equals("Pearson")) {
                                            value = assessment.substring(1, assessment.indexOf(","));
                                            doubleValue = Double.parseDouble(value);

                                        } else {
                                            doubleValue = Double.parseDouble(assessment);
                                        }

                                        String.format("%.2f", doubleValue);
                                        JSONObject outAssessmentParam = (JSONObject) outInstance.getJSONObject("datasetItem");
                                        outAssessmentParam.put("assessmentValue", String.format("%.2f", doubleValue));
                                    }


                                }
                            }
                        }
                    }

                }
                //update model instance
                List<State> bmsqList = (List<State>) (List) statesInit;
                UpdateModelInstanceDTO updataInstance = UpdateModelInstanceDTO.builder()
                        .behavior(bmsqList)
                        .status((Integer) refreshData.get("status"))//new status
                        .build();
                if (isOutEmpty) {
                    //set the status = -1
                    updataInstance.setStatus(1);
                }
                modelInstance = modelInstanceService.updateInstance(instanceInitial.getStr("id"), updataInstance);

            }
        }


        //else no update the instance
        return modelInstance;
    }

    @SneakyThrows
    @RequestMapping(value = "/runtask", method = RequestMethod.POST)
    JsonResult runTask(@RequestParam("file") MultipartFile file, @JwtTokenParser(key = "name") String name) {
//       return ResultUtils.success(managerServerFeign.runtask(file, "111"));
//        String userId="123";
        String suffix = "." + FilenameUtils.getExtension(file.getOriginalFilename());
        File temp = File.createTempFile("temp", suffix);
        file.transferTo(temp);
        FileSystemResource resource = new FileSystemResource(temp);

        RestTemplate restTemplate = new RestTemplate();
        String urlStr = "http://172.21.213.245:8084/GeoModeling/task/runTask";
//        JSONObject form = new JSONObject();
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("file", resource);
        param.add("userName", name);
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "Mozilla/5.0");
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(param, headers);

        try {
            ResponseEntity<JSONObject> jsonObjectResponseEntity = restTemplate.exchange(urlStr, HttpMethod.POST, httpEntity, JSONObject.class);
            if (!jsonObjectResponseEntity.getStatusCode().is2xxSuccessful()) {
                throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
            }
            String result = jsonObjectResponseEntity.getBody().getStr("data");
            return ResultUtils.success(result);
        } catch (Exception e) {
            System.out.println(e);
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
    }


}
