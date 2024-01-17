package edu.njnu.opengms.r2.domain.codingol;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class dockerConfig {

    @Value("${docker.clientHost}")
    private String clientHost;

    @Value("${docker.clientPort}")
    private String clientPort;

    @Bean(name = "dockerClient")
    DockerClient dockerClient() {
        return connect();
//        return DockerClientBuilder.getInstance().build();
    }
//    连接docker
    private DockerClient connect(){
        String host = "tcp://" + clientHost + ":" + clientPort;
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(host)
                .build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(180))
                .build();
        DockerClient client = DockerClientImpl.getInstance(config,httpClient);
        return client;
//        log.info("docker initialize successfully");
    }
}
