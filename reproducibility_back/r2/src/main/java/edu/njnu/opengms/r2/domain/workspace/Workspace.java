package edu.njnu.opengms.r2.domain.workspace;

import edu.njnu.opengms.common.entity.BaseEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author ：Zhiyi
 * @Date ：2023/8/21 11:08
 * @modified By：
 * @version: 1.0.0
 */
@Data
@Document
@Builder
public class Workspace extends BaseEntity {
    @Id
    String id;
    String name;
    String selectTaskId;//selected tasks;
    String projectId;
    String type;//notebook or integrateTask
}
