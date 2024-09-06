package edu.njnu.opengms.r2.remote;

import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author ：Zhiyi
 * @Date ：2020/12/29 11:08
 * @modified By：
 * @version: 1.0.0
 */
@RestController
@RequestMapping(value = "/portal")
public class RemotePortalController {
    @Autowired
    RemotePortalService remotePortalService;


    @RequestMapping(value = "/modelBehavior/{id}", method = RequestMethod.GET)
    JsonResult getModel(@PathVariable(value = "id") String id) {
        return ResultUtils.success(remotePortalService.getModelInfo(id));
    }

    @RequestMapping(value = "/getUnitList/{currentPage}/{pagesize}", method = RequestMethod.GET)
    JsonResult getModel(@PathVariable("currentPage") int currentPage, @PathVariable("pagesize") int pagesize) {
        return ResultUtils.success(remotePortalService.getUnitList(currentPage, pagesize));
    }

    @RequestMapping(value = "/getModelList", method = RequestMethod.GET)
    JsonResult getModelList(@RequestParam int page, @RequestParam int pageSize, @RequestParam String searchText) {
        return ResultUtils.success(remotePortalService.getModelsByPortal(page, pageSize, searchText));
    }

    @RequestMapping(value = "/getComputableModels/{oid}", method = RequestMethod.GET)
    JsonResult getComputableModels(@PathVariable String oid) {
        return ResultUtils.success(remotePortalService.getComputableModels(oid));
    }

    @RequestMapping(value = "/getDataServiceByPortal", method = RequestMethod.POST)
    public JsonResult getDataServiceByPortal(@RequestBody JSONObject jsonObject) {
        return ResultUtils.success(remotePortalService.getDataServiceByPortal(jsonObject.getStr("method"), jsonObject.getInt("page"), jsonObject.getInt("pageSize"), jsonObject.getStr("searchText")));
    }

    @RequestMapping(value = "/getDataServiceInfo/{oid}/{serviceId}", method = RequestMethod.GET)
    public JsonResult getDataServiceInfo(@PathVariable String oid, @PathVariable String serviceId) {
        return ResultUtils.success(remotePortalService.getDataServiceInfo(oid, serviceId));
    }
}
