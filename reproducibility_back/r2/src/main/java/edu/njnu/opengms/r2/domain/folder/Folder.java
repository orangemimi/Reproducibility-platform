package edu.njnu.opengms.r2.domain.folder;

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
public class Folder extends BaseEntity {
    @Id
    String id;
    String name;//创建的文件夹名称
    String tagId;// projectId or scenarioId
    String parent;
    Integer level;//貌似没用
    List<String> children;
    List<String> dataList;//这个文件夹下存储的 data id
    String creatorId;


}
