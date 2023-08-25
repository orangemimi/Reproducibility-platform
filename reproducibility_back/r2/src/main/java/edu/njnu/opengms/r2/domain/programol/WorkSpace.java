package edu.njnu.opengms.r2.domain.programol;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * @Author TRY
 * @Date 2023/3/29 10:40
 * @Version 1.0
 */
@Data
public class WorkSpace {
    @Id
    @ApiModelProperty(value = "id",hidden = true)
    String id= IdUtil.objectId();
    String userId;
    String userName;
    String workspacePath;
    List<String> projectNames;
    int projectCount;
}
