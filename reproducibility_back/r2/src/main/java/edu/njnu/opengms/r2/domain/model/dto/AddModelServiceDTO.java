package edu.njnu.opengms.r2.domain.model.dto;

import edu.njnu.opengms.common.dto.ToDomainConverter;
import edu.njnu.opengms.common.entity.AgentInfo;
import edu.njnu.opengms.r2.domain.model.Model;
import edu.njnu.opengms.r2.domain.model.support.Dependency;
import edu.njnu.opengms.r2.domain.model.support.State;
import lombok.Builder;
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
@Builder
public class AddModelServiceDTO implements ToDomainConverter<Model> {
    //service
//    ModelBehavior behavior;
    String name;
    String description;

    String type; //service or code
    String privacy;// is public?

    List<Dependency> dependencies;
    String boundaryCondition;

    List<AgentInfo> agentInfo;
    String license;
    String snapshot;
    String contributorId;// if id == OpenGMS_platform, name ==OpenGMS_platform,
    String value;//resource url

    //service
//    ModelBehavior behavior;
    List<State> behavior;
    String md5;
    String serviceId;
//    String sponsor;

    //code
    String content;
}

