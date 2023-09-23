package edu.njnu.opengms.r2.remote;

import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.enums.ResultEnum;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * @Author ：Zhiyi
 * @Date ：2020/12/29 11:01
 * @modified By：
 * @version: 1.0.0
 */
@RestController
@RequestMapping("/dataContainer")
public class RemoteDataContainerController {
    @Autowired
    DataContainerService dataContainerService;


    @RequestMapping (value = "/uploadFileForm", method = RequestMethod.POST)
    public JsonResult uploadMultipleData(HttpServletRequest request, @JwtTokenParser(key = "name") String name, @JwtTokenParser() String id) throws IOException, ServletException {
        MultipartHttpServletRequest request1 = (MultipartHttpServletRequest) request;
        Collection<Part> parts = request1.getParts();
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        for (Part part : parts) {
            String header = part.getHeader("Content-Disposition");
            String filename2 = header.substring(header.indexOf("filename=\"") + 10,header.lastIndexOf("\""));//filename=" (整个字符串长度为10，所以要加10)
            // 获取文件名
            String fileName = part.getName();
            //  获取文件后缀名
            String suffix ="." + FilenameUtils.getExtension(filename2);
            File file=File.createTempFile(part.getName(),suffix);//创建临时文件
            FileUtils.copyInputStreamToFile(part.getInputStream(),file);
            FileSystemResource fileSystemResource=new FileSystemResource(file);
            form.add("ogmsdata",fileSystemResource);
        }
        form.add("serverNode", "china");
        form.add("userId", id);
        form.add("name", name);
        form.add("origination","reproducibility");
        return ResultUtils.success(dataContainerService.upload(form));
    }

    //    无需配置文件的上传接口
    @RequestMapping(value = "/uploadSingle", method = RequestMethod.POST)
    public JsonResult uploadData(HttpServletRequest request, @JwtTokenParser(key = "name") String name, @JwtTokenParser() String id) throws IOException, ServletException {
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        Part part = multiRequest.getPart("file");
        String header = part.getHeader("Content-Disposition");
        String filename2 = header.substring(header.indexOf("filename=\"") + 10, header.lastIndexOf("\""));//filename=" (整个字符串长度为10，所以要加10)

        String filename = FilenameUtils.getBaseName(filename2);
        //  获取文件后缀名
        String suffix = "." + FilenameUtils.getExtension(filename2);
        File file = File.createTempFile(part.getName(), suffix);//创建临时文件
        FileUtils.copyInputStreamToFile(part.getInputStream(), file);

        FileSystemResource resource = new FileSystemResource(file);      //临时文件
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("serverNode", "china");
        form.add("userId", id);
        form.add("datafile", resource);
        form.add("name", filename);
        form.add("origination", "reproducibility");

//        return ResultUtils.success(dataContainerService.upload(file,id,name));
        return ResultUtils.success(dataContainerService.upload(form));
    }


    @RequestMapping(value = "/uploadAction", method = RequestMethod.POST)
    public JsonResult uploadDataAction(@RequestParam("file") HttpServletRequest request, @JwtTokenParser(key = "name") String name, @JwtTokenParser() String id) throws IOException, ServletException {
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        Part part = multiRequest.getPart("file");
        String header = part.getHeader("Content-Disposition");
        String filename2 = header.substring(header.indexOf("filename=\"") + 10, header.lastIndexOf("\""));//filename=" (整个字符串长度为10，所以要加10)

        String filename = FilenameUtils.getBaseName(filename2);
        //  获取文件后缀名
        String suffix = "." + FilenameUtils.getExtension(filename2);
        File file = File.createTempFile(part.getName(), suffix);//创建临时文件
        FileUtils.copyInputStreamToFile(part.getInputStream(), file);

        FileSystemResource resource = new FileSystemResource(file);      //临时文件
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("serverNode", "china");
        form.add("userId", id);
        form.add("datafile", resource);
        form.add("name", filename);
        form.add("origination", "reproducibility");

//        return ResultUtils.success(dataContainerService.upload(file,id,name));
        return ResultUtils.success(dataContainerService.upload(form));
    }



    @RequestMapping(value = "/download/{uid}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> download(@PathVariable("uid") String uid) {
        return dataContainerService.download(uid);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public JsonResult getDataServices() {
        String urlStr ="http://111.229.14.128:8898/onlineNodesAllPcs?token=fdtwTxlnhka8jY66lOT%2BkKutgZHnvi4NlnDc7QY5jR4%3D&type=Processing";
//        JSONObject form = new JSONObject();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json");
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONObject>  jsonObjectResponseEntity = restTemplate.exchange(urlStr, HttpMethod.GET,requestEntity, JSONObject.class);
        if (!jsonObjectResponseEntity.getStatusCode().is2xxSuccessful()) {
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }

        JSONObject result = jsonObjectResponseEntity.getBody().getJSONObject("servicesAvailable").getJSONObject("availablePcs");

        return ResultUtils.success(result);
    }

//    @RequestMapping(value = "/newService", method = RequestMethod.POST)
//    public JsonResult createNewService(AddDataServiceCodeDTO serviceForm , @JwtTokenParser(key = "userId") String userId) throws IOException, ServletException {
//
//        String token = serviceForm.getToken();
//
//        return ResultUtils.success();
//    }

    @RequestMapping(value = "/dataService/getData", method = RequestMethod.POST)
    public JsonResult getDataService( @RequestBody JSONObject jsonObject) {
        String serviceId = jsonObject.getStr("dataServiceId");
        String token = jsonObject.getStr("token");
        String type = jsonObject.getStr("type");
        return ResultUtils.success( dataContainerService.getDataService(serviceId,token,type));
    }


    @RequestMapping(value = "/dataService/findData", method = RequestMethod.POST)
    public JsonResult findData(@RequestBody JSONObject jsonObject) {
        String token = jsonObject.getStr("token");
        String name = jsonObject.getStr("name");
        return ResultUtils.success(dataContainerService.findData(token, name));
    }

    @RequestMapping(value = "/dataService/getAllProcessing", method = RequestMethod.GET)
    public JsonResult findAllProcessing(@RequestParam("token") String token) {
        return ResultUtils.success(dataContainerService.findAllProcessing(token));
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public JsonResult upload(@RequestParam("datafile") MultipartFile multipartFile, @RequestParam("name") String name) throws IOException {
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        String suffix="."+ FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        File temp = File.createTempFile("temp", suffix);
        multipartFile.transferTo(temp);
        FileSystemResource resource = new FileSystemResource(temp);
        multiValueMap.add("datafile", resource);
        multiValueMap.add("name", name);


        return ResultUtils.success(dataContainerService.upload(multiValueMap));
    }

}
