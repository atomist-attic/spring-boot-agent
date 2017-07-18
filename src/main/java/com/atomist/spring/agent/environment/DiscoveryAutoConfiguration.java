package com.atomist.spring.agent.environment;

import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscoveryAutoConfiguration {
    
    @Bean
    @ConditionalOnCloudPlatform(CloudPlatform.CLOUD_FOUNDRY)
    public Discovery cloudFoundryDiscovery() {
        return new CloudFoundrDiscovery();
    }
    
}
