# Camel JMS Local Transactions Demo

Tested against JBoss Fuse 6.2.1

A Camel demo that shows how to use local JMS transactions with Camel.
This demo configures a Camel route that consumes messages from an
ActiveMQ broker (the one embedded in JBoss Fuse) and processes the message
in an Camel Processor bean.


The Camel route simply reads:

```xml
<route id="jms-jms-xa">
  <from uri="activemq:queue:jms-localtx-demo.in" />
  <transacted ref="requiredJta"/> 
  <log message="Received msg with JMSRedelivered:${header.JMSRedelivered}" />
  <process ref="testProcessor" />
</route>
```

The only interesting files are:
- `src/main/resources/OSGI-INF/blueprint/blueprint.xml`
- `src/main/java/com/fusesource/support/TestProcessor.java`


Please study the file `src/main/resources/OSGI-INF/blueprint/blueprint.xml` and make sure to understand it.
Please pay attention to the configuration of the camel-jms endpoint.
Note, there is a second camel-jms endpoint configuration that is currently commented. 
We will use that later in this exercise.


## Compiling
- mvn install


## Deploying
- Start JBoss Fuse
- `osgi:install -s mvn:org.apache.camel.demo/camel-jms-localtx-demo/1.0`

## Exercises

- Send one or more messages to the source broker queue jms-localtx-demo.in:
  `activemq:producer --brokerUrl tcp://localhost:61616 --user admin --password admin --destination jms-localtx-demo.in --persistent true --messageCount 1`

- Watch the log. The msg will get routed within Camel using local JMS transactions.
  The use of transactions can be seen by observing the transaction Ids in the JBoss Fuse 
  log file. These Ids typically have long numbers compared to local JMS trans-
  actions that would not have such numbers.
  An example logging output is shown below.

- Increase transaction logging by running
```
log:set DEBUG org.apache.activemq.TransactionContext
```
  This will log every JMS transaction begin and commit operation to data/log/fuse.log.


- Send another message and verbose observe logging. It should log lines similar to
```
11:04:40,836 | INFO  | localtx-demo.in] | jms-jms-localtx | 198 - org.apache.camel.camel-core - 2.15.1.redhat-621084 | Received msg with JMSRedelivered:false
11:04:40,854 | DEBUG | .0.1:51521@61616 | LocalTransaction| 185 - org.apache.activemq.activemq-osgi - 5.11.0.redhat-621084 | commit: TX:ID:Mac.fritz.box-51392-1451294252828-7:1:107 syncCount: 2
```  


- Reconfigure the TestProcessor bean to throw an exception every two messages.
  Run these commands on the Karaf shell:
  ```
  config:edit org.apache.camel.demo.camel_jms_localtx_demo
  config:propset simulateProcessingError true
  config:update
  ```
  
  This will make the Camel bean raise a RuntimeException on every second message
  it receives. This bean can be used to simulate a processing error in the Camel route.
  Send two message to the source broker and observe that the second message causes
  the Camel processor to raise an exception, which causes a transaction rollback.
  The log should contain this message:
  `rollback: TX:ID:Mac.fritz.box-51392-1451294252828-29:1:1 syncCount: 2`
  Due to redelivery config, the message gets redelivered to the Camel route but 
  this time the Camel processor won't raise an exception (only every second msg causes
  an exception to be raised) and the transaction gets committed successfully this time.

  Question: What happens when you configure the Camel processor for 
  ```xml
  <property name="errorAfterMsgs" value="1" />
  ```


- Reset the TestProcessor bean to its default configuration:
  Run these commands on the Karaf shell:
  ```
  config:edit org.apache.camel.demo.camel_jms_localtx_demo
  config:propset simulateProcessingError false
  config:update
  ```


## Optional exercise:
- Download a standalone ActiveMQ broker from 
  http://origin-repository.jboss.org/nexus/content/groups/m2-proxy/org/apache/activemq/apache-activemq/5.11.0.redhat-621084/apache-activemq-5.11.0.redhat-621084-bin.tar.gz
  untar the ActiveMQ distribution to a folder of your choice. 


- cd into folder apache-activemq-5.11.0.redhat-621084
  

- Start an external broker using the broker configuration file provided in 
  optionalConfig/activemq.xml of this exercise demo

    `bin/activemq console xbean:file:/<full-path-to-this-demo>/optionalConfig/activemq.xml`
  This configuration will make the second broker listen on `tcp://localhost:61618`.


- Edit `src/main/resources/OSGI-INF/blueprint/blueprint.xml` and uncomment the 
  section "Target ActiveMQ Configuration" plus uncomment the line 
  ```xml
  <!-- to uri="activemq-2:queue:jms-localtx-demo.out" /-->
  ```
  within the Camel route definition.

- run mvn install to re-build the demo

- On the Karaf shell run
  `osgi:update <bundleid>`
  using the bundle id of the bundle "Camel JMS Local TX Demo (1.0.0)".
  You can get the bundleid by running `osgi:list`.


- Send another one or more messages to queue jms-localtx-demo.in.
  Note, there are two commit messages now:
```
11:25:19,613 | DEBUG | localtx-demo.in] | TransactionContext| 185 - org.apache.activemq.activemq-osgi - 5.11.0.redhat-621084 | Commit: TX:ID:Mac.fritz.box-51392-1451294252828-45:6:1 syncCount: 0
11:25:19,631 | DEBUG | localtx-demo.in] | TransactionContext| 185 - org.apache.activemq.activemq-osgi - 5.11.0.redhat-621084 | Commit: TX:ID:Mac.fritz.box-51392-1451294252828-43:1:17 syncCount: 1
```


- Stop the target broker. Send another single message and check the logs. 
  Notice the message gets processed three times (initial delivery plus 2 redeliveries) 
  before it gets moved to ActiveMQ.DLQ.
  Also notice on second and third attempt, the Camel route logs:
  `Received msg with JMSRedelivered:true`
  because the JMSRedelivered header of the message was changed from false to true,
  indicating the message got redelivered.
  Run activemq:dstat to confirm there is one message on ActiveMQ.DLQ.
  Restart the target broker.



## Logging output

With proper logging turned on (see above) the following logging output should 
appear when using only one ActiveMQ broker:

```
11:25:19,598 | DEBUG | localtx-demo.in] | TransactionContext               | 185 - org.apache.activemq.activemq-osgi - 5.11.0.redhat-621084 | Begin:TX:ID:Mac.fritz.box-51392-1451294252828-43:1:17
11:25:19,599 | INFO  | localtx-demo.in] | jms-jms-localtx                  | 198 - org.apache.camel.camel-core - 2.15.1.redhat-621084 | Received msg with JMSRedelivered:false
11:25:19,613 | DEBUG | localtx-demo.in] | TransactionContext               | 185 - org.apache.activemq.activemq-osgi - 5.11.0.redhat-621084 | Commit: TX:ID:Mac.fritz.box-51392-1451294252828-43:1:17 syncCount: 1
```

With two broker instances, the logging reads:
```
11:30:27,733 | DEBUG | localtx-demo.in] | TransactionContext               | 185 - org.apache.activemq.activemq-osgi - 5.11.0.redhat-621084 | Begin:TX:ID:Mac.fritz.box-51392-1451294252828-43:1:18
11:30:27,734 | INFO  | localtx-demo.in] | jms-jms-localtx                  | 198 - org.apache.camel.camel-core - 2.15.1.redhat-621084 | Received msg with JMSRedelivered:false
11:30:27,747 | DEBUG | localtx-demo.in] | TransactionContext               | 185 - org.apache.activemq.activemq-osgi - 5.11.0.redhat-621084 | Begin:TX:ID:Mac.fritz.box-51392-1451294252828-45:7:1
11:30:27,748 | DEBUG | localtx-demo.in] | TransactionContext               | 185 - org.apache.activemq.activemq-osgi - 5.11.0.redhat-621084 | Commit: TX:ID:Mac.fritz.box-51392-1451294252828-45:7:1 syncCount: 0
11:30:27,765 | DEBUG | localtx-demo.in] | TransactionContext               | 185 - org.apache.activemq.activemq-osgi - 5.11.0.redhat-621084 | Commit: TX:ID:Mac.fritz.box-51392-1451294252828-43:1:18 syncCount: 1
```

