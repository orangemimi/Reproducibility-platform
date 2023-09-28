package edu.njnu.opengms.r2.domain.scenario;

import edu.njnu.opengms.common.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2021/4/1 22:41
 * @modified By：
 * @version: 1.0.0
 */
@Data
public class Scenario extends BaseEntity {
    @Id
    String id;
    String name;
    String projectId;
    List<String> instances;//绑定的instanceId；
    String type;//notebook or integrateTask or sequentModels
    ResourceCollection resourceCollection;

}
