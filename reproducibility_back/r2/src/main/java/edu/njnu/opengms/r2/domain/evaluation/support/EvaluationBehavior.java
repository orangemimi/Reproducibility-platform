package edu.njnu.opengms.r2.domain.evaluation.support;


import edu.njnu.opengms.r2.domain.model.support.ModelEvent;
import lombok.Data;

import java.util.List;

/**
 * @ClassName EvaluationBehavior
 * @Description ass we see
 * @Author sun_liber
 * @Date 2020/4/13
 * @Version 1.0.0
 */
@Data
public class EvaluationBehavior {
    List<ModelEvent> inputs;
    String interactiveCode;
}
