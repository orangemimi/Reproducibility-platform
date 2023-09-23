package edu.njnu.opengms.r2.domain.project.dto;


import edu.njnu.opengms.common.dto.ToDomainConverter;
import edu.njnu.opengms.r2.domain.project.Member;
import edu.njnu.opengms.r2.domain.project.Project;
import lombok.Data;

import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2020/12/10 21:36
 * @modified By：
 * @version: 1.0.0
 */
@Data
public class UpdateProjectDTO implements ToDomainConverter<Project> {
    String name;
    String description;
    String privacy;// Ispublic
    String creatorId;
    List<Member> memberList;
    String scenario; //selected workspace


    Integer level=0;
    String forkingProjectId;// //从项目的那条记录开始fork的
    List<String> forkedProjectIdList;//有哪些项目是从这个project fork的

    Integer starredCount;
    Integer watchedCount;

    String picture;
    List<String> tags;
}
