package edu.njnu.opengms.r2.domain.AIAssistant;

import com.google.gson.JsonObject;
import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(value = "/AI")
public class AiController {

    @Autowired
    AiService aiService;


    @PostMapping("/pdfParsing")
    public JsonResult uploadDataAndCode(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty()){
            return ResultUtils.error("上传文件为空");
        }
        try {
            Map<String, String> result = aiService.uploadSinglePaper(file);
            return ResultUtils.success(result);
        } catch ( IOException e) {
            return ResultUtils.error("Error processing file: " + e.getMessage());
        }
    }

    @PostMapping("/modelQuery")
    public JsonResult modelQuery(@RequestParam("query") String modelName , @RequestParam("collection") String collection) {
//        String collection = "computableModel";
        ResponseEntity<?> response = aiService.modelQuery(modelName,collection);
        Object responseBody = response.getBody();
        return ResultUtils.success(responseBody);
    }
}
