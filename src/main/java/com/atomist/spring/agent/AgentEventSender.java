/*
 * Copyright 2018 Atomist.
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

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.atomist.spring.agent.AgentEvent.State;
import com.atomist.spring.agent.environment.Discovery;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AgentEventSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentEventSender.class);

    private final ApplicationContext context;
    private final GitProperties gitProperties;
    private final ObjectMapper objectMapper;
    private Map<String, Object> payload;
    private final AgentConfigurationProperties properties;
    private final RestTemplate restTemplate;
    private final Map<String, Discovery> discoveries;

    private boolean stopped = false;

    public AgentEventSender(AgentConfigurationProperties properties, ApplicationContext context,
            Map<String, Discovery> discoveries, GitProperties gitProperties,
            RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.properties = properties;
        this.context = context;
        this.gitProperties = gitProperties;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.discoveries = discoveries;
    }

    @PostConstruct
    public void init() throws Exception {
        Map<String, String> git = git();

        payload = new HashMap<>();
        payload.put("git", git);
        payload.put("version", AgentEventSender.class.getPackage().getImplementationVersion());

        this.properties.getEnvironment().entrySet()
                .forEach(e -> payload.put(e.getKey().toString(), e.getValue()));

        Map<String, Object> valueMap = new HashMap<>();
        this.discoveries.values().stream().filter(v -> v.getEnvironment() != null)
                .forEach(d -> valueMap.put(d.getName(), d.getEnvironment()));
        payload.put("data", valueMap);

        payload.put("host", getHostName());
        if (this.properties.getId() != null) {
            payload.put("id", this.properties.getId());
        }
        else {
            payload.put("id", context.getId() + "-" + getPid() + "-" + getHostName());
        }
    }

    @EventListener
    public void onApplicationEvent(AgentEvent event) {
        if (!this.stopped) {

            long ts = event.getTimestamp();
            String state = event.state().toString().toLowerCase();

            Map<String, Object> pl = new HashMap<>();
            pl.putAll(payload);
            pl.put("state", state);
            pl.put("ts", ts);
            pl.putAll(event.payload());

            debugPayload(pl);
            doSend(event, pl);
        }
    }

    private void debugPayload(Map<String, Object> pl) {
        if (properties.isDebug()) {
            try {
                LOGGER.debug("Atomist event about to be sent:\n{}",
                        objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(pl));
            }
            catch (JsonProcessingException e) {
                LOGGER.debug("Error sending event to Atomist", e);
            }
        }
    }

    private void doSend(AgentEvent event, Map<String, Object> pl) {
        try {
            restTemplate.postForEntity(properties.getUrl(), pl, String.class);
        }
        catch (RestClientException e) {
            if (properties.isDebug()) {
                LOGGER.debug("Error sending event to Atomist:", e);
            }
            else {
                LOGGER.debug("Error sending event to Atomist: {}", e.getMessage());
            }
        }
        finally {
            if (event.state() == State.STOPPING) {
                this.stopped = true;
            }
        }
    }

    private String getHostName() {
        return getValue(() -> InetAddress.getLocalHost().getHostName());
    }

    private String getPid() {
        return getValue(() -> System.getProperty("PID"));
    }

    private String getValue(Callable<Object> call) {
        return getValue(call, "");
    }

    private String getValue(Callable<Object> call, String defaultValue) {
        try {
            Object value = call.call();
            if (value != null && StringUtils.hasLength(value.toString())) {
                return value.toString();
            }
        }
        catch (Exception ex) {
            // Swallow and continue
        }
        return defaultValue;
    }

    private Map<String, String> git() {
        Map<String, String> git = new HashMap<>();
        git.put("sha", gitProperties.getCommitId());
        git.put("branch", gitProperties.getBranch());
        git.put("url", gitProperties.get("remote.origin.url"));
        return git;
    }

}
