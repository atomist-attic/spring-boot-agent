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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import com.atomist.spring.agent.environment.Discovery;
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
    private final GitProperties gitProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Map<String, Discovery> discoveries;

    public AgentAutoConfiguration(ObjectProvider<HealthAggregator> healthAggregator,
            ObjectProvider<Map<String, HealthIndicator>> healthIndicators,
            ObjectProvider<Map<String, Discovery>> discoveries,
            ObjectProvider<GitProperties> gitProperties, ObjectProvider<RestTemplate> restTemplate,
            ObjectProvider<ObjectMapper> objectMapper) {
        this.healthAggregator = healthAggregator.getIfAvailable();
        this.healthIndicators = healthIndicators.getIfAvailable();
        this.gitProperties = gitProperties.getIfAvailable();
        this.restTemplate = restTemplate.getIfAvailable();
        this.objectMapper = objectMapper.getIfAvailable();
        this.discoveries = discoveries.getIfAvailable();
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
                this.discoveries != null ? this.discoveries : Collections.emptyMap(), 
                this.gitProperties != null ? this.gitProperties
                        : new GitProperties(new Properties()),
                this.restTemplate != null ? this.restTemplate : new RestTemplate(),
                this.objectMapper != null ? this.objectMapper : new ObjectMapper());
    }
}
