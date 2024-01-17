package edu.njnu.opengms.r2.domain.assessment.dto;

import edu.njnu.opengms.common.dto.ToDomainConverter;
import edu.njnu.opengms.r2.domain.assessment.Assessment;
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
public class AddAssessmentDTO implements ToDomainConverter<Assessment> {
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

