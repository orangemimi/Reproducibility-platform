package edu.njnu.opengms.r2.domain.model;

import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
import edu.njnu.opengms.r2.domain.model.dto.AddModelServiceDTO;
import edu.njnu.opengms.r2.domain.model.support.State;
import edu.njnu.opengms.r2.remote.RemotePortalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static edu.njnu.opengms.r2.utils.Utils.*;

/**
 * @Author ：Zhiyi
 * @Date ：2023/8/23 17:08
 * @modified By：
 * @version: 1.0.0
 */

@RestController
@RequestMapping(value = "/models")
public class ModelController {
    @Autowired
    ModelRepository modelRepository;

    @Autowired
    RemotePortalService remotePortalService;

    //create one project
    @RequestMapping(value = "", method = RequestMethod.POST)
    public JsonResult create(@RequestBody AddModelServiceDTO add) {
        Model model;
        JSONObject data;

//        if(add.getType().equals("service")){
//            data = remotePortalService.getModelInfo(add.getServiceId());
//            model = Model.builder()
//                    .type("service")
//                    .privacy("public")
//                    .serviceId(add.getServiceId())
//
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

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public void createPortal(@RequestBody List<String> add) {
        for (int i = 0; i < add.size(); i++) {
            Model model;
            JSONObject data;
//            Optional<JSONObject> data = Optional.ofNullable(remotePortalService.getModelInfo(add.get(i)));
            if (!modelRepository.findByServiceId(add.get(i)).isPresent()) {
                data = remotePortalService.getModelInfo(add.get(i));
                if (data != null) {
                    model = Model.builder()
                            .type("service")
                            .privacy("public")
                            .serviceId(add.get(i))
                            .md5(data.getStr("md5"))
                            .contributorId("OpenGMS_platform")
                            .behavior((List<State>) data.get("behavior"))
                            .description(data.getStr("description"))
                            .name(data.getStr("name"))
                            .build();

                    modelRepository.insert(model);
                    System.out.print("______________" + add.get(i) + "  ______________");
                }
            }
        }
    }

    @RequestMapping(value = "/local", method = RequestMethod.POST)
    public void createModelServiceFromLocal(JSONObject jsonObject) {
        String mdl = jsonObject.getStr("mdl");
        String md5 = jsonObject.getStr("md5");
        if (!modelRepository.findByMd5(md5).isPresent()) {
            JSONObject mdlJson = documentToJSONObject(mdl);
            JSONObject modelClass = checkMdlJson(mdlJson);
            if (mdlJson.containsKey("ModelClass")) {
                mdlJson.getJSONArray("ModelClass").remove(0);
                mdlJson.getJSONArray("ModelClass").add(modelClass);
            }
            JSONObject data = convertMdl(mdlJson);
            if (data != null) {
                Model model = Model.builder()
                        .type("service")
                        .privacy(jsonObject.getStr("privacy"))//目前就两种，一种public 一种privacy
                        .md5(data.getStr("md5"))
                        .contributorId("OpenGMS_platform_test")
                        .behavior((List<State>) data.get("behavior"))
                        .description(jsonObject.getStr("description"))
                        .name(jsonObject.getStr("name"))
                        .build();

                modelRepository.insert(model);
                System.out.print("____________________________");

            }
        }

    }

    @RequestMapping(value = "/getPublicModels/{privacy}/{currentPage}/{pageSize}", method = RequestMethod.GET)
    public JsonResult listModelsByPrivacy(@PathVariable int currentPage, @PathVariable int pageSize) {
        PageRequest pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Model> modelList = modelRepository.findByPrivacy("public", pageable);
        return ResultUtils.success(modelList);
    }

    @RequestMapping(value = "/allPublic", method = RequestMethod.GET)
    public JsonResult getAllPublicModels() {
        List<Model> modelList = modelRepository.findAll();
        return ResultUtils.success(modelList);
    }

    @RequestMapping(value = "/getModelById/{id}", method = RequestMethod.GET)
    public JsonResult getModelById(@PathVariable String id) {
        Optional<Model> model = modelRepository.findById(id);
        if (model.isPresent()) {
            return ResultUtils.success(model.get());
        } else {
            return ResultUtils.error(404, "Model not found");
        }
    }


    @RequestMapping(value = "/getMyodels/{currentPage}/{pageSize}", method = RequestMethod.GET)
    public JsonResult getMyModels(@JwtTokenParser(key = "userId") String userId) {
        return ResultUtils.success();
    }

    @RequestMapping(value = "/getPublicModelListByIgnoreName/{text}", method = RequestMethod.GET)
    public JsonResult getPublicModelListByIgnoreName(@JwtTokenParser(key = "userId") String userId, @PathVariable String text) {
//        PageRequest pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        return ResultUtils.success(modelRepository.findByNameContainsIgnoreCase(text));
    }

    @RequestMapping(value = "/invoke", method = RequestMethod.POST)
    JsonResult invoke(@RequestBody JSONObject obj) {
        return ResultUtils.success();
    }

    @RequestMapping(value = "/getRecord", method = RequestMethod.POST)
    JsonResult getResult(@RequestBody JSONObject data) {

        return ResultUtils.success();
    }


}
