package edu.njnu.opengms.r2.domain.project;

import lombok.Data;

/**
 * @Author ：Zhiyi
 * @Date ：2020/12/21 15:38
 * @modified By：
 * @version: 1.0.0
 */
@Data
public class Member {
    String memberId;//member的userId
    //    String name;
    String role;////role: ['builder', 'rebuilder_explorer','rebuilder_operator']
}
