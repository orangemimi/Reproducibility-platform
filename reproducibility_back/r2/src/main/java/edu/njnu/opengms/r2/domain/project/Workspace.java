package edu.njnu.opengms.r2.domain.project;

import lombok.Data;

import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2023/8/21 11:08
 * @modified By：
 * @version: 1.0.0
 */
@Data
public class Workspace {
    ResourceCollection resourceCollection;
    List<String> taskList;//selected tasks;
}
