# Datasource bundle
This bundle defines the JDBC `DataSource`s that will be used for accessing the 
relational database.


## Blueprint
We are using Blueprint to define the data source bean and publish it into the OSGi Service Registry.  Have a look at
`src/main/resources/OSGI-INF/blueprint/datasource.xml` for more details about how this done.

## Checking the OSGi Service Registry
After the bundle is started, we can use the Fuse ESB Enterprise console to look at the registered objects in the registry.

First, find the bundle id for the bundle called " ESB :: Demo :: Camel-JMS-JDBC :: DataSource" by using the `osgi:list` command.
You can use `grep` to filter the list of bundles and quickly find the right one.

    FuseESB:karaf@root> osgi:list | grep Transactions
    [ 268] [Active     ] [Created     ] [       ] [   60] ESB :: Demo :: Camel-JMS-JDBC :: DataSource (1.0.0)
    [ 269] [Active     ] [            ] [Started] [   60] ESB :: Demo :: Camel-JMS-JDBC :: Routing (1.0.0)

In this example, the bundle id is 268.  Using the `osgi:ls` command, we can see that this bundle is publishing this DataSource as an OSGi service:
This is what the `osgi:ls` output looks like

Camel :: Demo :: Camel-JMS-JDBC:: DataSource (268) provides:
------------------------------------------------------------
datasource.name = MySQL
objectClass = javax.sql.DataSource
osgi.jndi.service.name = jdbc/mysqld
osgi.service.blueprint.compname = dbcp
service.id = 400
----
objectClass = org.osgi.service.blueprint.container.BlueprintContainer
osgi.blueprint.container.symbolicname = org.apache.camel.demo.camel-jms-jdbc.datasource
osgi.blueprint.container.version = 1.0.0
service.id = 401
