# Camel-JMS-JDBC demo

Tested against JBoss Fuse 6.0 (RC1).

A Camel based demo that shows how route messages through JMS and JDBC.
Access to an external JDBC database is needed to run this demo.
The demo currently connects to a local MySQL instance but can be easily 
reconfigured to work with any other JDBC database.

The demo defines two similar Camel routes. However one route uses the 
camel-sql component, the other route uses the camel-jdbc component. 
Both routes consume messages from a JMS destination, then call the JDBC database
and finally send the message to another JMS destination.


The two Camel routes simply read:

```xml
  <camelContext xmlns="http://camel.apache.org/schema/blueprint">
    <!-- Sample route using the camel-sql component -->
    <route id="jms-sql-jms">
      <from uri="amq:SQL_IN" />
      <log message="REDELIVERED: ${header.JMSRedelivered}" />
      <convertBodyTo type="java.lang.Object[]" />
      <to uri ="sql:INSERT INTO USERS values (#, #, #, #)" />
      <setBody>
        <constant>Data correctly written to database.</constant>
      </setBody>
      <to uri="log:AfterJDBC?level=INFO&amp;showAll=true" />
      <to uri="amq:queue:SQL_OUT" />
    </route>

    <!--  Sample route using camel-jdbc component -->
    <route id="jms-jdbc-jms">
      <from uri="amq:JDBC_IN" />
      <log message="REDELIVERED: ${header.JMSRedelivered}" />
      <to uri="jdbc:mysql-db?resetAutoCommit=false" />
      <setBody>
        <constant>Data correctly written to database.</constant>
      </setBody>
      <to uri="log:AfterJDBC?level=INFO&amp;showAll=true" />
      <to uri="amq:queue:JDBC_OUT" />
    </route>
  </camelContext>
```

Both the camel-jdbc and the camel-sql components are configured to use the same
MySQL database table.
The first route (id=jms-sql-jms) also uses a custom converter (provided with 
the demo) that converts a comma separated string into an array of String. This 
is needed for filling the SQL statement with values in Camel 2.10.


## Directory structure
* datasource - contains the JDBC data source definition
* routing - defines the Camel routes and JMS endpoints
* feature - contains the Apache Karaf features definition that allows for easy installation of this example


## PRE-REQUISITE CONFIGURATION:
This demo connect to a local MySQL database called Test by default. 
If you like to use a different DB, then reconfigure 
datasource/src/main/resources/OSGI-INF/blueprint/datasource.xml 
accordingly. 


### Setting up MySQL
- start a local MySQL instance (or any other JDBC database)
- Using the MySQL shell or workbench invoke 
  - `CREATE DATABASE test ;`
  - `CREATE TABLE Users (firstname VARCHAR(50) NOT NULL, lastname VARCHAR(50), login VARCHAR(12), password VARCHAR(20), PRIMARY KEY (login)) ;`
  - `INSERT INTO Users values ('Joe', 'Doe', 'jdoe', 'secret') ;`


## COMPILING
Run 
  mvn clean install
from the top level demo directory.


## DEPLOYING
- edit etc/users.properties of the JBoss Fuse 6.0 installation and uncomment
  the line 
  #admin=admin,admin
- Start JBoss Fuse 6.0 or higher
- `features:addurl mvn:org.apache.camel.demo.camel-jms-jdbc/features/1.0.0/xml/features`
- `features:install camel-jms-jdbc-demo`

## RUNNING
Check that all bundles got deployed and started successfully. 

### Using jconsole to send JMS messages
Open `jconsole` and connect to the running JBoss Fuse instance. If the instance is running locally, connect to
the process called `org.apache.karaf.main.Main`.

#### Testing the route using camel-sql component:
On the MBeans tab, navigate to `org.apache.activemq` &rarr; `fusemq` &rarr; `Queue` &rarr; `SQL_IN`.  
Using the `sendTextMessage(String body, String user, String password)` operation send a message.
For the first argument use the string `Sam, Smith, ssmith, secret`.  For the second and third password, 
use the username admin and password admin.
Observe ServiceMix log file and verify that the message got routed to JMS destination SQL_OUT.
If you click the browse() operation for the queue SQL_OUT, the message text should say
"Data correctly written to database".
Check the JDBC database and verify that this record got written into the Users table.

#### Testing the route using camel-jdbc component:
On the MBeans tab, navigate to `org.apache.activemq` &rarr; `fusemq` &rarr; `Queue` &rarr; `JDBC_IN`.
Using the `sendTextMessage(String body, String user, String password)` operation send a message.
For the first argument use the string `INSERT INTO Users VALUES('Jon', 'Jackson', 'jjackson', 'secret')`.  
For the second and third password, use the username admin and password admin.
Observe ServiceMix log file and verify that all messages got routed to 
JMS destination JDBC_OUT. 
If you click the browse() operation for the queue SQL_OUT, the message text should say
"Data correctly written to database".
Check the JDBC database and verify that this record got written into the Users table.
