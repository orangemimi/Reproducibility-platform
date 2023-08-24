package edu.njnu.opengms.r2.domain.task;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.njnu.opengms.r2.domain.model.support.InputParameter;
import edu.njnu.opengms.r2.domain.project.integrateTask.IntegrateTask;
import edu.njnu.opengms.r2.domain.project.modelInstance.ModelInstance;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author ：Zhiyi
 * @Date ：2023/8/22 20:02
 * @modified By：
 * @version: 1.0.0
 */
@Data
@Document
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type",visible =true,defaultImpl = InputParameter.class)
@JsonSubTypes({@JsonSubTypes.Type(value = ModelInstance.class, name = "model")
        , @JsonSubTypes.Type(value = IntegrateTask.class, name = "models"),
})
public class Task {
    String id;
    String projectId;//一个project可以有多个task，但最终仅绑定一个task给visitor看，一个task也可以有多个instance，但也仅绑定一个instance或一系列instances给vistor看
    String type;
    String creatorId;
    String executorId;
}
