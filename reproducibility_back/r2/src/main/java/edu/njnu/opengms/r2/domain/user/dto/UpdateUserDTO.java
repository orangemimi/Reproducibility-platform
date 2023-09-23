package edu.njnu.opengms.r2.domain.user.dto;


import edu.njnu.opengms.common.dto.ToDomainConverter;
import edu.njnu.opengms.r2.domain.user.User;
import lombok.Data;

import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2020/12/4 15:46
 * @modified By：
 * @version: 1.0.0
 */
@Data
public class UpdateUserDTO implements ToDomainConverter<User> {

    String name;
    String avatar;
    String institution;
    List<String> tags;


//    String state;    // County / State / Province
//    String city;
//    String homepage;
//    String introduction;
//    String createdTime;
//    List<String> organizations;
//    List<String> domain;
//    List<String> loginIp;
//
//    List<String> joinedProjects;
//    List<String> createdProjects;
}
