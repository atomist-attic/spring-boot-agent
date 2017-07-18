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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import com.atomist.spring.agent.AgentEvent;
import com.atomist.spring.agent.AgentEvent.State;

public class ApplicationEventReporter {

    private final ApplicationEventPublisher publisher;

    public ApplicationEventReporter(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @PostConstruct
    public void ini() {
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(this.publisher));
    }

    @EventListener
    public void onApplicationEvent(SpringApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            publisher.publishEvent(new AgentEvent(State.STARTED, this));
        }
        else if (event instanceof ApplicationFailedEvent) {
            publisher.publishEvent(new AgentEvent(State.FAILED, Collections.singletonMap("error",
                    ((ApplicationFailedEvent) event).getException()), this));
        }
    }
    
    @PreDestroy
    public void shutdown() {
        publisher.publishEvent(new AgentEvent(AgentEvent.State.STOPPING, this));
    }

    private static class ShutdownHook extends Thread {

        private ApplicationEventPublisher publisher;

        public ShutdownHook(ApplicationEventPublisher publisher) {
            this.publisher = publisher;
        }

        @Override
        public void run() {
            publisher.publishEvent(new AgentEvent(AgentEvent.State.STOPPING, this));
        }
    }
}
