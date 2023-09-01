package edu.njnu.opengms.r2.domain.scenario.dto;


import edu.njnu.opengms.common.dto.ToDomainConverter;
import edu.njnu.opengms.r2.domain.project.ResourceCollection;
import edu.njnu.opengms.r2.domain.scenario.Scenario;
import lombok.Data;

/**
 * @Author ：Zhiyi
 * @Date ：2021/4/1 22:42
 * @modified By：
 * @version: 1.0.0
 */
@Data
public class AddScenarioDTO implements ToDomainConverter<Scenario> {
    String userId;
    String projectId;
    String name;
    ResourceCollection resourceCollection;
    String type;
}
