package edu.njnu.opengms.container.domain.model;

import cn.hutool.json.JSONObject;
import com.google.common.collect.Lists;
import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.container.domain.model.dto.AddModelServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @Author ：Zhiyi
 * @Date ：2023/8/23 17:08
 * @modified By：
 * @version: 1.0.0
 */

@RestController
@RequestMapping(value = "/model_services")
public class ModelController {
    @Autowired
    ModelRepository modelRepository;

//    @Autowired
//    RemotePortalService remotePortalService;

    //create one project
    @RequestMapping(value = "", method = RequestMethod.POST)
    public JsonResult create(@RequestBody AddModelServiceDTO add) {
        ModelService model;
        JSONObject data;

//        if(add.getType().equals("service")){
//            data = remotePortalService.getModelInfo(add.getServiceId());
//            model = ModelService.builder()
//                    .type("service")
//                    .privacy("public")
//                    .serviceId(add.getServiceId())
//                    .snapshot(add.getSnapshot())
//                    .license(add.getLicense())
//                    .contributorId("OpenGMS_platform")
//                    .behavior( (List< State >) data.get("behavior"))
//                    .description(data.getStr("description"))
//                    .name(data.getStr("name"))
//                    .build();
//
//            return ResultUtils.success(modelRepository.insert(model));
//        }
        return ResultUtils.success();
    }

//    @RequestMapping(value = "/test", method = RequestMethod.POST)
//    public void createPortal(@RequestBody List<String> add) {
//        for(int i=0; i <add.size();i++){
//            ModelService model;
//            JSONObject data;
////            Optional<JSONObject> data = Optional.ofNullable(remotePortalService.getModelInfo(add.get(i)));
//                if(!modelRepository.findByServiceId(add.get(i)).isPresent()) {
//                    data = remotePortalService.getModelInfo(add.get(i));
//                    if (data != null) {
//                        model = ModelService.builder()
//                                .type("service")
//                                .privacy("public")
//                                .serviceId(add.get(i))
//                                .contributorId("OpenGMS_platform")
//                                .behavior((ModelBehavior) data.get("behavior"))
//                                .description(data.getStr("description"))
//                                .name(data.getStr("name"))
//                                .build();
//
//                        modelRepository.insert(model);
//                        System.out.print("______________" + add.get(i) + "  ______________");
//                    }
//                }
//        }
//    }

    @RequestMapping(value = "/getPublicModels/{privacy}/{currentPage}/{pageSize}", method = RequestMethod.GET)
    public JsonResult getModelsByPrivacy(@PathVariable int currentPage, @PathVariable int pageSize) {
        PageRequest pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<ModelService> modelList = modelRepository.findByPrivacy("public", pageable);
        return ResultUtils.success(modelList);
    }

    @RequestMapping(value = "/allPublic", method = RequestMethod.GET)
    public JsonResult getAllPublicModels(@PathVariable String userId) {
         List<ModelService> modelList = modelRepository.findAll();
        return ResultUtils.success(modelList);
    }


    @RequestMapping(value = "/getMyodels/{currentPage}/{pageSize}", method = RequestMethod.GET)
    public JsonResult getMyModels(@PathVariable String userId) {
        return ResultUtils.success();
    }

    @RequestMapping(value = "/getPublicModelListByIgnoreName/{text}", method = RequestMethod.GET)
    public JsonResult getPublicModelListByIgnoreName(@PathVariable String userId,@PathVariable String text) {
//        PageRequest pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        return ResultUtils.success(modelRepository.findByNameContainsIgnoreCase(text));
    }


    @RequestMapping (value = "/listVOByIds", method = RequestMethod.GET)
    public JsonResult listVOByIds(@RequestParam ("ids") List<String> ids) {
        return ResultUtils.success(StreamSupport.stream(modelRepository.findAllById(ids).spliterator(), true).map(service -> {
//            ModelServiceVO vo = new ModelServiceVO();
//            CopyUtils.copyProperties(service, vo);
            return service;
        }).collect(Collectors.toList()));
    }

    @RequestMapping (value = "/listByIds", method = RequestMethod.GET)
    public JsonResult listByIds(@RequestParam("ids")List<String>ids){
        return ResultUtils.success(Lists.newArrayList(modelRepository.findAllById(ids)));
    }

    @RequestMapping (value = "/all", method = RequestMethod.GET)
    public JsonResult findAll(){
        return ResultUtils.success(modelRepository.findAll());
    }


}
