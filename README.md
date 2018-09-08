# Atomist spring-boot-agent

[![Download](https://api.bintray.com/packages/atomist/atomist/spring-boot-agent/images/download.svg)](https://bintray.com/atomist/atomist/spring-boot-agent/_latestVersion)

An agent that sends events for Spring Boot applications to Atomist.

Learn more about automating software delivery at the [Atomist web
site][atomist].  Detailed information is available in the [Atomist
Documentation][docs].

[docs]: http://docs.atomist.com/ (Atomist Documentation)

## Configuration

To make proper use of the agent, you must have [created an Atomist
workspace][get-started].

Then you can drop the Agent in your application by adding the following to your `pom.xml`:

```xml
<dependency>
	<groupId>com.atomist</groupId>
	<artifactId>spring-boot-agent</artifactId>
	<version>latest version</version>
</dependency>
```

As this agent is not available from Maven Central adding the following repo is requried, too:

```xml
<repository>
	<id>public-atomist-release</id>
	<name>Atomist Release</name>
	<url>https://atomist.jfrog.io/atomist/libs-release</url>
</repository>
```

Once add to your project, the agent can be configured from the Spring Boot `application.yml` or
`application.properties`:

```
# enable or disable the agent
atomist.enabled=true
# enable trace output; this allows you to review the event messages the agent will send
atomist.debug=false
# configure the endpoint; {id} should be replaced by your Atomist workspace ID
atomist.url=https://webhook.atomist.com/atomist/application/teams/{id}

# use the following keys to send some information about your environment to Atomist
atomist.environment.domain=
atomist.environment.<any>=
```

[get-started]: https://docs.atomist.com/user/ (Atomist - Getting Started)

## Support

General support questions should be discussed in the `#support`
channel in the [Atomist community Slack workspace][slack].

If you find a problem, please create an [issue][].

[issue]: https://github.com/atomist/spring-boot-agent/issues

## Contributing

If you are interested in contributing to the Atomist open source
projects, please see our [contributing guidelines][contrib] and
our [code of conduct][code].

[contrib]: https://github.com/atomist/welcome/blob/master/CONTRIBUTING.md
[code]: https://github.com/atomist/welcome/blob/master/CODE_OF_CONDUCT.md

---
Created by [Atomist][atomist].
Need Help?  [Join our Slack workspace][slack].

[atomist]: https://atomist.com/ (Atomist - How Teams Deliver Software)
[slack]: https://join.atomist.com/ (Atomist Community Slack Workspace)
