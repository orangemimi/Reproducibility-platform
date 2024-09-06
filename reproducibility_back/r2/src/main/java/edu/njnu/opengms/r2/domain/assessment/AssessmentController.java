package edu.njnu.opengms.r2.domain.assessment;

import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
import edu.njnu.opengms.r2.domain.assessment.dto.AddAssessmentDTO;
import edu.njnu.opengms.r2.domain.dataItem.DataItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author ：Zhiyi
 * @Date ：2023/8/23 17:08
 * @modified By：
 * @version: 1.0.0
 */

@RestController
@RequestMapping(value = "/assessment")
public class AssessmentController {
    @Autowired
    AssessmentService assessmentService;

    @Autowired
    DataItemRepository dataItemRepository;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public JsonResult create(@RequestBody AddAssessmentDTO add, @JwtTokenParser(key = "userId") String userId) {
        return ResultUtils.success(assessmentService.create(add, userId));
    }

    @RequestMapping(value = "/{assessmentId}", method = RequestMethod.GET)
    public JsonResult get(@PathVariable("assessmentId") String assessmentId) {
        return ResultUtils.success(assessmentService.get(assessmentId));
    }

    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public JsonResult startAssessOut(@RequestBody JSONObject form) {
        return ResultUtils.success(assessmentService.startAssessment(form));
    }

    @RequestMapping(value = "/auto/{reproducedScenarioId}", method = RequestMethod.GET)
    public JsonResult autoAssess(@PathVariable("reproducedScenarioId") String reproducedScenarioId) {
        return ResultUtils.success(assessmentService.autoAssessment(reproducedScenarioId));
    }

//    @RequestMapping(value = "/{scenarioId}", method = RequestMethod.GET)
//    public JsonResult getByUserId(@PathVariable("scenarioId") String scenarioId, String userId) {
//        return ResultUtils.success(assessmentService.getByScenarioId(scenarioId));
//    }

}
