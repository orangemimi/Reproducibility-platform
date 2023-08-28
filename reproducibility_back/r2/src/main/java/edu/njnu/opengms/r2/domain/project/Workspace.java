package edu.njnu.opengms.r2.domain.project;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2023/8/21 11:08
 * @modified By：
 * @version: 1.0.0
 */
@Data
@Builder
public class Workspace {
//    ResourceCollection resourceCollection;
    List<String> dataList;
    List<String> modelList;
    String task;//selected tasks;
}
