package edu.njnu.opengms.r2.domain.instance;

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
public class Instance {
    @Id
    String id;
    String name;
    String type;

    String taskId;//对应的task的id，每一个instance都有对应的id；

    String executorId;//如果是public的 任何人都可以execute

}
