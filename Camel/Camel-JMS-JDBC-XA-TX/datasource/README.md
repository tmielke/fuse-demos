# Datasource bundle
This bundle defines the JDBC `DataSource`s that will be used for accessing the 
relational database.


## Blueprint
We are using Blueprint to define the data source bean and publish it into the OSGi Service Registry.  Have a look at
`src/main/resources/OSGI-INF/blueprint/datasource.xml` for more details about how this done.

Due to bug http://fusesource.com/issues/browse/ENTESB-633 it is not possible to
have the configured JDBC DataSource getting auto-enlisted into Aries.
Instead a workaround solution is presented in src/main/resources/OSGI-INF/blueprint/datasource.xml


## Checking the OSGi Service Registry
After the bundle is started, we can use the Fuse ESB Enterprise console to look at the registered objects in the registry.

First, find the bundle id for the bundle called "Fuse By Example :: Transactions :: Datasource" by using the `osgi:list` command.
You can use `grep` to filter the list of bundles and quickly find the right one.

    FuseESB:karaf@root> osgi:list | grep Transactions
    [ 268] [Active     ] [Created     ] [       ] [   60] ESB :: Demo :: Camel-JMS-JDBC-XA-TX :: DataSource (1.0.0)
    [ 269] [Active     ] [            ] [Started] [   60] ESB :: Demo :: Camel-JMS-JDBC-XA-TX :: Routing (1.0.0)

In this example, the bundle id is 268.  Using the `osgi:ls` command, we can see that this bundle is publishing 3 services:

* first, there's the `javax.sql.XADataSource` that we created in our Blueprint XML file
* secondly, Aries JTA has added a corresponding `javax.sql.DataSource` and added the `aries.xa.aware = true` property to it to indicate an XA-aware data source
* finally, the Blueprint extender mechanism also published the Blueprint container (containg our data source bean definitions) it created

This is what the `osgi:ls` output looks like

    FuseESB:karaf@root> osgi:ls 268

    Fuse By Example :: Transactions :: Datasource (268) provides:
    -------------------------------------------------------------
    datasource.name = MySQL
    objectClass = javax.sql.XADataSource
    osgi.jndi.service.name = jdbc/transactions
    osgi.service.blueprint.compname = mysql-cf
    service.id = 414
    ----
    aries.xa.aware = true
    datasource.name = MySQL
    objectClass = javax.sql.DataSource
    osgi.jndi.service.name = jdbc/transactions
    osgi.service.blueprint.compname = mysql-cf
    service.id = 415
    service.ranking = 1000
    ----
    objectClass = org.osgi.service.blueprint.container.BlueprintContainer
    osgi.blueprint.container.symbolicname = org.apache.servicemix.demo.camel-jms-jdbc-xa-tx.datasource
    osgi.blueprint.container.version = 1.0.0
    service.id = 416