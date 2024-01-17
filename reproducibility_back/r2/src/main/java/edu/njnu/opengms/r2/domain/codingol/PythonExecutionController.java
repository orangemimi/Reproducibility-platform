package edu.njnu.opengms.r2.domain.codingol;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
@RestController
public class PythonExecutionController {

    @Autowired
    PythonExecutionService pythonExecutionService;

    @PostMapping("/execute-python")
    public JsonResult executePythonCode(@RequestBody Map<String, String> requestBody, @JwtTokenParser(key = "userId") String userId) {
        String pythonCode = requestBody.get("code");
        String folderId = requestBody.get("folderId");
        try {
            // 使用Docker容器执行Python代码
            String result = pythonExecutionService.executePythonCodeInDocker(pythonCode,folderId,userId);
            return ResultUtils.success(result);
        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("执行Python代码时出错");
            return ResultUtils.error("代码错误或执行未响应，请检查代码或联系管理员");

        }
    }


}
