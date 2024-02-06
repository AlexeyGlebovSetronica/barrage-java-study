**# Entertainment event project

## Task 1

Let's begin our Java story :)

Let's imagine that you are a computer science student. You have several friends who are
part of rock bands and love playing their music. However, they lack opportunities to monetize
their creativity. They need a system to keep track of the participants at their concerts, plan
their performances, announce future events, and so on.

### What to do?

* Install [IntelliJ IDEA](https://www.jetbrains.com/idea/download)
  and [Java 17 SDK](https://www.jetbrains.com/help/idea/sdk.html)
* Fork from this project into your private one and open access to your
  mentor ([AlexeyGlebovSetronica](https://github.com/AlexeyGlebovSetronica))
    * How to fork - https://docs.github.com/en/get-started/quickstart/fork-a-repo
    * How to open access - https://docs.github.com/en/account-and-profile/setting-up-and-managing-your-personal-account-on-github/managing-access-to-your-personal-repositories/inviting-collaborators-to-a-personal-repository
* Open it in Idea and run
    * `Ctrl-Shift-A` - `Execute Gradle Task` - `gradle bootRun`
    * Check if you can open http://localhost:8080/event/api/v1/events

    * Try `List All Events`
    * Try `Search All Events`, it should fail with 500
* For this task you will need to create a new branch `TASK-1` and to do following things:
    * Read carefully everything you have in `src/main/**` and `*.gradle` and try to understand.
      Everywhere you have any doubts or questions
      on what is going on - leave a text comment right in the code, e.g:
      ```java
      public static void main(String[] args) {
          SpringApplication.run(EventingApplication.class, args);
      }
      ```
    * Implement following search
      API - [EventController::searchEvents](com/setronica/eventing/web/EventController.java)
      ```
      http://localhost:8080/event/api/v1/events/search?q=text
      ```
    * Create MR and send it to your mentor. He will review it and go through your comments.

## Task 2

Next we will continue learning Spring Framework and Java. Today we will have a new task is to connect PostgreSQL database to the project.
In the project is available file docker-compose.yml with the described PostgreSQL database service.

In Spring Framework to work with the database created sub-module data-jpa, which implements Java Persistence API.

When working with the database it is necessary to take care of the database schema management. We will use the liquibase tool.
In summary, for convenient work with the database we need to connect the following packages:
1. Spring Data JPA
2. Liquibase
3. PostgreSQL driver

Additional information:
https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa
https://docs.spring.io/spring-data/jpa/reference/jpa/getting-started.html
https://www.baeldung.com/liquibase-refactor-schema-of-java-app
https://docs.liquibase.com/start/home.html


Add the dependencies to our project:

```groovy
  implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
  runtimeOnly 'org.postgresql:postgresql:42.7.1'
  implementation 'org.liquibase:liquibase-core:4.25.1'
```

    Note that for `spring-boot-starter` packages it is not necessary to specify the package version, as the appropriate version of the gradle library will be selected by the `io.spring.dependency-management` plugin.
    ```
    plugins {
      id 'java'
      id 'org.springframework.boot' version '3.2.2'
      id 'io.spring.dependency-management' version '1.1.4' // <-- here
    }
    ```


Now it is necessary to describe the liquibase configuration and specify the database connection configuration.

`LiquibaseConfiguration`

```java
@Configuration
public class LiquibaseConfiguration {
    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:liquibase/master.xml");
        liquibase.setDataSource(dataSource);
        return liquibase;
    }
}
```

Data Scheme:

Migration index file `classpath:liquibase/master.xml`:
```xml
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.4.xsd">
    <include file="classpath:liquibase/changelog/00000000000000_initial_schema.xml" />
</databaseChangeLog>
```

And the first migration:
```xml
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:ext="http://www.liquibase.org/xml/ns/dbchangelog-ext"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.4.xsd http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd">
    <changeSet id="20240126-1" author="alex.glebov">
....
    </changeSet>
</databaseChangeLog>

```
DB connection configs at `application.yml`:

```yaml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/event
    username: event_user
    password: pa55w0rd
```

And we are ready to start our application.

### What to do?
Practical exercise:
Describe a CRUD to handle the `Event` entity, i.e. read, create, update, delete operations.
Update the service code, and controller methods for all the above operations.


## Task3

In the next task we need to add business logic for event tickets. Events can take place on different days, as well as repeat, for example, Cirque du Soleil, therefore we need to organise the storage of the schedule of events, as well as the number of available seats at the event.
So, to create the schedule, let's create the `event_schedule` table


```sql
create table event_schedule (
  id serial primary key,
  event_id int not null,
  event_date date not null,
  available_seats int not null,
  price decimal(10,2) not null
);
```

table for selling tickets `ticket_order`

```sql
create table ticket_order (
  id serial primary key,
  firstname varchar(255) not null,
  lastname varchar(255) not null,
  email varchar(255) not null,
  amount int not null
);
```

Let's describe the entities for tables and JPA repositories:
```java
...
```


Then we need to implement business logic to manage these entities (CRUD).
Create CRUD endpoints.


In business logic sometimes exceptional situations occur, to handle them correctly we can use Spring Boot tools for exception handling.
In exceptional situations we will throw exceptions and use Spring Boot tools to convert them into client-understandable DTOs and error codes.

Create your own exception types and use the `@ControllerAdvice` component to handle exceptions in your application, for example, when an object is not found in a data source.

Note that the number of ticket sales cannot be more than `event_schedule.available_seats`. To avoid this, you need to create an insert trigger, which will check the available quantity before inserting a new row.
Do exception handling for the current case.

Think why this task should not be solved at the application level?


## Task 4

logging + ELK
testing: JUnit5, SpringBootTest

In Java, logging often uses an API such as SLF4J (Simple Logging Facade for Java) in combination with a specific implementation such as Logback or Log4j.
Slf4j (Simple Logging Facade for Java) is a facade (interface) for various logging libraries in Java. It provides a common interface for applications, allowing them to use different logging libraries without having to modify the application code. In this way, Slf4j makes code more flexible and portable between different logging implementations.
Lombok optionally provides a convenient access to Logger via the `@Slf4j` annotation, if without lombok, here is an example usage:

```java
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CLASS_NAME);
```

where CLASS_NAME, is the classname of your class, for example, EventController.class.

#### then you can just use it in your code:

```java
    log.trace("Trigger event or any debug info which make sense");
    log.trace("Trigger event or any debug info which make sense with exception to propogate", throwable);
    log.debug("Trigger event or any debug info which make sense");
    log.debug("Trigger event or any debug info which make sense with exception to propogate", throwable);
    log.info("Trigger event or any info which make sense");
    log.info("Trigger event or any info which make sense with exception to propogate", throwable);
    log.warn("Trigger event or any info which make sense");
    log.warn("Trigger event or any info which make sense with exception to propogate", throwable);
    log.error("Error description or any info which make sense");
    log.error("Error description or any info which make sense with exception to propogate", throwable);
```

#### Additional Parameters:

```java
    log.info("Looking for event with id {}", id); // int id, will return for example "Looking for event with id 1"
    // or
    log.info("Looking for event with id {}, data {}", id, obj); // some object, will return for example "Looking for event with id `object string representation`", but don't forget to override `toString()` method to convert the object to human-readable representation
```

As you can see, there are 5 logging levels, each of which can be filtered before outputting to the stream.
In my practice, there have been cases when detailed logging from production is required, then I changed the logging level of either the application or a separate module of the programme in the application configuration:

#### For the whole application:

```yaml
# application.yml
logging:
  level:
    root: warn
```

#### Or separate module (package name):

```yaml
# application.yml
logging:
  level:
    # where `org.hibernate` is required package name
    org.hibernate: error
```

I think this will be enough to understand how the logging system is organised inside a Java application.

### Sending logs to external storage

In Java applications it is possible to use sending logs by different models PUSH model and PULL model.
Push model implies that application will send logs to external storage by itself, PULL model implies writing logs in a special format, for example, JSON.

#### Push model

To configure sending logs to an external storage, for example, ElasticSearch using Logstash, first you need to add a dependency for the [logback](https://github.com/logfellow/logstash-logback-encoder) library in the `build.gradle` gradle configuration file

```gradle
dependencies {
    // ... other dependencies
    
	// logging
	runtimeOnly 'net.logstash.logback:logstash-logback-encoder:7.4'
	
	// ... other dependencies
}
```

And also add a configuration file for logback, the file defaults to `/resources/logback.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false">
    <property name="DESTINATION" value="${log.destination:-localhost:5000}"/>
    <include resource="org/springframework/boot/logging/logback/base.xml"/>
    <appender name="LOGSTASH_DEFAULT" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>${DESTINATION}</destination>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <providers>
                <mdc/> <!-- MDC variables on the Thread will be written as JSON fields-->
                <context/> <!--Outputs entries from logback's context -->
                <version/> <!-- Logstash json format version, the @version field in the output-->
                <logLevel/>
                <loggerName/>

                <pattern>
                    <pattern>
                    </pattern>
                </pattern>

                <threadName/>
                <message/>

                <logstashMarkers/> <!-- Useful so we can add extra information for specific log lines as Markers-->
                <arguments/> <!--or through StructuredArguments-->

                <stackTrace/>
            </providers>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="LOGSTASH_DEFAULT"/>
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

Where

```xml
<property name="DESTINATION" value="${log.destination:-localhost:5000}"/>
```

Sets the value of a variable from the `application.yml` file

```yaml
log:
  destination: 127.0.0.1
```

For `appender` with type `net.logstash.logback.appender.LogstashTcpSocketAppender`, the logstash server address `destination` must be added.
The `encoder` specifies the format into which the log stream will be transformed, and the `providers` list specifies what should be reflected in it.

And finally
```xml
    <root level="INFO">
        <appender-ref ref="LOGSTASH_DEFAULT"/>
        <appender-ref ref="CONSOLE"/>
    </root>
```
here sets the direction of log stream output, as specified in the current configuration send to stdout and send to Logstash.

To configure the path to the logback configuration file, you can use the `logging.config=classpath:logback-dev.xml` parameter:

```yaml
logging:
  config: classpath:logback-dev.xml
```
For example, to avoid sending logs to logstash during development, this can have the unpleasant effect of causing the application to make attempts to send logs.

#### Pull модель

For the pull model, it will be sufficient to change the application log format, for example, to json format

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/base.xml"/>
    <appender name="stdout" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <providers>
                <mdc/>
                <timestamp/>
                <context />
                <version />
                <logLevel />
                <loggerName />
                <message/>
                <stackTrace/>
                <threadName />
                <logstashMarkers />
                <arguments />
            </providers>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="stdout"/>
    </root>
</configuration>
```

Then configure external services to collect logs.


Any package versions you can check on [Maven Central](https://mvnrepository.com/ "https://mvnrepository.com/")


## Task 5

RabbitMQ


concurrency and multi-threading

how to create Thread
how to create Runnable object
how to work with Future and CompletableFuture
what is the Atomic
ThreadPool executors
how to work with threads in a loop


explain the Atomic types


TBD ....

## Task 6

healthcheck
prometheus
grafana

TBD ....

## Task 7

TBD ....

---

#### Misc

Don't forget to create custom docker network to avoid any inconvenient circumstances with 
other docker services and keep current environment as isolated from the other projects

I can suggest to create 
```
docker network create -d bridge --subnet 172.21.0.0/24 --gateway 172.21.0.1 dockernet
```
