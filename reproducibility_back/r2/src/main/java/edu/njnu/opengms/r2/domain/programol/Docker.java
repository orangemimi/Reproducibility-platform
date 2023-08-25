package edu.njnu.opengms.r2.domain.programol;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @Author TRY
 * @Date 2023/4/6 10:27
 * @Version 1.0
 */
@Data
public class Docker {
    @Id
    @ApiModelProperty(value = "id",hidden = true)
    String id= IdUtil.objectId();
    String dockerId;
    String projectId;
    String userId;
    String projectName;
    String pythonVersion;
    String path;
}
