package edu.njnu.opengms.r2.domain.folder;

import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
import edu.njnu.opengms.r2.domain.dataItem.DataItemRepository;
import edu.njnu.opengms.r2.domain.folder.dto.AddFolderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author ：Zhiyi
 * @Date ：2023/8/23 17:08
 * @modified By：
 * @version: 1.0.0
 */

@RestController
@RequestMapping(value = "/folders")
public class FolderController {
    @Autowired
    FolderService folderService;

    @Autowired
    DataItemRepository dataItemRepository;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public JsonResult create(@RequestBody AddFolderDTO add, @JwtTokenParser(key = "userId") String userId) {
        return ResultUtils.success(folderService.create(add,userId));
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public JsonResult getByCreatorId(@JwtTokenParser(key = "userId") String userId) {
        return ResultUtils.success(folderService.getFolderByCreator(userId));
    }

    @RequestMapping(value = "/{scenarioId}", method = RequestMethod.GET)
    public JsonResult getByUserId(@PathVariable("scenarioId") String scenarioId, String userId) {
        return ResultUtils.success(folderService.getFolderByScenarioId(scenarioId));
    }

    @RequestMapping(value = "", method = RequestMethod.PATCH)
    public JsonResult update(@RequestBody AddFolderDTO add,@JwtTokenParser(key = "userId") String userId) {
        return ResultUtils.success(folderService.update(add,userId));
    }

    // 通过dataItem的ID获取所在folder的id
    @RequestMapping(value = "/getFolderIdByDataItemId/{dataId}", method = RequestMethod.GET)
    public JsonResult getFolderIdByDataItemId(@JwtTokenParser(key = "userId") String userId , @PathVariable String dataId) {
        return ResultUtils.success(folderService.getFolderIdByDataItemId(userId,dataId));
    }
}
