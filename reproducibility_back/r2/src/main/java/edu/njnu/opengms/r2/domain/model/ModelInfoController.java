package edu.njnu.opengms.r2.domain.model;

import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
import edu.njnu.opengms.r2.domain.model.dto.AddModelInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

/**
 * @Author ：Zhiyi
 * @Date ：2023/8/23 17:08
 * @modified By：
 * @version: 1.0.0
 */

@RestController
@RequestMapping(value = "/models")
public class ModelInfoController {
    @Autowired
    ModelInfoRepository modelInfoRepository;

    //create one project
    @RequestMapping(value = "", method = RequestMethod.POST)
    public JsonResult create(@JwtTokenParser(key="userId") String userId,@RequestBody AddModelInfoDTO add) {
        ModelInfo modelInfo = new ModelInfo();
        add.setContributorId(userId);
        add.convertTo(modelInfo);

        if(add.getType()=="service"){

        }
        return ResultUtils.success(modelInfoRepository.insert(modelInfo));
    }

    @RequestMapping(value = "/getPublicModels/{currentPage}/{pageSize}", method = RequestMethod.GET)
    public JsonResult getPublicModels(@PathVariable int currentPage, @PathVariable int pageSize) {
        PageRequest pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<ModelInfo> modelList = modelInfoRepository.findByPrivacy("public", pageable);
        return ResultUtils.success(modelList);
    }

    @RequestMapping(value = "/getProjectsPage/{currentPage}/{pageSize}", method = RequestMethod.GET)
    public JsonResult getMyModels(@JwtTokenParser(key = "userId") String userId) {
        return ResultUtils.success();
    }

}
