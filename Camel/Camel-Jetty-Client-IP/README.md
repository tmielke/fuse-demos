
#CamelJettyHeaderTest

Simple Camel JUnit test that shows how to extract the client's IP address
in a Camel route that consumes from a Jetty HTTP endpoint.

Although the camel-jetty endpoint does not expose the client's IP address 
as a message headers, it is possible to retrieve Jetty's HttpServletRequest
object. This contains the complete client address information.

The Camel route definition in src/main/resources/META-INF/spring/camel-context.xml
is very simple:

  <bean id="SimpleProcessor" class="org.apache.camel.test.SimpleProcessor"/>

    <camelContext id="camel" xmlns="http://camel.apache.org/schema/spring">
      <route>
        <from uri="jetty:http://localhost:9000/JettyHeaderTest"/>
        <bean ref="SimpleProcessor" />
        <to uri="log:org.fusesource.test.camel-jetty-test?level=INFO&amp;showAll=true"/>
      </route>
    </camelContext>

The SimpleProcessor in src/main/java/org/apache/camel/test/SimpleProcessor 
extracts the client's IP address using these few lines of code:

    HttpServletRequest req = exchange.getIn().getBody(HttpServletRequest.class);
    String remoteAddr = ((org.eclipse.jetty.server.Request) req).getConnection().getEndPoint().getRemoteAddr(); 
    int remotePort = ((org.eclipse.jetty.server.Request) req).getConnection().getEndPoint().getRemotePort(); 
    LOG.info("Client called from " + remoteAddr + ":" + remotePort);

Finally the JUnit test class is located in 
  src/test/java/org/apache/camel/test/JettyHeaderTest


##Compiling
Simply run mvn test-compile to build the project


##Running
There are two ways to run the demo.

###1) JUnit test
simply run mvn test and observe the logging output


###2) Camel Maven plugin
Simply run mvn camel:run and wait for the route to be started.
Then open your favorite browser and navigate to
  http://localhost:9000/JettyHeaderTest

  The reply displayed in your browser should read:

    Camel Route Reply
    You called from address 127.0.0.1:59524 with data: null
