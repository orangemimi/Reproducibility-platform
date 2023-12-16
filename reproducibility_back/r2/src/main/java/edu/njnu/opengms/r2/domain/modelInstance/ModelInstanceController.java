package edu.njnu.opengms.r2.domain.modelInstance;

import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
import edu.njnu.opengms.r2.domain.model.Model;
import edu.njnu.opengms.r2.domain.model.ModelRepository;
import edu.njnu.opengms.r2.domain.modelInstance.dto.AddModelInstanceDTO;
import edu.njnu.opengms.r2.domain.modelInstance.dto.UpdateModelInstanceDTO;
import edu.njnu.opengms.r2.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @Author ：Zhiyi
 * @Date ：2023/8/23 17:08
 * @modified By：
 * @version: 1.0.0
 */

@RestController
@RequestMapping(value = "/model_instances")
public class ModelInstanceController {
    @Autowired
    ModelInstanceRepository modelInstanceRepository;

    @Autowired
    ModelRepository modelRepository;

    @Autowired
    UserService userService;


    //create one project
    @RequestMapping(value = "", method = RequestMethod.POST)
    public JsonResult create(@RequestBody AddModelInstanceDTO add,@JwtTokenParser(key = "userId") String userId) {
        ModelInstance modelInstance = new ModelInstance();

        add.convertTo(modelInstance);
        modelInstance.setExecutorId(userId);

        return ResultUtils.success( modelInstanceRepository.insert(modelInstance));

    }

    @RequestMapping(value = "/inscenario/{scenarioId}/{modelId}", method = RequestMethod.GET)
    public JsonResult getInstancesByScenarioIdAndModelIs(@PathVariable("modelId") String modelId,@PathVariable("scenarioId") String scenarioId) {
        List<ModelInstance> modelInstanceList = modelInstanceRepository.findAllByScenarioIdAndModelId(scenarioId,modelId);

        return ResultUtils.success(modelInstanceList) ;
<<<<<<< Updated upstream

=======
    }
    //新加，用来读取绑定的实例
    @RequestMapping(value = "/getBoundInstances", method = RequestMethod.POST)
    public JsonResult getModelInstancesByIds(@RequestBody List<String> modelInstanceIds) {

        return ResultUtils.success(getAllInstances(modelInstanceIds));
>>>>>>> Stashed changes
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public JsonResult getInstanceById(@PathVariable("id") String id) {
        return ResultUtils.success( modelInstanceRepository.findById(id) );
    }

    @RequestMapping(value = "/{id}", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.PATCH)
    public JsonResult updateScenarioInfo(@PathVariable("id") String id, @RequestBody UpdateModelInstanceDTO update) {
        ModelInstance modelInstance = modelInstanceRepository.findById(id).orElseThrow(MyException::noObject);
        update.updateTo(modelInstance);
        return ResultUtils.success(modelInstanceRepository.save( modelInstance));
    }

   public List<JSONObject> getAllInstances(List<String> modelInstanceIds){
        List<JSONObject> modelInstanceList = new ArrayList<>();
        for (String id : modelInstanceIds) {
            ModelInstance modelInstance = modelInstanceRepository.findById(id).orElseThrow(MyException::noObject);
            JSONObject object = new JSONObject();
            Optional.ofNullable(modelInstance)
                    .map(x -> x.getModelId())
                    .map(x -> {
                        Model model = modelRepository.findById(x).orElseThrow(MyException::noObject);

                        object.put("modelDescription",model.getDescription());
                        object.put("name",modelInstance.getName());
                        object.put("id",modelInstance.getId());
                        object.put("modelName",modelInstance.getModelName());
                        object.put("modelId",modelInstance.getModelId());
                        object.put("behavior",modelInstance.getBehavior());
                        object.put("status",modelInstance.getStatus());
                        object.put("executorId",modelInstance.getExecutorId());
                        object.put("scenarioId",modelInstance.getScenarioId());
                        object.put("refreshForm",modelInstance.getRefreshForm());
                        object.put("isReproduced",modelInstance.getIsReproduced());
                        object.put("createTime",modelInstance.getCreateTime());
                        object.put("updateTime",modelInstance.getUpdateTime());
                        return object;
                    })
                    .orElseGet( () -> {
                        return null;
                    });

            Optional.ofNullable(modelInstance)
                    .map(x -> x.getExecutorId())
                    .map(x -> {
                        JSONObject user = userService.getUserInfoById(x);
                        object.put("executorName",user.get("name"));
                        return object;
                    })
                    .orElseGet( () -> {
                        return null;
                    });
            if (modelInstance != null) {
                modelInstanceList.add(object);
            }
        }
        return modelInstanceList;
    }




}
