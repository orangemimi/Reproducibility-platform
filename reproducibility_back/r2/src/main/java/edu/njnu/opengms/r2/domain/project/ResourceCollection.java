package edu.njnu.opengms.r2.domain.project;

import lombok.Data;

import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2023/8/22 20:00
 * @modified By：
 * @version: 1.0.0
 */
@Data
public class ResourceCollection {
    List<String> dataResourceList;
    List<String> modelResourceList;
//    List<String> reproducedDataResourceList;
}
