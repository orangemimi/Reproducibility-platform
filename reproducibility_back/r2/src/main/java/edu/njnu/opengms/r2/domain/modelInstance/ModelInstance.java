package edu.njnu.opengms.r2.domain.modelInstance;

import edu.njnu.opengms.common.entity.BaseEntity;
import edu.njnu.opengms.r2.domain.model.support.State;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2023/8/22 20:12
 * @modified By：
 * @version: 1.0.0
 */
@Data
@Document
public class ModelInstance extends BaseEntity {
    @Id
    String id;
    String name;
    String modelName;
    List<State> behavior;
    String modelId;
    String executorId;
    String scenarioId;
    Boolean isReproduced;
    Integer status;//running 0, fail, success 2
    RefreshForm refreshForm;
    String content; //code


}
