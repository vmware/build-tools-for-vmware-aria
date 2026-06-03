# Logging configuration

## Overview

Installer supports detailed logging configuration for the execution of the `installer` script.

## Table Of Contents

1. [Usage](#usage)
2. [Example](#example)

### Usage

Once a bundle is generated with `-Pbundle-with-installer` maven profile, a `logback.xml` file is generated inside the bundle's `./etc` directory which can be used for logging configurations such as severity, appenders, etc. For detailed information feel free to refer to the official documentation of the [Logback project](https://logback.qos.ch/documentation.html).

### Example

```xml
<configuration>
    <!-- Console appender for INFO logs -->
    <appender name="INFO_CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Console appender for WARN logs -->
    <appender name="WARN_CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>WARN</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Console appender for DEBUG logs -->
    <appender name="DEBUG_CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>DEBUG</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Root logger with level INFO -->
    <root level="INFO">
        <appender-ref ref="INFO_CONSOLE" />
        <appender-ref ref="WARN_CONSOLE" />
        <appender-ref ref="DEBUG_CONSOLE" />
    </root>

    <!-- Uncomment the below tag if you need to enable DEBUG logging as well -->
    <!--
    <root level="DEBUG">
        <appender-ref ref="INFO_CONSOLE" />
        <appender-ref ref="WARN_CONSOLE" />
        <appender-ref ref="DEBUG_CONSOLE" />
    </root>
    -->
</configuration>
```
