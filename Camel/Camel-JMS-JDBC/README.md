# Camel-JMS-JDBC demo

Tested against JBoss Fuse 6.2.1

A Camel demo that shows how to route messages through the camel-jms and camel-jdbc
component. It uses an embedded Derby database, so that no external DB is 
required, however can be easily modified to work with any external JDBC 
database.

The demo defines two similar Camel routes. However one route uses the 
camel-sql component, the other route uses the camel-jdbc component.
Both routes consume messages from a different JMS destination, then call the 
JDBC database and finally send the message to another JMS destination.


The two Camel routes simply read:

```xml
    <camelContext xmlns="http://camel.apache.org/schema/blueprint">
        <route id="jms-sql-jms">
           <from uri="amq:SQL_IN" />
            <log message="REDELIVERED: ${header.JMSRedelivered}" />
            <!--  Camel 2.10 does not support named parameters yet -->
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
            <!-- when using XA transactions, need to set resetAutoCommit=false
                otherwise this error may be thrown:
                "java.sql.SQLException: Can't set autocommit to 'true' on an XAConnection"
            -->
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
embedded Derby database table.
The first route (id=jms-sql-jms) also uses a custom converter (provided with 
the demo) that converts a comma separated string into an array of String. This 
is needed for filling the SQL statement with values in Camel 2.10.

While embedded Derby can auto-create a database it does not create tables 
automatically. To create tables, I have configured an extra Camel route that 
only fires once and needs to be started manually:

```xml
        <route id="create-db-tables" autoStartup="false">
           <from uri="timer:CreateDB?delay=2000&amp;repeatCount=1" />
            <log message="Creating DB Table using" />
            <setBody>
                <constant><![CDATA[CREATE TABLE Users (firstname VARCHAR(50) NOT NULL, lastname VARCHAR(50), login VARCHAR(12), password VARCHAR(20), PRIMARY KEY (login))]]></constant>
            </setBody>
            <log message="${body}" />
            <to uri="jdbc:mysql-db?resetAutoCommit=false" />
        </route>
```
It executes the SQL statement to create a table called 'Users' directly from 
the Camel route via the camel-sql component.
Note this route sets autoStartup="false" and hence needs to be started 
manually. Steps on how to run this route are given below.


## Demo Directory structure
* `datasource` - Contains the JDBC data source definition and exports it as 
   OSGi service.
* `routing` - Defines the Camel routes and JMS endpoints.
* `feature` - Contains the Apache Karaf features definition that allows for 
   easy installation of this demo.


## PRE-REQUISITE CONFIGURATION:
If you want to run this demo against any other database than embedded Derby,
then reconfigure 
`datasource/src/main/resources/OSGI-INF/blueprint/datasource.xml`
accordingly. Provided next are the optional steps needed to configure and run an
external MySQL database:


### Example: Setting up MySQL
- Start a local MySQL instance (or any other JDBC database)
- Using the MySQL shell or workbench invoke 
  - `CREATE DATABASE test ;`
  - `CREATE TABLE Users (firstname VARCHAR(50) NOT NULL, lastname VARCHAR(50), login VARCHAR(12), password VARCHAR(20), PRIMARY KEY (login)) ;`
  - `INSERT INTO Users values ('Joe', 'Doe', 'jdoe', 'secret') ;`
  - Update `datasource/src/main/resources/OSGI-INF/blueprint/datasource.xml`, 
    Uncomment the bean with id="mysql-ds" and export is as its own OSGi service.
    You can comment the embedded Derby database definition (bean with id="derby-ds").

## COMPILING
Run `mvn clean install`
from the top level demo directory.


## DEPLOYING
- If you have not already done edit `etc/users.properties` of the JBoss Fuse 
  6.2.1 installation and uncomment the line 
  `#admin=admin,admin,...`
- Start JBoss Fuse 6.2.1 or higher
- Run these Karaf commands:
  `features:addurl mvn:org.apache.camel.demo.camel-jms-jdbc/features/2.0.0/xml/features`
  `features:install camel-jms-jdbc-demo`
  to deploy the demo and its bundles.
- Run `osgi:list | grep Camel-JMS-JDBC` and note the bundle id of the DataSource bundle.
```
    [ 268] [Active     ] [Created     ] [       ] [   60] ESB :: Demo :: Camel-JMS-JDBC :: DataSource (2.0.0)
    [ 269] [Active     ] [            ] [Started] [   60] ESB :: Demo :: Camel-JMS-JDBC :: Routing (2.0.0)
```
In this example, the bundle id is 268. Using the `osgi:ls 268` command, we can 
see that this bundle is publishing this DataSource as an OSGi service:
This is what the `osgi:ls` output looks like
```
Camel :: Demo :: Camel-JMS-JDBC:: DataSource (270) provides:
------------------------------------------------------------
datasource.name = DerbyDS
objectClass = [javax.sql.DataSource]
osgi.service.blueprint.compname = derby-ds
service.id = 621
service.ranking = 5
```

## RUNNING
Check that all bundles got deployed and started successfully. 

In order to create the database table in the embedded Derby database, active 
the Camel route "create-db-tables" via the Karaf shell command:
`camel:route-start create-db-tables camel-jms-jdbc-demo`

Examine the logging output, which should contain these lines:
```
INFO  | l Console Thread | BlueprintCamelContext | 198 - org.apache.camel.camel-core - 2.15.1.redhat-621084 | Route: create-db-tables started and consuming from: Endpoint[timer://CreateDB?delay=2000&repeatCount=1]
INFO  | timer://CreateDB | create-db-tables     | 198 - org.apache.camel.camel-core - 2.15.1.redhat-621084 | Creating DB Table using
INFO  | timer://CreateDB | create-db-tables     | 198 - org.apache.camel.camel-core - 2.15.1.redhat-621084 | CREATE TABLE Users (firstname VARCHAR(50) NOT NULL, lastname VARCHAR(50), login VARCHAR(12), password VARCHAR(20), PRIMARY KEY (login))
```

That means the database table got created successfully. 
If you find any errors, you need to resolve them before moving forward in this demo.


#### Testing the route using camel-sql component:
From the Karaf shell send a JMS message to the SQL_IN JMS queue via this Karaf command:
`activemq:producer --user admin --password admin --destination queue://SQL_IN --persistent true --messageCount 1 --message "Sam, Smith, ssmith, secret"`

Observe the log file and verify that the message got routed to JMS destination SQL_OUT.
Run Karaf command `activemq:dstat` to verify the JMS destination SQL_OUT got created.
If you browse the JMS destination SQL_OUT using the Karaf command
`activemq:browse --amqurl tcp://localhost:61616 --user admin --password admin  SQL_OUT`
then the message written to this destination should have this text: 
`Data correctly written to database`


#### Testing the route using camel-jdbc component:
From the Karaf shell send a JMS message to the JDBC_IN JMS queue via this Karaf command:
`activemq:producer --user admin --password admin --destination queue://JDBC_IN --persistent true --messageCount 1 --message "INSERT INTO Users VALUES('Jon', 'Jackson', 'jjackson', 'secret')"`

Observe the log file and verify that all messages got routed to the JMS 
destination JDBC_OUT.
Run Karaf command `activemq:dstat` to verify the JMS destination JDBC_OUT got created.
If you browse the JMS destination JDBC_OUT using the Karaf command
`activemq:browse --amqurl tcp://localhost:61616 --user admin --password admin  JDBC_OUT`
then the message written to this destination should have this text: 
`Data correctly written to database`


#### Optional Testing Steps
1) What happens if you send the above message again? 

2) Stop the bundle `Camel :: Demo :: Camel-JMS-JDBC:: DataSource (2.0.0)`
using `osgi:stop <bundleid>`.
Send another message to one of the JMS destinations that the Camel routes listen on.
What happens this time? Why is there no error raised?

3) Run `camel:route-info jms-sql-jms`. What does the count for "Exchanges Inflight" say? Why?

4) What happens if you restart the datasource bundle?

5) What happens if you don't restart the database bundle within five minutes?
