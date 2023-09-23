package edu.njnu.opengms.r2.domain.model.dto;

import edu.njnu.opengms.common.dto.ToDomainConverter;
import edu.njnu.opengms.r2.domain.model.Model;
import edu.njnu.opengms.r2.domain.model.support.State;
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
public class AddModelServiceDTO implements ToDomainConverter<Model> {
    //service
//    ModelBehavior behavior;
    List<State> behavior;
    String md5;
    String serviceId;
}

