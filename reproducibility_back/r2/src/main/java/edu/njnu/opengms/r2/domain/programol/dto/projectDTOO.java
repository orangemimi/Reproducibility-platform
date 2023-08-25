package edu.njnu.opengms.r2.domain.programol.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author TRY
 * @Date 2023/8/24 21:54
 * @Version 1.0
 */
@Data
public class projectDTOO {
    String projectName;
    String projectId;
    List<ProgramFileDTO> programFileDTO;
}
