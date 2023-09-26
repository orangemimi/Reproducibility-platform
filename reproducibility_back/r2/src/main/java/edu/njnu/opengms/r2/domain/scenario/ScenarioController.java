package edu.njnu.opengms.r2.domain.scenario;


import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
import edu.njnu.opengms.r2.domain.scenario.dto.AddScenarioDTO;
import edu.njnu.opengms.r2.domain.scenario.dto.UpdateScenarioDTO;
import edu.njnu.opengms.r2.domain.scenario.dto.UpdateScenarioInstanceDTO;
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

        return ResultUtils.success( scenarioService.getScenario(id) );
    }




    @RequestMapping(value = "/project/{projectId}", method = RequestMethod.GET)
    public JsonResult getScenariosByProjectId(@PathVariable("projectId") String projectId) {
        return ResultUtils.success( scenarioService.getScenariosByProjectId(projectId) );
    }

    @RequestMapping(value = "/{id}", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.PATCH)
    public JsonResult updateScenarioInfo(@PathVariable("id") String id, @RequestBody UpdateScenarioDTO update) {
        return ResultUtils.success(scenarioService.updateScenario(id, update));
    }

    @RequestMapping(value = "/instance/{id}", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.PATCH)
    public JsonResult updateScenarioInstance(  @JwtTokenParser(key="userId") String userId,@PathVariable("id") String id, @RequestBody UpdateScenarioInstanceDTO update) {

        return ResultUtils.success(scenarioService.updateScenarioInstance(id, update));
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public JsonResult saveScenarioInfo(@RequestBody AddScenarioDTO add, @JwtTokenParser(key="userId") String userId) {
        return ResultUtils.success(scenarioService.saveScenario(add,userId));
    }

    @RequestMapping(value = "/resources/{id}/{type}", method = RequestMethod.PATCH)
    public JsonResult updateResourceCollection(@PathVariable("id") String id,
                                               @PathVariable("type") String type,
                                               @JwtTokenParser(key = "userId") String userId,
                                               @RequestBody List<String> update) {
        return ResultUtils.success(scenarioService.updateresourceCollection(id,type, userId, update));
    }
}
