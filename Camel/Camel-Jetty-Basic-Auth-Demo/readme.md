# Camel Jetty Basic Authentication Demo

Tested against JBoss Fuse 6.1

## Description

This demo programmatically configures a camel-jetty endpoint for HTTP Basic Authentication against the JAAS
authentication realm provided by the Karaf OSGi container. That authentication realm is called 'karaf'.

The configuration that is applied here programmatically is taken from this documentation
https://access.redhat.com/documentation/en-US/Red_Hat_JBoss_Fuse/6.1/html/Security_Guide/files/CamelJetty.html






## Compiling

simply run `mvn clean install` to build the demo.



## Deploying

- open `$KARAF_HOME/etc/users.properties` and define a user with password and role.
- start JBoss Fuse 6.1
- install the following two bundles:
  `install -s mvn:org.apache.camel/camel-core-osgi/2.12.0.redhat-610379`
  `install -s mvn:org.apache.camel.demo/camel-jetty-basic-auth/1.0-SNAPSHOT`



## Running

Once these two bundles are deployed and started successfully, point your browser to
`http://localhost:9081/jettyexample` and you will be required to provide username and password credentials using
HTTP basic authentication. Provide the same credentials that you initially entered into
`$KARAF_HOME/etc/users.properties`.


If authentication succeeds, the returned HTML will simply contain the word `response`.