package edu.njnu.opengms.r2.domain.programol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author TRY
 * @Date 2023/3/27 9:45
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {
    String projectName;
    String pythonVersion;
    String userId;
    String workspace;
    List<DemoDTO> demoList;
    String requirementPath;

}
