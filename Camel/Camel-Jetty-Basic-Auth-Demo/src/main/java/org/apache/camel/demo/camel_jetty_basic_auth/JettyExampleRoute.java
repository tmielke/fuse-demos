package org.apache.camel.demo.camel_jetty_basic_auth;

import org.apache.camel.builder.RouteBuilder;

public class JettyExampleRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
        from("jetty:http://0.0.0.0:9081/jettyexample?handlers=securityHandler")
            .routeId("jettyexampleroute")
            .wireTap("log:something")
            .setBody(simple("Response from Camel."));
	}
}
