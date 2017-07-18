package com.atomist.spring.agent.environment;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

public class CloudFoundrDiscovery implements Discovery, EnvironmentAware {

    private Environment environment;

    @Override
    public String getEnvironment() {
        return environment.getProperty("VCAP_APPLICATION");
    }

    @Override
    public String getName() {
        return "cloudfoundry";
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
