package edu.njnu.opengms.r2.domain.environmentalConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @version 1.0
 * @Author rgj
 * @Date 2024/12/3 11:05
 * @注释
 */
@Configuration
public class AsyncConfig {
    @Bean(name = "customTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("CustomTaskExecutor-");
        executor.initialize();
        return executor;
    }
}
