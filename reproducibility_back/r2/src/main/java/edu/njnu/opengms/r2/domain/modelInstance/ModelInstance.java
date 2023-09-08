package edu.njnu.opengms.r2.domain.modelInstance;

import edu.njnu.opengms.common.entity.BaseEntity;
import edu.njnu.opengms.r2.domain.model.Model;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    Model model;
    String executorId;
    String scenarioId;
    String status;//running, fail, success


}
