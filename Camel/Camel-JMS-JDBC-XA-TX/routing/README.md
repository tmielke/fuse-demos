# Routing

This project contains the actual Camel route definition and JMS endpoint configuration using 
OSGi blueprint configuration.

That configuration is in 
src/main/resources/OSGI-INF/blueprint/camel-context.xml

It defines two Camel route, one using the camel-jdbc component, the other
using the camel-sql component.

In order to fill in data into the SQL statement created by the camel-sql 
component, it requires an object array if more than one value is to be 
replaced in the SQL statement. 

In the routing step
  <to uri ="sql:INSERT INTO USERS values (#, #, #, #)" />

4 values are inserted into the SQL statement, so an object array of 4 strings
is required. The source of this Camel route however is a JMS endpoint which
receives a single JMS message at a time. 
The idea here is that we assume a JMS TextMessage with a comma separated list of 
values and use a custom Camel converter to convert this TextMessage into an 
object array.
This Camel converter class is defined in 
  src/main/java/org/apache/servicemix/demo/camel_jms_jdbc_xa_demo/DemoJDBCConverter.java


Perhaps also take a minute to study the configuration of the JMS component. 
Note the ConnectionFactory used is of type org.apache.activemq.pool.JcaPooledConnectionFactory. 
When using XA transactions and enlisting into the Aries transaction manager, 
then this ConnectionFactory is needed. In addition it needs to be configured 
for a unique name property as that name identifies the XA resource when Aries 
writes its transaction log file. 

The same unique name needs to be configured for ActiveMQResourceManager bean 
definition. The resource manager is called on startup and asks the broker for
any outstanding transactions that are then to be committed or rolled back. 

Further please notice the cacheLevelName is set to CACHE_CONNECTION which is the
recommendation for XA transactions.

And finally notice the brokerURL property of the ActiveMQXAConnectionFactory uses 
the url failover:(tcp://localhost:61616)?jms.prefetchPolicy.all=1. Using a 
prefetch of just one message makes sense since we don't cache JMS sessions and 
JMS consumers at Spring level. So a new JMS consumer gets created for every new
message that is to be consumed. If the default prefetch of 1000 was used we 
would potentially prefetch up to 1000 messages to each consumer although 
only one message is processed before the consumer is destroyed again 
(which rejects all remaining 999 messages). 

