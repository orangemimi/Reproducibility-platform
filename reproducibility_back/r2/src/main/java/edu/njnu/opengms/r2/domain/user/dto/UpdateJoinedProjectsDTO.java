package edu.njnu.opengms.r2.domain.user.dto;


import edu.njnu.opengms.common.dto.ToDomainConverter;
import edu.njnu.opengms.r2.domain.user.User;
import lombok.Data;

import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2021/4/25 21:22
 * @modified By：
 * @version: 1.0.0
 */
@Data
public class UpdateJoinedProjectsDTO implements ToDomainConverter<User> {
    List<String> joinedProjects;
}
