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

import java.net.URI;
import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("atomist")
public class AgentConfigurationProperties {

    /**
     * URL of the Atomist webhook for your workspace
     */
    private URI url = URI.create("https://webhook.atomist.com/atomist/spring");

    /**
     * Enable trace output; this allows you to review the event messages the agent will send
     */
    private boolean enabled = true;

    /**
     * Enable or disable the agent
     */
    private boolean debug = false;

    /**
     * Id of the agent to be sent to Atomist
     */
    private String id;

    /**
     * Information to send to Atomist regarding your environment
     */
    private Properties environment = new Properties();
    
    public Properties getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(Properties environment) {
        this.environment = environment;
    }
    
    public void setUrl(URI url) {
        this.url = url;
    }
    
    public URI getUrl() {
        return url;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isDebug() {
        return debug;
    }
    
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
}
