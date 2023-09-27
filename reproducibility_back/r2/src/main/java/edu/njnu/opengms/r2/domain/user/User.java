package edu.njnu.opengms.r2.domain.user;

import edu.njnu.opengms.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2020/12/4 14:28
 * @modified By：
 * @version: 1.0.0
 */

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User  extends BaseEntity {
    @Id
    String id;  //除了和server交互之外，都是利用这个id
    String name;
    String password;    //MD5
    String email;   //used for registration

    String avatar;
    String institution;
    List<String> tags;
    /**
     * Unique
     * adi
     */
    List<String> joinedProjects;
    List<String> createdProjects;
    List<String> forkedProjects;
    List<String> staredProjects;

    List<String> modelList;//contained model list

    /**
     * User server
     */
    String userServerId;
}
