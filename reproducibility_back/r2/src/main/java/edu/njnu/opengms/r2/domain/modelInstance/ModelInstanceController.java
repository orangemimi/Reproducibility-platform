package edu.njnu.opengms.r2.domain.modelInstance;

import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
import edu.njnu.opengms.r2.domain.modelInstance.dto.AddModelInstanceDTO;
import edu.njnu.opengms.r2.domain.modelInstance.dto.UpdateModelInstanceDTO;
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


    //create one project
    @RequestMapping(value = "", method = RequestMethod.POST)
    public JsonResult create(@RequestBody AddModelInstanceDTO add,@JwtTokenParser(key = "userId") String userId) {
        ModelInstance modelInstance = new ModelInstance();

        add.convertTo(modelInstance);
        modelInstance.setExecutorId(userId);

        return ResultUtils.success( modelInstanceRepository.insert(modelInstance));

    }

    @RequestMapping(value = "/{scenarioId}/{modelId}", method = RequestMethod.GET)
    public JsonResult getInstancesByScenarioIdAndModelIs(@PathVariable("modelId") String modelId,@PathVariable("scenarioId") String scenarioId) {
        List<ModelInstance> modelInstanceList = modelInstanceRepository.findAllByScenarioId(scenarioId);
        List<ModelInstance> newArray = new ArrayList<>();

        for (ModelInstance modelInstance : modelInstanceList){
            if (modelInstance.getModel().getId().equals(modelId)){
                newArray.add(modelInstance);
            }
        }
        return ResultUtils.success(newArray) ;

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




}