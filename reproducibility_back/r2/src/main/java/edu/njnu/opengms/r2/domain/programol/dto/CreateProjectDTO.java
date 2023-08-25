package edu.njnu.opengms.r2.domain.programol.dto;

import lombok.Data;

/**
 * @Author TRY
 * @Date 2023/3/30 15:08
 * @Version 1.0
 */
@Data
public class CreateProjectDTO {
    String projectName;
    String userId;
    String pythonVersion;
    String projectPath;
    String workspace;
    boolean ifReq;
    String requirementPath;
}
