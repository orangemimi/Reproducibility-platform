package edu.njnu.opengms.r2.config;

import edu.njnu.opengms.r2.domain.environmentalConfiguration.InstallRequiresWebSocketHandler;
import edu.njnu.opengms.r2.domain.environmentalConfiguration.InstallSinglePackageWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.github.dockerjava.api.DockerClient;


/**
 * @ClassName WebSocketConfig
 * @Description todo
 * @Author sun_liber
 * @Date 2019/7/17
 * @Version 1.0.0
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final DockerClient dockerClient;

    public WebSocketConfig(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
//        System.out.println("DockerClient has been injected: " + (dockerClient != null));
    }

//    @Bean
//    public ServerEndpointExporter serverEndpointExporter() {
//        return new ServerEndpointExporter();
//    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new InstallRequiresWebSocketHandler(dockerClient), "/installRequires")
                .setAllowedOrigins("*");
        registry.addHandler(new InstallSinglePackageWebSocketHandler(dockerClient), "/installSinglePackage")
                .setAllowedOrigins("*");
    }
}
