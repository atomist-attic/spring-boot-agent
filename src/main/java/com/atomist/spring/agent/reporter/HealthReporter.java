/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atomist.spring.agent.reporter;

import java.util.Collections;
import java.util.Map;

import org.springframework.boot.actuate.health.CompositeHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;

import com.atomist.spring.agent.AgentEvent;
import com.atomist.spring.agent.AgentEvent.State;

public class HealthReporter {

    private final ApplicationEventPublisher publisher;

    private final CompositeHealthIndicator healthIndicator;

    private Status previousStatus;

    public HealthReporter(HealthAggregator healthAggregator,
            Map<String, HealthIndicator> healthIndicators, ApplicationEventPublisher publisher) {
        CompositeHealthIndicator healthIndicator = new CompositeHealthIndicator(healthAggregator);
        for (Map.Entry<String, HealthIndicator> entry : healthIndicators.entrySet()) {
            healthIndicator.addHealthIndicator(getKey(entry.getKey()), entry.getValue());
        }
        this.healthIndicator = healthIndicator;
        this.publisher = publisher;
    }

    @Scheduled(fixedRate = 10000)
    public void poll() {
        Health health = healthIndicator.health();
        if (health.getStatus() != null && this.previousStatus != null
                && health.getStatus().getCode() != previousStatus.getCode()) {
            publisher.publishEvent(new AgentEvent(
                    (health.getStatus() == Status.DOWN ? State.UNHEALTHY : State.HEALTHY),
                    Collections.singletonMap("health", health), this));
        }
        previousStatus = health.getStatus();
    }

    private String getKey(String name) {
        int index = name.toLowerCase().indexOf("healthindicator");
        if (index > 0) {
            return name.substring(0, index);
        }
        return name;
    }

}
