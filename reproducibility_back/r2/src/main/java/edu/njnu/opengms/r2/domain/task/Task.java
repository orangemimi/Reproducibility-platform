package edu.njnu.opengms.r2.domain.task;

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
public class Task {
    String id;
    String projectId;//一个project可以有多个task，但最终仅绑定一个task给visitor看，一个task也可以有多个instance，但也仅绑定一个instance或一系列instances给vistor看
    String type;
    String creatorId;
    String executorId;
}
