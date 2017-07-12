/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atomist.spring.agent;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.OrderedHealthAggregator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import com.atomist.spring.agent.reporter.ApplicationEventReporter;
import com.atomist.spring.agent.reporter.HealthReporter;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableScheduling
@EnableAsync
@EnableConfigurationProperties({ AgentConfigurationProperties.class })
@ConditionalOnExpression("${atomist.enabled:true}")
public class AgentAutoConfiguration {

    private final HealthAggregator healthAggregator;

    private final Map<String, HealthIndicator> healthIndicators;

    private GitProperties gitProperties;

    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    public AgentAutoConfiguration(ObjectProvider<HealthAggregator> healthAggregator,
            ObjectProvider<Map<String, HealthIndicator>> healthIndicators,
            ObjectProvider<GitProperties> gitProperties, ObjectProvider<RestTemplate> restTemplate,
            ObjectProvider<ObjectMapper> objectMapper) {
        this.healthAggregator = healthAggregator.getIfAvailable();
        this.healthIndicators = healthIndicators.getIfAvailable();
        this.gitProperties = gitProperties.getIfAvailable();
        this.restTemplate = restTemplate.getIfAvailable();
        this.objectMapper = objectMapper.getIfAvailable();
    }

    @Bean
    public ApplicationEventReporter agentApplicationEventListener(
            ApplicationEventPublisher publisher) {
        return new ApplicationEventReporter(publisher);
    }

    @Bean
    public HealthReporter healthListener(HealthAggregator healthAggregator,
            Map<String, HealthIndicator> healthIndicators, ApplicationEventPublisher publisher) {
        return new HealthReporter(
                this.healthAggregator == null ? new OrderedHealthAggregator()
                        : this.healthAggregator,
                this.healthIndicators == null ? Collections.<String, HealthIndicator> emptyMap()
                        : this.healthIndicators,
                publisher);
    }

    @Bean
    public AgentEventSender eventSender(AgentConfigurationProperties properties,
            ApplicationContext context) {
        return new AgentEventSender(properties, context,
                this.gitProperties != null ? this.gitProperties
                        : new GitProperties(new Properties()),
                this.restTemplate != null ? this.restTemplate : new RestTemplate(),
                this.objectMapper != null ? this.objectMapper : new ObjectMapper());
    }

    @Bean
    @ConditionalOnMissingBean(name = "eventTaskExecutor")
    public TaskExecutor eventTaskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setMaxPoolSize(1);
        return pool;
    }

}
