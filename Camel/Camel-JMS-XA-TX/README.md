# Camel JMS XA Transactions Demo

Tested against JBoss Fuse 6.2.1

A Camel demo that shows how to use XA transactions with Camel.
This demo configures a Camel route that consumes messages from one
ActiveMQ broker (the one embedded in JBoss Fuse) and produces messages
to a second external ActiveMQ broker.


The Camel route simply reads:

```xml
<route id="jms-jms-xa">
  <from uri="activemq:queue:jms-xa-demo.in" />
  <transacted ref="requiredJta"/> 
  <log message="Received msg with JMSRedelivered:${header.JMSRedelivered}" />
  <process ref="testProcessor" />
  <to uri="activemq-2:queue:jms-xa-demo.out" />
</route>
```

There is no producer provided with this demo. In order to push messages onto 
the test.in queue, use any other external tool such as the ActiveMQ web 
console or jconsole. Both have capabilities for sending messages.

The only interesting files are:
- `src/main/resources/OSGI-INF/blueprint/blueprint.xml`
- `src/main/java/com/fusesource/support/TestProcessor.java`


## Compiling
- mvn install


## Deploying
- Start JBoss Fuse
- `osgi:install -s mvn:org.apache.camel.demo/camel-jms-xa-tx-demo/0.1`

## Running

- Start a second broker using the provided ./activemq.xml broker configuration:
  This requires a standalone ActiveMQ installation. From its bin/ folder run
    `./activemq console xbean:file:/<full-path-to-this-demo>/optionalConfig/activemq.xml`
  This configuration will make the second broker listen on `tcp://localhost:61618`.


- Send one or more messages to the source broker queue jms-xa-demo.in:
  `activemq:producer --brokerUrl tcp://localhost:61616 --user admin --password admin --destination jms-xa-demo.in --persistent true --messageCount 1`

- Watch the log. The msg will get routed within Camel using XA transactions.
  The use of XA can be seen by observing the transaction Ids in the ServiceMix 
  log file. These Ids typically have long numbers compared to local JMS trans-
  actions that would not have such numbers.
  An example logging output is shown below.

- Watch the SMX log. The msg will get routed within Camel using XA transactions.
  The use of XA can be seen by observing the transaction Ids in the ServiceMix 
  log file. These Ids typically have long numbers compared to local JMS trans-
  actions that would not have such numbers.
  An example logging output is shown below.


## NOTE: 
For more verbose logging about the use of XA transactions, this logging 
configuration can be applied on the Karaf shell:

```
log:set DEBUG org.apache.activemq.transaction
log:set DEBUG org.springframework.transaction
log:set DEBUG org.springframework.jms.connection.JmsTransactionManager
log:set DEBUG org.springframework.orm.jpa.JpaTransactionManager
log:set TRACE org.apache.geronimo.transaction.manager.WrapperNamedXAResource
log:set DEBUG org.apache.geronimo.transaction.log
log:set DEBUG org.jencks
```

This will log every tx.begin, tx.prepare and tx.commit operation to data/log/fuse.log.


## Logging output

With proper logging turned on (see above) the following logging output should 
appear:

```
13:37:31,483 | DEBUG | [jms-xa-demo.in] | JtaTransactionManager  | 202 - org.apache.servicemix.bundles.spring-tx - 3.2.12.RELEASE_2 | Creating new transaction with name [JmsConsumer[jms-xa-demo.in]]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
13:37:31,483 | TRACE | [jms-xa-demo.in] | WrapperNamedXAResource | 163 - org.apache.aries.transaction.manager - 1.3.0 | Start called on XAResource activemq.defaultn  |  Xid: [Xid:globalId=211d37ffffffce511006f72672e6170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000,length=64,branchId=1000ffffffacffffff9836ffffffce511006170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000,length=64]n  |  flags: TMNOFLAGS
13:37:31,484 | DEBUG | .0.1:62553@61616 | XATransaction          | 185 - org.apache.activemq.activemq-osgi - 5.11.0.redhat-621084 | XA Transaction new/begin : XID:[1197822575,globalId=211d37ffffffce511006f72672e6170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000,branchId=1000ffffffacffffff9836ffffffce511006170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000]
13:37:31,949 | DEBUG | [jms-xa-demo.in] | JtaTransactionManager  | 202 - org.apache.servicemix.bundles.spring-tx - 3.2.12.RELEASE_2 | Participating in existing transaction
13:37:31,949 | INFO  | [jms-xa-demo.in] | jms-jms-xa             | 198 - org.apache.camel.camel-core - 2.15.1.redhat-621084 | Received msg with JMSRedelivered:false
13:37:31,961 | TRACE | [jms-xa-demo.in] | WrapperNamedXAResource | 163 - org.apache.aries.transaction.manager - 1.3.0 | Start called on XAResource activemq.defaultn  |  Xid: [Xid:globalId=211d37ffffffce511006f72672e6170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000,length=64,branchId=2000ffffffacffffff9836ffffffce511006170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000,length=64]n  |  flags: TMNOFLAGS
13:37:31,963 | DEBUG | [jms-xa-demo.in] | JtaTransactionManager  | 202 - org.apache.servicemix.bundles.spring-tx - 3.2.12.RELEASE_2 | Initiating transaction commit
13:37:31,963 | TRACE | [jms-xa-demo.in] | WrapperNamedXAResource | 163 - org.apache.aries.transaction.manager - 1.3.0 | End called on XAResource activemq.source  |  Xid: [Xid:globalId=211d37ffffffce511006f72672e6170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000,length=64,branchId=2000ffffffacffffff9836ffffffce511006170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000,length=64]n  |  flags: TMSUCCESS remaining: 67108864
13:37:31,963 | TRACE | [jms-xa-demo.in] | WrapperNamedXAResource | 163 - org.apache.aries.transaction.manager - 1.3.0 | End called on XAResource activemq.target  |  Xid: [Xid:globalId=211d37ffffffce511006f72672e6170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000,length=64,branchId=1000ffffffacffffff9836ffffffce511006170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000,length=64]n  |  flags: TMSUCCESS remaining: 67108864
13:37:31,964 | TRACE | [jms-xa-demo.in] | WrapperNamedXAResource | 163 - org.apache.aries.transaction.manager - 1.3.0 | Prepare called on XAResource activemq.source  |  Xid: [Xid:globalId=211d37ffffffce511006f72672e6170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000,length=64,branchId=1000ffffffacffffff9836ffffffce511006170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000,length=64]
13:37:31,964 | DEBUG | .0.1:62553@61616 | XATransaction          | 185 - org.apache.activemq.activemq-osgi - 5.11.0.redhat-621084 | XA Transaction prepare: XID:[1197822575,globalId=211d37ffffffce511006f72672e6170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000,branchId=1000ffffffacffffff9836ffffffce511006170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000]
13:37:31,965 | TRACE | [jms-xa-demo.in] | WrapperNamedXAResource | 163 - org.apache.aries.transaction.manager - 1.3.0 | Prepare called on XAResource activemq.target  |  Xid: [Xid:globalId=211d37ffffffce511006f72672e6170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000,length=64,branchId=2000ffffffacffffff9836ffffffce511006170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000,length=64]
13:37:31,969 | TRACE | [jms-xa-demo.in] | WrapperNamedXAResource | 163 - org.apache.aries.transaction.manager - 1.3.0 | Commit called on XAResource activemq.source  |  Xid: [Xid:globalId=211d37ffffffce511006f72672e6170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000,length=64,branchId=1000ffffffacffffff9836ffffffce511006170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000,length=64]n  |  onePhase:false
13:37:31,969 | DEBUG | .0.1:62553@61616 | XATransaction          | 185 - org.apache.activemq.activemq-osgi - 5.11.0.redhat-621084 | XA Transaction commit onePhase:false, xid: XID:[1197822575,globalId=211d37ffffffce511006f72672e6170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000,branchId=1000ffffffacffffff9836ffffffce511006170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000]
13:37:31,971 | TRACE | [jms-xa-demo.in] | WrapperNamedXAResource | 163 - org.apache.aries.transaction.manager - 1.3.0 | Commit called on XAResource activemq.target  |  Xid: [Xid:globalId=211d37ffffffce511006f72672e6170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000,length=64,branchId=2000ffffffacffffff9836ffffffce511006170616368652e61726965732e7472616e73616374696f6e0000000000000000000000000000,length=64]n  |  onePhase:false
```
