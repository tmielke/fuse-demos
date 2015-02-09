#Fabric Endpoint Example

This demo uses the Camel Fabric Master component to ensure only a single Camel consumer is active at any point in 
time for a particular endpoint. It configures two routes, a consumer route that consumes messages from an ActiveMQ
broker and a client route that produces messages to the ActiveMQ broker. 
Although multiple instances of the consumer route may be started at runtime, only one route instance will 
actually consume messages. Any other started instances will be inactive until they can register in the Camel 
registry. The consumer route consumes from 

```xml
<from uri="master:fabrictestendpoint:activemq:queue:FabricEndpointTest"/>
```

and hence registers its endpoint in the Fabric registry under the cluster id fabrictestendpoint.
Only one endpoint can be active in the registry at runtime, this is ensured by using the Fabric master component.


See https://access.redhat.com/documentation/en-US/Red_Hat_JBoss_Fuse/6.1/html/Apache_Camel_Component_Reference/Master.html 
for more details on the Camel Fabric Master Component. 


##Project layout

The Maven projects contained within are as follows:

* `client` - The client used to connect to the service
* `consumer` - The module that provides the service trough it's fabric endpoint
* `features` - Contains an XML features file used to simplify the installation of the bundles.

## Requirements:

* JBoss Fuse 6.1.0 (http://www.jboss.org/jbossfuse)
* Maven 3.x (http://maven.apache.org/)
* Java SE 6

## Building the example

Build this project so bundles are deployed into your local maven repo

    <project home> $ mvn clean install

## Prepare Fabric

Start JBoss Fuse

    <JBoss Fuse home>  $ bin/fuse



## Create consumer and client profiles and container


Run the following commands by copy-pasting to the Karaf shell.
Its important that only the root container runs the jboss-fuse-full profile. 

``` 
fabric:create --clean --profile jboss-fuse-full --wait-for-provisioning

profile-create --parents feature-camel --parents karaf fabric-endpoint-base
profile-edit --repositories mvn:com.fusesource.examples/fabric-endpoint-features/1.0-SNAPSHOT/xml/features fabric-endpoint-base
profile-edit --repositories mvn:org.apache.activemq/activemq-karaf/5.9.0.redhat-610379/xml/features fabric-endpoint-base
profile-edit --features activemq-camel fabric-endpoint-base

profile-create --parents fabric-endpoint-base fabric-endpoint-consumer
profile-edit --features fabric-endpoint-consumer fabric-endpoint-consumer
container-create-child --profile fabric-endpoint-consumer root fabric-endpoint-consumer
container-create-child --profile fabric-endpoint-consumer root fabric-endpoint-consumer-2

profile-create --parents fabric-endpoint-base fabric-endpoint-client
profile-edit --features fabric-endpoint-client fabric-endpoint-client
container-create-child --profile fabric-endpoint-client root fabric-endpoint-client
```


This will create three profiles: 
- `fabric-endpoint-base` is the base profile definition for both consumer and client
- `fabric-endpoint-consumer` is the profile for deploying the master/slave Camel route
- `fabric-endpoint-client` is the profile for deplyong the client side Camel route

Also we have already created three child container:
- `fabric-endpoint-consumer` runs one instance of the master / slave Camel route
- `fabric-endpoint-consumer-2` runs the second instance of the master / slave Camel route
- `fabric-endpoint-client` runs the client side Camel route that invokes the master / slave Camel route.


Check the logs of the fabric-endpoint-client container, it should print every second:

```
2015-02-09 16:26:21,335 | INFO  | #3 - timer://foo | fabric-client | rg.apache.camel.util.CamelLogger  176 | 100 - org.apache.camel.camel-core - 2.12.0.redhat-610379 | client said: Hello Consumer!
```

The log of either container fabric-endpoint-consumer or fabric-endpoint-consumer-2 should print 

```
2015-02-09 16:26:50,380 | INFO  | ricEndpointTest] | fabric-consumer | rg.apache.camel.util.CamelLogger  176 | 100 - org.apache.camel.camel-core - 2.12.0.redhat-610379 | Message received : client said: Hello Consumer!
```

Stop the container fabric-endpoint-consumer and logging should continue in container fabric-endpoint-consumer-2, illustrating a successful failover to the second Camel route instance that ran as a slave before.

