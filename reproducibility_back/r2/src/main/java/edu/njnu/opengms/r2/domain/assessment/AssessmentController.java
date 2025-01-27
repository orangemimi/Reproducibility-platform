package edu.njnu.opengms.r2.domain.assessment;

import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
import edu.njnu.opengms.r2.domain.assessment.dto.AddAssessmentDTO;
import edu.njnu.opengms.r2.domain.assessment.dto.UpdateAssessmentDTO;
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
        return ResultUtils.success(assessmentService.create(add,userId));
    }
    @RequestMapping(value = "/{scenarioId}", method = RequestMethod.GET)
    public JsonResult get( @JwtTokenParser(key = "userId") String userId, @PathVariable("scenarioId") String scenarioId) {
        return ResultUtils.success(assessmentService.get(userId,scenarioId));
    }

    @RequestMapping(value = "/{assessmentId}", method = RequestMethod.PATCH)
    public JsonResult update(@JwtTokenParser(key = "userId") String userId, @PathVariable("assessmentId") String assessmentId, @RequestBody UpdateAssessmentDTO update) {
        return ResultUtils.success(assessmentService.update(update,userId,assessmentId));
    }



    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public JsonResult startAssessOut(@RequestBody JSONObject form) {
        return ResultUtils.success(assessmentService.startAssessment(form));
    }

    @RequestMapping(value = "/auto/{reproducedScenarioId}", method = RequestMethod.GET)
    public JsonResult autoAssess(@PathVariable("reproducedScenarioId") String reproducedScenarioId) {
        return ResultUtils.success(assessmentService.autoAssessment(reproducedScenarioId));
    }


}
