package edu.njnu.opengms.r2.domain.modelInstance.dto;

import edu.njnu.opengms.common.dto.ToDomainConverter;
import edu.njnu.opengms.r2.domain.model.support.State;
import edu.njnu.opengms.r2.domain.modelInstance.ModelInstance;
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
public class UpdateModelInstanceDTO implements ToDomainConverter<ModelInstance> {

    List<State> behavior;

    String status;//running 0, fail, success 2


}
