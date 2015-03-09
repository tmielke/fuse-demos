
## EJB Client

This directory contains the EJB client application.
Its a standalone JBoss client that invokes a stateless session bean deployed
in JBoss 5.

The client can also be run standalone for sanity checking but the real value
of this demo is to deploy this client into Fuse ESB.


### Standalone Client
To run the client standalone follow these steps:

```
- Deploy the Simple-EJB into JBoss 5 as explained in ../README.md

- edit create-uber-jar.sh and update JBOSS_HOME accordingly

- run ./create-uber-jar.sh and verify it creates ./jboss-client-all.jar

- run the standalone client using ./run_client.sh 
```

## OSGi client

To run the client within an OSGi container such as Fuse ESB Enterprise, 
follow the steps given in ../README.md

