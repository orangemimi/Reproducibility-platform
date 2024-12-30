package edu.njnu.opengms.r2.domain.environmentalConfiguration;

import lombok.Data;

/**
 * @version 1.0
 * @Author rgj
 * @Date 2024/11/27 17:34
 * @注释
 */

@Data
public class Task {
    private String state; // init, pending, error, success
    private String modelExecutionInfo; // 执行日志信息

    public void appendModelExecutionInfo(String log) {
        this.modelExecutionInfo += log + "\n";
    }
}
