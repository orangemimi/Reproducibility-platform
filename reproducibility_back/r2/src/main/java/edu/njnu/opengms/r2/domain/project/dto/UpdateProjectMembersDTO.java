package edu.njnu.opengms.r2.domain.project.dto;


import edu.njnu.opengms.common.dto.ToDomainConverter;
import edu.njnu.opengms.r2.domain.project.Member;
import edu.njnu.opengms.r2.domain.project.Project;
import lombok.Data;

import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2021/4/25 20:06
 * @modified By：
 * @version: 1.0.0
 */
@Data
public class UpdateProjectMembersDTO implements ToDomainConverter<Project> {
    List<Member> memberList;//type: ['builder', 'rebuilder_explorer','rebuilder_operator', 'visitor']
}
