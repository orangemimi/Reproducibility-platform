package edu.njnu.opengms.r2.domain.model.support;

import lombok.Data;

import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2023/8/23 18:09
 * @modified By：
 * @version: 1.0.0
 */
@Data
public class State {
    String name;
    String description;
    String stateId;
    List<ModelEvent> inputs;
    List<ModelEvent> outputs;
    List<Parameter> parameters;

}
