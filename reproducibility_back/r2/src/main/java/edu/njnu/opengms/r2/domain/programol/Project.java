package edu.njnu.opengms.r2.domain.programol;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @Author TRY
 * @Date 2023/3/27 9:57
 * @Version 1.0
 */
@Data
public class Project {
    @Id
    @ApiModelProperty(value = "id",hidden = true)
    String id= IdUtil.objectId();
    String projectName;
    String userId;
    String pythonVersion;
    String projectPath;
    String workspace;
    boolean ifReq;
    String requirementPath;
    String PyPath;
    String containerId;
    String projectFileId;
}
