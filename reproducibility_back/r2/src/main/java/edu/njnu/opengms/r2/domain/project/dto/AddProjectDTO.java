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
public class AddProjectDTO implements ToDomainConverter<Project> {

    String name;
    String description;
    String privacy;// Ispublic
    String creatorId;
    List<Member> memberList;


    Integer level=0;
    String forkingProjectId;// //从项目的那条记录开始fork的
    List<String> forkedProjectIdList;//有哪些项目是从这个project fork的

    Integer starredCount= 0;
    Integer watchedCount= 0;

    String picture;
    List<String> tags;
}
