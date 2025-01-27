package edu.njnu.opengms.r2.domain.assessment;

import cn.hutool.json.JSONObject;
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
    String reproducedScenarioId;
    String purpose;//reperformability/consistency
    String object;// model/all/
    String modelId;// the assessment object/resource/allprocess
    Integer step;
    List<JSONObject> outputs;//outputs of assessment
    String creatorId;
}
