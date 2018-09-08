/*
 * Copyright 2018 Atomist.
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

package com.atomist.spring.agent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationEvent;

public class AgentEvent extends ApplicationEvent {
    
    public enum State {
        STARTED, FAILED, STOPPING, HEALTHY, UNHEALTHY
    }

    private static final long serialVersionUID = 8376189303592439766L;
    
    private final State state;
    private final Map<String, Object> payload = new HashMap<>();
    
    public AgentEvent(State state, Object source) {
        this(state, Collections.emptyMap(), source);
    }
    
    public AgentEvent(State state, Map<String, Object> payload, Object source) {
        super(source);
        this.state = state;
        this.payload.putAll(payload);
    }
    
    public Map<String, Object> payload() {
        return payload;
    }
    
    public State state() {
        return state;
    }
}
