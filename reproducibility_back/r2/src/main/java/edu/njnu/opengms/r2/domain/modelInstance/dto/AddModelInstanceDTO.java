package edu.njnu.opengms.r2.domain.modelInstance.dto;

import edu.njnu.opengms.common.dto.ToDomainConverter;
import edu.njnu.opengms.r2.domain.model.Model;
import edu.njnu.opengms.r2.domain.modelInstance.ModelInstance;
import lombok.Data;

/**
 * @ClassName AddModelServiceDTO
 * @Description ass we see
 * @Author sun_liber
 * @Date 2020/4/29
 * @Version 1.0.0
 */
@Data
public class AddModelInstanceDTO implements ToDomainConverter<ModelInstance> {
    Model model;
    String executorId;
    String scenarioId;
    String status;//running, fail, success
}

