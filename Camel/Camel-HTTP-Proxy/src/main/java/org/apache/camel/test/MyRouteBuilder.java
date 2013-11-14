/**
 * 
 */
package org.apache.camel.test;

import org.apache.camel.builder.RouteBuilder;

/**
 * Sets up the same HTTP Proxy route that is defined in camel-context.xml
 * but using the Java DSL.
 * @author tmielke
 *
 */
public class MyRouteBuilder extends RouteBuilder {

    /* (non-Javadoc)
     * @see org.apache.camel.builder.RouteBuilder#configure()
     */
    @Override
    public void configure() throws Exception {

        from("jetty://http://localhost:9100/weather?matchOnUriPrefix=true").
        to("http://weather.yahoo.com/united-states?bridgeEndpoint=true&throwExceptionOnFailure=false&traceEnabled").
        to("log:MyLogger?level=INFO&showAll=true");
    }
}
