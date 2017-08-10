# Atomist 'spring-boot-agent'

[![Build Status](https://travis-ci.org/atomist/spring-boot-agent.svg?branch=master)](https://travis-ci.org/atomist/spring-boot-agent)

An Agent that sends events for Spring Boot applications to Atomist.

Read more about automating software development
at [Automating Our Development Flow With Atomist][blog].  Detailed
documentation is available in the [Atomist Documentation][docs].

[blog]: https://medium.com/the-composition/automating-our-development-flow-with-atomist-6b0ec73348b6#.hwa55uv8o
[docs]: http://docs.atomist.com/

## Configuration

To make proper use of the agent, you have to enroll your team with the Atomist Bot.

Then you can drop the Agent in your application by adding the following to your `pom.xml`:

```
		<dependency>
			<groupId>com.atomist</groupId>
			<artifactId>spring-boot-agent</artifactId>
			<version>latest version</version>
		</dependency>
```

As this agent is not available from Maven Central adding the following repo is requried, too:

```
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
# configure the endpoint; {id} should be replaced by your Slack team id
atomist.url=https://webhook.atomist.com/atomist/application/teams/{id}

# use the following keys to send some information about your environment to Atomist
atomist.environment.domain=
atomist.environment.<any>= 
```


## Support

General support questions should be discussed in the `#support`
channel on our community Slack team
at [atomist-community.slack.com][slack].

If you find a problem, please create an [issue][].

[issue]: https://github.com/atomist/github-rugs/issues

## Contributing

If you are interested in contributing to the Atomist open source
projects, please see our [contributing guidelines][contrib] and
our [code of conduct][code].

[contrib]: https://github.com/atomist/welcome/blob/master/CONTRIBUTING.md
[code]: https://github.com/atomist/welcome/blob/master/CODE_OF_CONDUCT.md


---
Created by [Atomist][atomist].
Need Help?  [Join our Slack team][slack].

[atomist]: https://www.atomist.com/
[slack]: https://join.atomist.com/
