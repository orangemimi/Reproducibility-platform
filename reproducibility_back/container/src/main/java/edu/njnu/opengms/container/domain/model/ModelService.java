package edu.njnu.opengms.container.domain.model;

import edu.njnu.opengms.r2.domain.model.ModelInfo;
import edu.njnu.opengms.r2.domain.model.support.Dependency;
import edu.njnu.opengms.r2.domain.model.support.ModelBehavior;
import edu.njnu.opengms.common.dto.ToDomainConverter;
import edu.njnu.opengms.common.entity.AgentInfo;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * @ClassName AddModelServiceDTO
 * @Description ass we see
 * @Author sun_liber
 * @Date 2020/4/29
 * @Version 1.0.0
 */
@Data
public class ModelService implements ToDomainConverter<ModelInfo> {
    @Id
    String id;
    String name;
    String description;

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
    String serviceId;

    //code
    String content;
}

