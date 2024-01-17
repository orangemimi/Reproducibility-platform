package edu.njnu.opengms.r2.domain.scenario;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2023/8/22 20:00
 * @modified By：
 * @version: 1.0.0
 */
@Data
@Builder
public class ForkedScenarios {
   public List<String> instances;
   public String executorId;
}
