## JBoss-Client-OSGi Demo

Tested against JBoss 5.0.1 and Fuse ESB Enterprise 7.1

This demo illustrates how to invoke on an EJB deployed into JBoss from a an application deployed into Fuse ESB or Fuse ESB Enterprise or even ServiceMix. 
Its a self contained demo, meaning that the EJB code and the JBoss client are provided. 


### Module Simple-EJB 
Provides the implementation of a simple stateless session bean.
The beans interface is defined in src/main/java/org/apache/servicemix/demo/simpleejb/SimpleGreeter.java,
and offers the following interface

````java
public interface SimpleGreeter {

    String greetMe(String name);

}
```

The bean implementation class is in src/main/java/org/apache/servicemix/demo/simpleejb/SimpleGreeterBean.java.


### Module EJB-Client
Provides the client side code for invoking on the above EJB. This is basically 
an external JBoss client, doing a lookup of the beans remote interface and then
invoking the beans business method. 
The client has the JNDI settings hard coded and provides a main() method to run 
it standalone. However the client can also be deployed into an OSGi container 
as it will be packaged as an OSGi bundle.
Only for demonstration purposes an OSGi Activator is supplied that runs the EJB
client. In a real life application some business logic would trigger the EJB 
invocation. The OSGi Activator gets called on bundle startup. It calls the 
Client class which then invokes the EJB running in JBoss.


## Compiling
Simply run mvn install to compile the demo.


### Deploying
1) Deploying the EJB into JBoss
- Start JBoss 5 or later (assuming default configuration)
- `cp Simple-EJB/target/simple-ejb-demo-1.0.0-client.jar $JBOSS_HOME/server/default/deploy`
  Verify the EJB gets deployed correctly.

2) Deploying the client into OSGi
- Its easiest to generate a big JBoss client uber jar and deploy that into OSGi
  rather than deploying all the individual JBoss client jar files using wrap:

- Create the uber jar using the provided create-uber-jar script. You need to 
  set the JBOSS-HOME variable correctly beforehand. The script creates a larger
  jboss-client-all.jar that is then to be deployed into Fuse ESB.
- Start Fuse ESB Enterprise
- `osgi:install wrap:file:/path/to/demo/EJB-Client/jboss-client-all.jar`
- `start <bundle id>`
- `osgi:install mvn:org.apache.servicemix.demo.simpleejb/simple-ejb-client/1.0.0`
  or `cp Simple-EJB/target/simple-ejb-demo-1.0.0-client.jar $SMX_HOME/deploy`


## Running
- `start <demo bundle id>`
- If this raises any ClassNotFoundExceptions or NoClassDefFoundErrors 
  (which it should not) then 
  - `stop <demo bundle id>`
  - `dev:dynamic-import <bundle id>`
  - `start <demo bundle id>`
  - this should make the demo start.

- Run log:display, the logging output should read:
```
18:25:32,162 | INFO  | l Console Thread | Client | 228 - org.apache.servicemix.demo.simpleejb.simple-ejb-client - 1.0.0 | main started.
18:25:32,176 | INFO  | l Console Thread | Client | 228 - org.apache.servicemix.demo.simpleejb.simple-ejb-client - 1.0.0 | Obtained a remote EJB reference for invocation.
18:25:32,183 | INFO  | l Console Thread | Client | 228 - org.apache.servicemix.demo.simpleejb.simple-ejb-client - 1.0.0 | Result is: "Welcome user, you have just invoked SimpleGreeterBean!"
```
