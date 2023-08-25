package edu.njnu.opengms.r2.domain.programol.Controller;

import edu.njnu.opengms.r2.domain.programol.Service.WorkSpaceService;
import edu.njnu.opengms.r2.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author TRY
 * @Date 2023/3/29 11:21
 * @Version 1.0
 */
@CrossOrigin
@RestController
@RequestMapping(value="/workspace")
public class WorkSpaceController {
    @Autowired
    WorkSpaceService workSpaceService;

    @GetMapping(value = "/{userId}")
    public JsonResult getUserInfo(@PathVariable(value = "userId") String userId){
        return workSpaceService.OpenworkSpace(userId);
    }




}
