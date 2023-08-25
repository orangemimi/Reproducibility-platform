package edu.njnu.opengms.r2.domain.programol.Controller;


import edu.njnu.opengms.r2.domain.programol.Service.ProgramFileService;
import edu.njnu.opengms.r2.domain.programol.dto.CreateProgramFileDTO;
import edu.njnu.opengms.r2.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @Author TRY
 * @Date 2023/3/29 11:21
 * @Version 1.0
 */
@CrossOrigin
@RestController
@RequestMapping(value="/programfile")
public class ProgramFileController {
    @Autowired
    ProgramFileService programFileService;

    @PostMapping("/create")
    JsonResult CreateFile(@RequestBody CreateProgramFileDTO createProgramFileDTO){
        return programFileService.CreateFile(createProgramFileDTO);
    }

    @GetMapping("/delete/{fileId}")
    JsonResult DeleteFile(@PathVariable(value = "fileId")String fileId){
        return programFileService.deleteFile(fileId);
    }

    @GetMapping("/rename/{fileId}/{fileNewName}")
    JsonResult RenameFile(@PathVariable(value = "fileId")String fileId,@PathVariable(value = "fileNewName")String fileNewName){
        return programFileService.renameFile(fileId,fileNewName);
    }

    @GetMapping("/download/{fileId}")
    void DownloadFile(@PathVariable(value = "fileId")String fileId, HttpServletResponse response){
        programFileService.downloadFile(fileId,response);
    }

    @PostMapping(value = "/uploadfile/{parentId}")
    public JsonResult uploadUserPic(@PathVariable("parentId") String parentId,
                                    @RequestPart("upFile") MultipartFile upFile){
        return programFileService.uploadFile(parentId,upFile);
    }

    @GetMapping(value = "/openprogramfile/{id}")
    public JsonResult openProgramFile(@PathVariable("id") String id){
        return programFileService.openProgramFile(id);
    }

    @PostMapping(value = "/saveEdit/{id}")
    public JsonResult saveEdit(@PathVariable("id") String id,
                                    @RequestPart("content") String content){
        return programFileService.saveEdit(id,content);
    }

}
