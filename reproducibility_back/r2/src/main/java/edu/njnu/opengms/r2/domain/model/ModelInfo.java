package edu.njnu.opengms.r2.domain.model;


import edu.njnu.opengms.r2.domain.model.support.Dependency;
import edu.njnu.opengms.r2.domain.model.support.ModelBehavior;
import edu.njnu.opengms.common.entity.AgentInfo;
import edu.njnu.opengms.common.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @ClassName ModelInfo
 * @Description ass we see
 * @Author sun_liber
 * @Date 2020/4/15
 * @Version 1.0.0
 */
@Data
@Document
public class ModelInfo extends BaseEntity {
    @Id
    String name;
    String description;

    String type; //service or code
    String privacy;// is public?

    List<Dependency> dependencies;
    String boundaryCondition;

    List<AgentInfo> agentInfo;
    String license;
    String snapshot;
    String contributorId;
    String value;//resource url


    //service
    ModelBehavior behavior;
    String md5;
    String sponsor;

    //code
    String content;
}
