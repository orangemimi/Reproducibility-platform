package edu.njnu.opengms.r2.domain.project;


import edu.njnu.opengms.common.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2020/12/4 14:44
 * @modified By：
 * @version: 1.0.0
 */

@Data
@Document
public class Project extends BaseEntity {
    @Id
    String id;
    String name;
    String description;
    String privacy;// Ispublic
    String creatorId;
    List<Member> memberList;
    Workspace workspace;

    Integer level=0;
    String forkingProjectId;// //从项目的那条记录开始fork的
    List<String> forkedProjectIdList;//有哪些项目是从这个project fork的

    Integer starredCount;
    Integer watchedCount;

    String introduction;
    String purpose;

    String picture;
    List<String> tags;
}
