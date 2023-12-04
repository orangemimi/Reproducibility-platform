package edu.njnu.opengms.r2.remote;

import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.enums.ResultEnum;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
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

//    @Autowired
//    IntegrateTaskInstanceRepository integrateTaskInstanceRepository;
//
//    @Autowired
//    FileItemRepository fileItemRepository;

    @Value("${managerServerIpAndPort}")
    private String managerServerIpAndPort;

//    @Value("${wzpIpAndPort}:8084")
//    String wzpIpAndPort;

    @RequestMapping(value = "/getServiceTask/{pid}", method = RequestMethod.GET)
        JsonResult getServiceTask(@PathVariable String pid){
        System.out.println("-------------");
        return ResultUtils.success(managerServerFeign.getServiceTask(pid));
    }

    @RequestMapping(value = "/initTask", method = RequestMethod.POST)//step 1
    JsonResult createTask(@RequestBody JSONObject serviceJson){
//        ip port pid username
        return ResultUtils.success(managerServerFeign.createTask(serviceJson));
    }

    @RequestMapping(value = "/invoke", method = RequestMethod.POST)
    JsonResult getServiceTask(@RequestBody JSONObject obj){
        return ResultUtils.success(managerServerFeign.invoke(obj));
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    JsonResult refresh(@RequestBody JSONObject obj){

        return ResultUtils.success(managerServerFeign.refresh(obj));
    }

    @SneakyThrows
    @RequestMapping(value = "/runtask", method = RequestMethod.POST)
    JsonResult runTask(@RequestParam("file") MultipartFile file,@JwtTokenParser(key="name") String name){
//       return ResultUtils.success(managerServerFeign.runtask(file, "111"));
//        String userId="123";
        String suffix="."+ FilenameUtils.getExtension(file.getOriginalFilename());
        File temp= File.createTempFile("temp",suffix);
        file.transferTo(temp);
        FileSystemResource resource = new FileSystemResource(temp);

        RestTemplate restTemplate = new RestTemplate();
        String urlStr ="http://172.21.213.245:8084/GeoModeling/task/runTask";
//        JSONObject form = new JSONObject();
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("file", resource);
        param.add("userName",name);
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
        }catch (Exception e) {
            System.out.println(e);
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
    }



}
