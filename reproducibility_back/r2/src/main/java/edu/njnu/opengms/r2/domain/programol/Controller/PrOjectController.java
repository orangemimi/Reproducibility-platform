package edu.njnu.opengms.r2.domain.programol.Controller;

import edu.njnu.opengms.r2.domain.programol.Service.PrOjectService;
import edu.njnu.opengms.r2.domain.programol.dto.CreateProjectDTO;
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
@RequestMapping(value="/project")
public class PrOjectController {
    @Autowired
    PrOjectService projectService;


    @PostMapping
    public JsonResult createProject(@RequestBody CreateProjectDTO createProjectDTO){
        JsonResult jsonResult = projectService.CreateProject(createProjectDTO);
        return jsonResult;
    }

    @GetMapping("/open/{projectId}")
    public  JsonResult OpenProject(@PathVariable(value = "projectId")String projectId){
        JsonResult jsonResult = projectService.OpenProject(projectId);
        return jsonResult;
    }

    @GetMapping("/delete/{projectId}")
    public  JsonResult DeleteProject(@PathVariable(value = "projectId")String projectId){
        JsonResult jsonResult = projectService.DeleteProject(projectId);
        return jsonResult;
    }

    @GetMapping("/donwloadDep/{projectId}")
    public JsonResult donwloadDep(@PathVariable(value = "projectId")String projectId){
        JsonResult jsonResult = projectService.DownloadDep(projectId);
        return jsonResult;
    }

    @GetMapping("/runDemo/{projectId}")
    public  JsonResult runDemo(@PathVariable(value = "projectId")String projectId){
        JsonResult jsonResult = projectService.RunDemo(projectId);
        return jsonResult;
    }

    @GetMapping("/stopDemo/{projectId}")
    public  JsonResult stopDemo(@PathVariable(value = "projectId")String projectId){
        JsonResult jsonResult = projectService.StopDemo(projectId);
        return jsonResult;
    }

    @GetMapping("/stopContainer/{projectId}")
    public JsonResult stopContainer(@PathVariable(value = "projectId")String projectId){
        JsonResult jsonResult = projectService.StopContainer(projectId);
        return jsonResult;
    }

}
