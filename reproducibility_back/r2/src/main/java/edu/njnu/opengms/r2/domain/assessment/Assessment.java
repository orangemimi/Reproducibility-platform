package edu.njnu.opengms.r2.domain.assessment;

import edu.njnu.opengms.common.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Author Zhiyi
 * @Date 2020/8/13  21:34
 * @Version 1.0.0
 */
@Data
@Document
public class Assessment extends BaseEntity {
    @Id
    String id;
    String projectId;
    String initialScenarioId;
    String reproducedScenarioId;
    String purpose;//reperformability/consistency
    String object;// assessment object:data/model
    String comparasionMethodId;//assessment method id for example r/r2/mse....
    String initialResourceId;// the assessment object/resource
    String reproducedResourceId;// the comparasion object/resource
    List<String> outputs;//outputs of assessment
    String creatorId;


}
