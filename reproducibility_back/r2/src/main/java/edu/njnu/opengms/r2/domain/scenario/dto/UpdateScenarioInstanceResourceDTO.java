package edu.njnu.opengms.r2.domain.scenario.dto;


import edu.njnu.opengms.common.dto.ToDomainConverter;
import edu.njnu.opengms.r2.domain.scenario.ResourceCollection;
import edu.njnu.opengms.r2.domain.scenario.Scenario;
import lombok.Data;

import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2021/4/1 22:42
 * @modified By：
 * @version: 1.0.0
 */
@Data
public class UpdateScenarioInstanceResourceDTO implements ToDomainConverter<Scenario> {
    List<String> instances;//绑定的instanceId；
    ResourceCollection resourceCollection;

}
