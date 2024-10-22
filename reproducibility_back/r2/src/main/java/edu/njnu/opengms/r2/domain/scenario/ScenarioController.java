package edu.njnu.opengms.r2.domain.scenario;


import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
import edu.njnu.opengms.r2.domain.scenario.dto.AddScenarioDTO;
import edu.njnu.opengms.r2.domain.scenario.dto.UpdateScenarioContainerIdDTO;
import edu.njnu.opengms.r2.domain.scenario.dto.UpdateScenarioDTO;
import edu.njnu.opengms.r2.domain.scenario.dto.UpdateScenarioInstanceResourceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2021/4/13 14:15
 * @modified By：
 * @version: 1.0.0
 */
@RestController
@RequestMapping("/scenario")
public class ScenarioController {
    @Autowired
    ScenarioService scenarioService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public JsonResult getScenarioInfoBId(@PathVariable("id") String id) {
        return ResultUtils.success(scenarioService.getScenario(id));
    }

    @RequestMapping(value = "/r/{scenarioId}", method = RequestMethod.GET)
    public JsonResult getScenarioInfoByInitialScenarioId(@PathVariable("scenarioId") String scenarioId) {

        return ResultUtils.success(scenarioService.getScenarioByInitialScenarioId(scenarioId));
    }


    @RequestMapping(value = "/project/{projectId}", method = RequestMethod.GET)
    public JsonResult getAllScenariosByProjectId(@PathVariable("projectId") String projectId) {
        return ResultUtils.success(scenarioService.getScenariosByProjectId(projectId));
    }

    // 保存integrateTask的flowData
    @RequestMapping(value = "/updateWorkflowInfo/{projectId}", method = RequestMethod.POST)
    public JsonResult updateWorkflowInfo(@PathVariable("projectId") String projectId,@RequestParam("flowData") String flowData) {
        Scenario integrateTaskScenario = scenarioService.updateScenarioFlowData(projectId,flowData);
        return ResultUtils.success(integrateTaskScenario);
    }

    @RequestMapping(value = "/{id}", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.PATCH)
    public JsonResult updateScenarioInfo(@PathVariable("id") String id, @RequestBody UpdateScenarioDTO update) {
        return ResultUtils.success(scenarioService.updateScenario(id, update));
    }
//    @RequestMapping(value = "/r/{id}", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.PATCH)
//    public JsonResult updateScenarioInfo(@PathVariable("id") String id, @RequestBody UpdateForkedScenarioDTO update) {
//        return ResultUtils.success(scenarioService.updateScenario(id, update));
//    }

    @RequestMapping(value = "/instance/{id}", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.PATCH)
    public JsonResult updateScenarioInstance(@JwtTokenParser(key = "userId") String userId, @PathVariable("id") String id, @RequestBody UpdateScenarioInstanceResourceDTO update) {

        return ResultUtils.success(scenarioService.updateScenarioInstance(id, update));
    }

    @RequestMapping(value = "/{initialScenatioId}", method = RequestMethod.POST)
    public JsonResult saveScenarioInfo(@RequestBody AddScenarioDTO add, @JwtTokenParser(key = "userId") String userId, @PathVariable("initialScenatioId") String initialScenatioId) {
        return ResultUtils.success(scenarioService.saveScenario(add, userId, initialScenatioId));
    }

    @RequestMapping(value = "/resources/{id}", method = RequestMethod.PATCH)
    public JsonResult updateResourceCollection(@PathVariable("id") String id,
                                               @JwtTokenParser(key = "userId") String userId,
                                               @RequestBody JSONObject update) {
        return ResultUtils.success(scenarioService.updateresourceCollection(id, userId, update));
    }

    @RequestMapping(value = "/containerId", method = RequestMethod.POST)
    public JsonResult updateContainerId(@RequestParam("scenarioId") String scenarioId,@RequestParam("containerId") String containerId) {
        UpdateScenarioContainerIdDTO updateScenarioContainerIdDTO =new UpdateScenarioContainerIdDTO(containerId);
        return ResultUtils.success(scenarioService.updateScenarioContainerId(scenarioId, updateScenarioContainerIdDTO));
    }
}
