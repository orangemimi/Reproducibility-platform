package edu.njnu.opengms.common.domain.container.data;

import edu.njnu.opengms.common.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2021/4/16 20:18
 * @modified By：
 * @version: 1.0.0
 */
@Data
@Document(collection = "DataServices")
public class DataService extends BaseEntity {
    @Id
    String id;
    String name;
    String description;
    String dataServiceId;
    String token;
    String type;
    String privacy;
    List<String> tags;
    String projectId;
    String oid;

    String userId;
    List<String> fileList;
}