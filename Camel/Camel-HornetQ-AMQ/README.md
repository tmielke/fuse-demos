# Camel HornetQ to ActiveMQ Demo


Tested against JBoss Fuse 6.1 and HornetQ of JBoss EAP 6.2.


A Camel demo that shows how to route messages from a remote HornetQ JMS broker
to ActiveMQ. ActiveMQ is embedded in JBoss Fuse ESB however the HornetQ 
broker is not provided with this demo. It should be used from a JBoss EAP
installation.

In this demo a Camel route consumes messages from a JMS queue testQueue
on an remote HornetQ broker, routes the message to a Camel 
processor and further to the embedded ActiveMQ broker on queue OUT.

The Camel route calls a custom processor which can optionally 
be configured to raise an exception to test error handling.
Out of the box the custom Camel processor won't raise an exception.


The Camel route simply reads:
```xml
  <camelContext xmlns="http://camel.apache.org/schema/blueprint">
    <route id="hornetq-to-amq">
      <from uri="hornetq:queue:testQueue" />
      <log message="GOT MESSAGE ################################" />
      <log message="REDELIVERED: ${header.JMSRedelivered}" />
      <to uri="amq:queue:OUT" />
      <process ref="testProcessor" />
      <log message="AFTER CAMEL PROCESSOR ######################" />
    </route>    </camelContext>
```



This demo contains two subdirectories:
- routing/ contains the Camel route and JMS endpoint configuration.
  Check out the files
  `routing/src/main/resources/OSGI-INF/blueprint/camel-context.xml`
  `routing/src/main/java/com/fusesource/support/TestProcessor.java`

- features/ contains an OSGi feature definition in order to deploy the 
  Camel route and all its dependencies in one go. Check out the file
  `features/src/main/resources/features.xml`


## COMPILING:

mvn clean install


## Deploying

- Edit `routing/src/main/resources/META-INF/spring/camel-context.xml` of this demo
  and configure the <jee:jndi-lookup> bean to point to your JBoss EAP instance.
  You need to have a HornetQ JMS ConnectionFactory exposed in JNDI under the 
  JNDI name 'java:jboss/exported/'.

- Edit `features/src/main/resources/features.xml` and update the bundle 
  <bundle>wrap:file:///Volumes/Transcend/JBoss/EAP/jboss-eap-6.2/bin/client/jboss-client.jar</bundle>
  to point to your installation of JBoss.
  There is surely a way to reference client side Maven artifacts of JBoss EAP, 
  I just have not yet figured out the right list of Maven artifacts that are
  required for a remote JBoss client.

- Start JBoss EAP 6.2 using a configuration that also deploys and runs a default 
  HornetQ server.

- Start JBoss Fuse 6.1

- From the Karaf shell enter:
  ```
  features:addurl mvn:org.apache.camel.demo.camel-hornetq-amq/features/1.0.0/xml/features
  features:install camel-hornetq-amq-demo
  ```

- Check that all bundles get deployed and start up successfully. 



## Running

- Send a test message to the queue 'testQueue' on HornetQ.

- Observe the JBoss Fuse log file and verify that the message got routed to 
  the embedded ActiveMQ broker, destination OUT. You can also connect to the
  ActiveMQ broker using JMX and verify that messages got enqueued to queue OUT.

- Sample logging output
  ```
  20:56:03,169 | INFO  | 0 - timer://Push | PushMessages   | 142 - org.apache.camel.camel-core - 2.12.0.redhat-610379 | Hello World.
  20:56:03,192 | INFO  | sumer[testQueue] | hornetq-to-amq | 142 - org.apache.camel.camel-core - 2.12.0.redhat-610379 | GOT MESSAGE ################################
  20:56:03,192 | INFO  | sumer[testQueue] | hornetq-to-amq | 142 - org.apache.camel.camel-core - 2.12.0.redhat-610379 | REDELIVERED: false
  20:56:03,210 | INFO  | sumer[testQueue] | hornetq-to-amq | 142 - org.apache.camel.camel-core - 2.12.0.redhat-610379 | AFTER CAMEL PROCESSOR ######################
  ```

- Additional logging useful for trouble shooting JNDI related problems:
```
  log:set TRACE org.springframework.jndi
  log:set TRACE org.jboss.naming.remote
```

