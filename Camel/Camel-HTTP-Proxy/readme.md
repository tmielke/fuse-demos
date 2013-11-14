# Camel HTTP Proxy demo 

A quick demo that shows how to do HTTP proxying in Camel.
For additional documentation see 
https://access.redhat.com/site/documentation/en-US/JBoss_Fuse/6.0/html/Web_Services_and_Routing_with_Camel_CXF/files/Proxying-HTTP.html

This demo uses the following route definition

  <camelContext xmlns="http://camel.apache.org/schema/spring">
    <route id="camel-http-proxy">
      <from uri="jetty://http://localhost:9000/weather?matchOnUriPrefix=true"/>
      <to uri="http://weather.yahoo.com/united-states?bridgeEndpoint=true&amp;throwExceptionOnFailure=false" />
      <to uri="log:MyLogger?level=INFO&amp;showAll=true" />
    </route>
  </camelContext>

and proxies any HTTP requests coming to the url http://localhost:9000/weather to 
the external HTTP address http://weather.yahoo.com/united-states.


The most interesting files are
src/main/resources/META-INF/spring/camel-context.xml
src/main/java/org/apache/camel/test/MyRouteBuilder.java



## Compiling

- mvn install 



## Running

- mvn camel:run
- open a browser and type any of these urls
  http://localhost:9000/weather/california
  http://localhost:9000/weather/california/acton-2351805
  http://localhost:9000/weather/massachusetts
  http://localhost:9000/weather/massachusetts/boston-2367105