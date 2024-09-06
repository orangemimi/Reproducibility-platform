package edu.njnu.opengms.r2.domain.scenario.dto;

import edu.njnu.opengms.common.dto.ToDomainConverter;
import edu.njnu.opengms.r2.domain.scenario.Scenario;
import lombok.Data;

@Data
public class UpdateScenarioContainerIdDTO implements ToDomainConverter<Scenario> {
    String containerId;


    public UpdateScenarioContainerIdDTO(String containerId) {
        this.containerId = containerId;
    }
}
