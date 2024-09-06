package edu.njnu.opengms.container.domain.model.dto;

import edu.njnu.opengms.common.dto.ToDomainConverter;
import edu.njnu.opengms.common.entity.AgentInfo;
import edu.njnu.opengms.container.domain.model.ModelService;
import edu.njnu.opengms.container.domain.model.support.Dependency;
import edu.njnu.opengms.container.domain.model.support.ModelBehavior;
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
public class AddModelServiceDTO implements ToDomainConverter<ModelService> {
    String name;
    String description;

    String privacy;// is public?

    String type; //service or code

    List<Dependency> dependencies;
    String boundaryCondition;

    List<AgentInfo> agentInfo;
    String license;
    String snapshot;
    String contributorId;
    String value;//resource url


    //service
    ModelBehavior behavior;
    //    List<State> behavior;
    String md5;
    String serviceId;
    String sponsor;

    //code
    String content;
}
