package edu.njnu.opengms.r2.domain.programol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author TRY
 * @Date 2023/3/30 19:57
 * @Version 1.0
 */
@Data
@AllArgsConstructor
public class CreateProgramFileDTO {
    String fileName;
    String filePath;
    boolean isFolder;
    String projectName;
    String parentId;
    String userId; //上传者ID
}
