package edu.njnu.opengms.r2.domain.model;

import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
import edu.njnu.opengms.r2.domain.model.dto.AddModelServiceDTO;
import edu.njnu.opengms.r2.domain.model.support.State;
import edu.njnu.opengms.r2.domain.user.User;
import edu.njnu.opengms.r2.domain.user.UserRepository;
import edu.njnu.opengms.r2.remote.RemotePortalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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
    UserRepository userRepository;

    @Autowired
    RemotePortalService remotePortalService;



    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public void createPortal(@RequestBody List<String> add) {
        for (int i = 0; i < add.size(); i++) {
            AddModelServiceDTO model;
            Model newModel = new Model();
            JSONObject data;
//            Optional<JSONObject> data = Optional.ofNullable(remotePortalService.getModelInfo(add.get(i)));
            if (!modelRepository.findByServiceId(add.get(i)).isPresent()) {
                data = remotePortalService.getModelInfo(add.get(i));
                if (data != null) {
                    model = AddModelServiceDTO.builder()
                            .type("service")
                            .privacy("public")
                            .serviceId(add.get(i))
                            .md5(data.getStr("md5"))
                            .contributorId("OpenGMS_platform")
                            .behavior((List<State>) data.get("behavior"))
                            .description(data.getStr("description"))
                            .name(data.getStr("name"))
                            .build();
                    model.convertTo(newModel);

                    modelRepository.insert(newModel);
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

//    @RequestMapping(value = "/getPublicModels/{privacy}/{currentPage}/{pageSize}", method = RequestMethod.GET)
//    public JsonResult listModelsByPrivacy(@PathVariable int currentPage, @PathVariable int pageSize) {
//        PageRequest pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
//        Page<Model> modelList = modelRepository.findByPrivacy("public", pageable);
//        return ResultUtils.success(modelList);
//    }

    @RequestMapping(value = "/getPublicModels", method = RequestMethod.POST)
    public JsonResult listModelsByPrivacy(@RequestBody JSONObject jsonObject) {
        int currentPage = jsonObject.getInt("currentPage");
        int pageSize = jsonObject.getInt("pageSize");
        String key = jsonObject.getStr("key");
        String privacy = jsonObject.getStr("privacy");

        PageRequest pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Model> modelList;
        if(!key.equals("")){
            Model modelquery = Model.builder()
                    .name(key)
                    .privacy(privacy)
                    .build();

            ExampleMatcher matcher = ExampleMatcher.matching()
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                    .withIgnoreCase(true) //改变默认大小写忽略方式：忽略大小写
                    .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
                    .withMatcher("privacy",ExampleMatcher.GenericPropertyMatchers.exact());

            modelList = modelRepository.findAll(Example.of(modelquery,matcher),pageable);
//            Page<Model> newModels = (Page<Model>) modelList.stream().filter(s->s.getPrivacy().equals("public")).collect(Collectors.toList());
            return ResultUtils.success(modelList);
        } else{
            modelList = modelRepository.findByPrivacy("public", pageable);
            return ResultUtils.success(modelList);
        }
    }


    @RequestMapping(value = "/allPublic", method = RequestMethod.GET)
    public JsonResult getAllPublicModels() {
        List<Model> modelList = modelRepository.findAll();
        return ResultUtils.success(modelList);
    }

    @RequestMapping(value = "/getModelById/{id}", method = RequestMethod.GET)
    public JsonResult getModelById(@JwtTokenParser(key = "userId") String userId, @PathVariable String id) {
        Optional<Model> model = modelRepository.findById(id);
        if (model.isPresent()) {
            return ResultUtils.success(model.get());
        } else {
            return ResultUtils.error(404, "Model not found");
}
    }

    @RequestMapping(value = "/getAssessmentMethod", method = RequestMethod.GET)
    public JsonResult getModelById(@JwtTokenParser(key = "userId") String userId) {
        Optional<Model> model = modelRepository.findById("65a0a40a1e8e312ef974d82b");
        if (model.isPresent()) {
            return ResultUtils.success(model.get());
        } else {

            return ResultUtils.error(404, "Model not found");
        }
    }

@RequestMapping(value = "/my", method = RequestMethod.GET)
public JsonResult getMyModels(@JwtTokenParser(key = "userId") String userId) {
        User user = userRepository.findById(userId).orElseThrow(MyException::noObject);
        List<Model> modelList=  modelRepository.findAllByIdInOrContributorId(user.getModelList(),userId);
        return ResultUtils.success(modelList);
        }
    JsonResult invoke(@RequestBody JSONObject obj) {
        return ResultUtils.success();
    }

    @RequestMapping(value = "/getRecord", method = RequestMethod.POST)
    JsonResult getResult(@RequestBody JSONObject data) {

        return ResultUtils.success();
    }

    @RequestMapping(value = "/newCodeModel", method = RequestMethod.POST)
    JsonResult newCodeModel(@JwtTokenParser(key = "userId") String userId,@RequestBody JSONObject newModel){
        try {
            // 从JSON数据中提取必要的字段
            String name = newModel.get("name").toString();
            String description = newModel.get("description").toString();
            String privacy = newModel.get("privacy").toString();
            String content = newModel.get("content").toString();

            // 创建一个新的Model对象
            Model model = Model.builder()
                    .name(name)
                    .description(description)
                    .type("code")
                    .privacy(privacy)
                    .contributorId(userId)
                    .content(content)
                    .build();

            model =modelRepository.insert(model);

            String insertedId = model.getId();

            return ResultUtils.success(insertedId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error("Save Error.");
        }
    }


}
