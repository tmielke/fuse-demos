# Routing

This project contains the actual Camel route definitions and JMS endpoint configuration using 
OSGi blueprint configuration.

That configuration is in 
src/main/resources/OSGI-INF/blueprint/camel-context.xml

It defines two Camel routes, one using the camel-jdbc component, the other
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
