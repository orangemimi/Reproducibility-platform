package edu.njnu.opengms.r2.domain.programol.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author TRY
 * @Date 2023/3/31 10:19
 * @Version 1.0
 */
@Data
public class WorkSpaceDTO {
    String userName;
    List<Option> projectOption;
}
