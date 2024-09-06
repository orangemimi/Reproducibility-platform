package edu.njnu.opengms.r2.domain.user.dto;


import edu.njnu.opengms.common.dto.ToDomainConverter;
import edu.njnu.opengms.r2.domain.user.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2020/12/4 17:39
 * @modified By：
 * @version: 1.0.0
 */
@Data
public class AddUserDTO implements ToDomainConverter<User> {
    String name;
    String password;    //MD5
    String email;   //used for registration
    //    String userId;
    String avatar;
    String institution;
    List<String> tags;

    List<String> joinedProjects = new ArrayList<>();
    List<String> createdProjects = new ArrayList<>();
    List<String> forkedProjects = new ArrayList<>();

}
