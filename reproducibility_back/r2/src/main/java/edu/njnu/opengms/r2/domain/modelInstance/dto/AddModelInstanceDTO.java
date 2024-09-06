package edu.njnu.opengms.r2.domain.modelInstance.dto;

import edu.njnu.opengms.common.dto.ToDomainConverter;
import edu.njnu.opengms.r2.domain.model.support.State;
import edu.njnu.opengms.r2.domain.modelInstance.ModelInstance;
import edu.njnu.opengms.r2.domain.modelInstance.RefreshForm;
import lombok.Data;

import java.util.List;

/**
 * @ClassName AddModelServiceDTO
 * @Description ass we see
 * @Author sun_liber
 * @Date 2020/4/29
 * @Version 1.0.0
 */
@Data
public class AddModelInstanceDTO implements ToDomainConverter<ModelInstance> {
    String name;
    String modelName;
    List<State> behavior;
    String modelId;
    String executorId;
    String scenarioId;
    Integer status;//running 0, fail, success 2
    RefreshForm refreshForm;
    Boolean isReproduced;
    String content; //code


}

