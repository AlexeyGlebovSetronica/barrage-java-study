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

### Multithreading in Java. Working with threads

Multithreading in Java is the simultaneous execution of two or more threads to maximise the use of the CPU (central processing unit). Each thread runs in parallel and does not require a separate memory area. In addition, context switching between threads takes less time.

**Multithreading Utilisation:**

* **Better use of a single CPU:** If a thread is waiting for a response to a request sent over the network, another thread can use the CPU to perform other tasks at that time. Also, if a computer has multiple CPUs or a CPU has multiple execution cores, multithreading allows an application to use these additional cores.

* **Optimal use of multiple CPUs or their cores:** You must use multiple threads in an application to use all CPUs or CPU cores. A single thread can use at most one CPU, sometimes not even fully.

* **Improved user experience in terms of query response speed:** e.g. if a button is clicked in the GUI, this action sends a query over the network: what matters here is which thread executes this query. If the same thread that updates/notifies the GUI is used, then the user may experience a hang on the interface waiting for the request to be answered. But this request can be executed by a background thread, so that the thread in the GUI can respond to other user requests in the meantime.

* **Improved user experience in terms of fairness in resource allocation:** multithreading allows fair allocation of computer resources among users. Imagine a server that receives requests from clients and it has only one thread to fulfil those requests. If a client sends a request that takes a long time to process, all other requests are forced to wait until it completes. When each client request is executed by its own thread, no single task can completely take over the CPU.

#### Processes in Java: definition and functions

* A process consists of code and data. It is created by the operating system when the application is launched, is quite resource-intensive and has its own virtual address space.

* Processes work independently of each other. They do not have direct access to shared data in other processes.

* The operating system allocates resources to the process - memory and execution time.

* If one process is locked, no other process can execute until it is unlocked.

* A parent process is usually duplicated to create a new process.

* A process can control child processes, but not processes of the same level.

#### What are threads

A thread is the smallest component of a process. Streams can run in parallel with each other. They are also often referred to as lightweight processes. They use the address space of a process and share it with other threads.

Threads can monitor each other and communicate through the **wait()**, **notify()**, **notifyAll()** methods.

#### Thread states
Threads can be in several states:

* **New** - when an instance of the `Thread` class is created, the thread is in the new state. It is not running yet.

* **Running** - the thread is running and the processor starts its execution. During execution, the thread's state can also change to `Runnable`, `Dead` or `Blocked`.

* **Suspended** - a running thread is suspended, then it can be resumed. The thread will start running from where it was stopped.

* **Blocked** - the thread is waiting for resources to be released or I/O operation to be completed. While in this state, the thread does not consume CPU time.

* **Terminated** - the thread immediately terminates its execution. Its work cannot be resumed. The reasons for thread termination may be situations when the thread code is completely executed or an error occurred during thread execution (for example, a segmentation error or an unhandled exception).

* **Dead** - after a thread has finished its execution, its state changes to dead, i.e. it completes its life cycle.

#### Runnable threads

Provide an implementation of the `Runnable` object. The `Runnable` interface defines a single method, run, which must contain the code that runs in the thread. The `Runnable` object is passed to the `Thread` constructor. For example:

```java
public class ExampleApplication {
    public static void main(String[] args) {
        // the task object for 
        Runnable myRunnable = () -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Inside a thread: " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread myThread = new Thread(myRunnable);
        
        // starting new thread 
        myThread.start();

        for (int i = 0; i < 3; i++) {
            System.out.println("In main thread: " + i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

#### Thread termination and Demon Threads

In Java, a process is terminated when all of its main and child threads are terminated.

Demon threads are low-priority threads that run in the background to perform tasks such as rubbish collection: they free memory of unused objects and clear the cache. Most JVM (Java Virtual Machine) threads are daemon threads.

#### Thread-demon properties:

Have no effect on closing the JVM when all user threads have finished their execution;

The JVM itself closes when all user threads have stopped executing;

If the JVM detects a running thread daemon, it will terminate it and then close. The JVM does not take into account whether a thread is running or not.

To determine whether a thread is a daemon, the boolean isDaemon() method is used. If yes, it returns the value true, if no, it returns the value false.

#### Thread Termination
Terminating a Java thread requires the preparation of thread implementation code. The Java Thread class contains a stop() method, but it is marked as deprecated. The original stop() method makes no guarantees about the state in which a thread is stopped. That is, any Java objects that the thread had access to during execution will remain in an unknown state. If other threads in the application had access to the same objects, they may "break" unexpectedly.

Instead of calling the stop() method, you need to implement the thread's code to stop it. Here is an example of a class with a Runnable implementation that contains an additional doStop() method that sends Runnable a signal to stop. Runnable will check it and stop it when it is ready.

```java
public class MyRunnable implements Runnable {
    private boolean doStop = false;
    public synchronized void doStop() {
        this.doStop = true;
    }
    private synchronized boolean keepRunning() {
        return !this.doStop;
    }
    @Override
    public void run() {
        while(keepRunning()) {
            // keep doing what this thread should do.
            System.out.println("Running");
            try {
                Thread.sleep(3_000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```
Pay attention to the `doStop()` and `keepRunning()` methods. The `doStop()` method is not called from the thread executing the `run()` method of `MyRunnable`.

The `keepRunning()` method is called from within the thread executing the `run()` method of `MyRunnable`. Since the `doStop()` method is not called, the `keepRunning()` method will return true, meaning the thread executing the `run()` method will continue to run.

**Example**:

```java
public class MyRunnableMain {
    public static void main(String[] args) {
        MyRunnable myRunnable = new MyRunnable();
        Thread thread = new Thread(myRunnable);
        thread.start();
        try {
            Thread.sleep(10L * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myRunnable.doStop();
    }
}
```
In the example, `MyRunnable` is first created and then passed to a thread and started. The thread executing the `main()` method (the main thread) goes to sleep for 10 seconds and then calls the `doStop()` method of the `MyRunnable` class instance. Subsequently, the thread executing the `MyRunnable` method will stop because `keepRunning()` will return false after `doStop()` is called.

Note that if a `Runnable` implementation needs not only the `run()` method (but for example also the `stop()` or `pause()` method), the Runnable implementation can no longer be created using lambda expressions. You will need a custom class or interface that extends `Runnable`, which contains additional methods and is implemented by an anonymous class.

#### Method Thread.sleep()
A thread can stop itself by calling the static `Thread.sleep()` method. `Thread.sleep()` takes the number of milliseconds as a parameter. The sleep() method will attempt to sleep for this amount of time before resuming execution. Thread sleep() does not guarantee absolute accuracy.

Here is an example of stopping a Java thread for 10 seconds (10 thousand milliseconds) by calling the `Thread.sleep()` method:

```java
try {
    Thread.sleep(10L * 1000L);
} catch (InterruptedException e) {
    e.printStackTrace();
}
```
The thread executing the code will go to sleep for about 10 seconds.

#### Method join()
The **join()** method of a Thread class instance is used to join the start of execution of one thread with the end of execution of another thread. This is to ensure that one thread does not start executing before the other thread completes. If the join() method is called on a Thread, the thread that is currently executing is blocked until the Thread finishes execution.

The **join()** method waits no more than the specified number of milliseconds for the thread to die. A timeout of 0 (zero) means "wait forever".

**Syntax:**

```java
public void join() throws InterruptedException
```

**For example:**

```java
class TestJoinMethod1 extends Thread {
    public void run() {
        for (int i = 1; i <= 5; i++) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.println(i);
        }
    }

    public static void main(String args[]) {
        TestJoinMethod1 t1 = new TestJoinMethod1();
        TestJoinMethod1 t2 = new TestJoinMethod1();
        TestJoinMethod1 t3 = new TestJoinMethod1();
        t1.start();
        try {
            t1.join();
        } catch (Exception e) {
            System.out.println(e);
        }
        t2.start();
        t3.start();
    }
}
```

The result:
```
1
2
3
4
5
1
1
2
2
3
3
4
4
5
5
```

From the example, we can see that as soon as thread t1 completes its task, threads t2 and t3 start executing their tasks.

#### Thread safe data types in Java

In Java, Atomic types are classes that provide atomic read and write operations for primitive data types. These classes reside in the java.util.concurrent.atomic package and provide safe operations for multi-threaded data access without using explicit locks. The basic idea is that read and write operations for these types are atomic, meaning that they are executed in their entirety and cannot be interrupted by other threads.

For example:

```java
    @Test
    void multi_thread_test_with_atomic_integer() throws InterruptedException {
        AtomicInteger a = new AtomicInteger(0);
        AtomicInteger b = new AtomicInteger(0);
        int count = 100000;
        var cdl = new CountDownLatch(count);
        var executor = Executors.newFixedThreadPool(100);
        for (int i = 0; i < count; i++) {
            executor.execute(() -> {
                a.incrementAndGet();
                b.incrementAndGet();
                cdl.countDown();
            });
        }

        cdl.await();
        System.out.println(a);
        System.out.println(b);

        executor.shutdown();
    }
```

Here the operation of incrementing variables `a` and `b` within ~100 threads is performed, and `AtomicInteger` guarantees safe operation between threads.

#### Thread pool executions

For parallel computing, Java provides an Executor interface that helps separate the description of tasks from how they will be executed, how threads will be used, how scheduling will be done, etc.

The interface accepts tasks as instances of the Runnable class. At some point in time, one of the threads "picks up" a task and executes it by calling the Runnable::run method. This interface has many implementations designed for different types of tasks.

In general, when choosing which Executor interface implementation to use, you should answer the following questions based on the peculiarities of the tasks you plan to execute:

* How many parallel threads should be started by default?
* What should be done with new tasks if all available threads are busy?
* Should I limit the size of the task queue and what should I do if it overflows?

In such cases it is best to use `ExecutorService` to organise multithreaded work.

**For example:**

```java
import java.util.Random;
import java.util.concurrent.*;

public class ExampleApplication {
    public static void main(String[] args) {
        System.out.println("Started");
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        // creating 5 MyRunnable instances
        for (int i = 1; i <= 5; i++) {
            MyRunnable myRunnable = new MyRunnable("Thread " + i);
            // put it to the executor
            executorService.execute(myRunnable);
        }
        
        // awaiting
        executorService.shutdown();
    }

    private static class MyRunnable implements Runnable {
        private final String threadName;
        public MyRunnable(String threadName) {
            this.threadName = threadName;
        }

        @Override
        public void run() {
            try {
                System.out.println(threadName + " starting.");
                // Thread sleep
                Thread.sleep(new Random().ints(2000, 5000).findFirst().getAsInt());
                System.out.println(threadName + " finished.");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
```

Here are threads that perform operations and return nothing, but if multithreading results are required, `Callable` objects are used for such cases.

For example:

```java
import java.util.concurrent.*;

public class ExampleApplication {
	public static void main(String[] args) {
		ExecutorService executor = Executors.newSingleThreadExecutor();

        // create callable operation with the type String (or what ever you need)
		Callable<String> task = () -> {
			Thread.sleep(2000);
			return "Any task data";
		};

		Future<String> future = executor.submit(task);

		try {
            // await the result
			String result = future.get();
			System.out.println("The result is: " + result);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();
		}
	}
}
```

This also uses `ExecutorService` with a thread pool,
to which a `Callable` object is passed for processing in another thread,
and the result `executor.submit(task)` will be returned as a `Future` wrapper class, which will be the result of processing.

The `Future` interface in Java represents a way to interact with the result of an asynchronous operation. It is used when executing a task might take a significant amount of time, and you want to continue the program's execution without blocking while waiting for the task to complete.

Generally, `Future` allows you to:

* **Asynchronous task execution:** You can submit a task for execution and obtain a Future object as a result. This Future object can later be used to check the status of the task execution or retrieve its result.

* **Checking execution status:** You can check whether the task has completed or is still running.
The `isDone()` and `isCancelled()` methods provide information about the status.

* **Retrieving the result:** If the task has completed, you can obtain its result using the get() method. This method blocks until the result becomes available if the task has not yet completed.

**For example:**
```java
import java.util.concurrent.*;

public class ExampleApplication {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        Callable<String> task = () -> {
            // Simulate a long-running task
            Thread.sleep(2000);
            return "Task execution result";
        };

        Future<String> future = executor.submit(task);

        // Do some other work in the current thread

        // Check if the task has completed
        if (!future.isDone()) {
            System.out.println("Task is not yet completed. Waiting...");
        }

        // Get the result (blocks if the task is not completed yet)
        String result = future.get();

        System.out.println("Result: " + result);

        // Shutdown the ExecutorService
        executor.shutdown();
    }
}
```

#### Creating background operations in Spring Boot

Above we looked at the cases without using the Spring framework and next we will look at how to implement this within the framework.

Firstly, we need to create an `@Bean` describing the required `ThreadPoolExecutor`, giving it a name to separate our thread pool from other threads or the framework's internal thread pool.

And then tag the necessary service method with the `@Async` annotation.

**Например:**

```java
@Configuration // mark this class as Configuration, and it will be scanned by DI to create our beans 
@EnableAsync // annotation for configuration which allow to use another thread pool
public class AsyncConfiguration {
    
    // creating specified thread pool
    @Bean(name = "appThreadPool")
    public Executor appThreadPool() {
        return Executors.newSingleThreadExecutor();
    }
}
```

**Create a async operation:**

```java
import com.setronica.eventing.exceptions.NotFoundException;
import com.setronica.eventing.persistence.Event;
import com.setronica.eventing.persistence.EventRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsyncService {
    @Async("appThreadPool")
    public void asyncOperation() throws InterruptedException {
        Thread.sleep(5000);
        System.out.println("Done.");
    }
}
```

The `@Async` annotation indicates that this method should be processed in another thread,
in this case the call will be passed by the framework to the `appThreadPool` bean for execution.

This is how multithreaded operations are described, now the call of this method from any part of the programme will be automatically passed to the thread pool and there is no need to write code to run the task.

## Task 6

RabbitMQ
Spring doc

TBD ....

## Task 7

healthcheck
prometheus
grafana

TBD ....

---

#### Misc

Don't forget to create custom docker network to avoid any inconvenient circumstances with 
other docker services and keep current environment as isolated from the other projects

I can suggest to create 
```
docker network create -d bridge --subnet 172.21.0.0/24 --gateway 172.21.0.1 dockernet
```
