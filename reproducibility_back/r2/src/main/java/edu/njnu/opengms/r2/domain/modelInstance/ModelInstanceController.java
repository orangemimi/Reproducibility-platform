package edu.njnu.opengms.r2.domain.modelInstance;

import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
import edu.njnu.opengms.r2.domain.model.ModelRepository;
import edu.njnu.opengms.r2.domain.modelInstance.dto.AddModelInstanceDTO;
import edu.njnu.opengms.r2.domain.modelInstance.dto.UpdateModelInstanceDTO;
import edu.njnu.opengms.r2.domain.user.UserService;
import edu.njnu.opengms.r2.utils.FunctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    FunctionUtils functionUtils;

    @Autowired
    ModelInstanceService modelInstanceService;


    //create one project
    @RequestMapping(value = "", method = RequestMethod.POST)
    public JsonResult create(@RequestBody AddModelInstanceDTO add,@JwtTokenParser(key = "userId") String userId) {
        ModelInstance modelInstance = new ModelInstance();

        add.convertTo(modelInstance);
        modelInstance.setExecutorId(userId);

        return ResultUtils.success( modelInstanceRepository.insert(modelInstance));

    }

    @RequestMapping(value = "/inscenario/{scenarioId}/{modelId}/{isReproduced}", method = RequestMethod.GET)
    public JsonResult getInstancesByScenarioIdAndModelIs(@PathVariable("modelId") String modelId,
                                                         @PathVariable("scenarioId") String scenarioId,
                                                         @PathVariable("isReproduced") Boolean isReproduced,
                                                         @JwtTokenParser(key = "userId") String userId) {
        List<ModelInstance> modelInstanceList = new ArrayList<>();
        if(modelId.equals("allInstanceInScenario")){
            modelInstanceList = modelInstanceRepository.findAllByScenarioId(scenarioId);

        }else{
            modelInstanceList = modelInstanceRepository.findAllByScenarioIdAndModelIdAndIsReproducedAndExecutorId(scenarioId,modelId,isReproduced,userId);
        }
        List<JSONObject> objectList = new ArrayList<>();
        for (ModelInstance modelInstance:modelInstanceList){
            if (modelInstance != null) {
                JSONObject obj =functionUtils.getFullModelInstance(modelInstance);
                objectList.add(obj);
            }

        }
        return ResultUtils.success(objectList) ;
    }
    @RequestMapping(value = "/inscenario/assessment/{scenarioId}", method = RequestMethod.GET)
    public JsonResult getAssessmentInstancesByScenarioIdAndModelIs(@PathVariable("scenarioId") String scenarioId,@JwtTokenParser(key = "userId") String userId) {
        List<ModelInstance> modelInstanceList = modelInstanceRepository.findAllByScenarioIdAndModelIdAndIsReproducedAndExecutorId(scenarioId,"65a0a40a1e8e312ef974d82b",false,userId);
        List<JSONObject> objectList = new ArrayList<>();
        for (ModelInstance modelInstance:modelInstanceList){
            if (modelInstance != null) {
                JSONObject obj =functionUtils.getFullModelInstance(modelInstance);
                objectList.add(obj);
            }

        }
        return ResultUtils.success(objectList) ;
    }

    //新加，用来读取绑定的实例
    @RequestMapping(value = "/getBoundInstances", method = RequestMethod.POST)
    public JsonResult getModelInstancesByIds(@RequestBody List<String> modelInstanceIds) {
        return ResultUtils.success(functionUtils.getAllInstances(modelInstanceIds));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public JsonResult getInstanceById(@PathVariable("id") String id) {
        return ResultUtils.success( modelInstanceRepository.findById(id) );
    }

    @RequestMapping(value = "/{id}", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.PATCH)
    public JsonResult updateInstanceInfo(@PathVariable("id") String id, @RequestBody UpdateModelInstanceDTO update) {
        return  ResultUtils.success(modelInstanceService.updateInstance(id,update));
    }






}
