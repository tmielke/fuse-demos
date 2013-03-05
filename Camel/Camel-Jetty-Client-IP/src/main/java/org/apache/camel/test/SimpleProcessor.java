package org.apache.camel.test;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import org.eclipse.jetty.server.Request;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Tries to extract the TCP address of the remote client from
 * the exchange. This address is not exposed as a general header 
 * on the incoming message. Instead it tries to extract the original 
 * Jetty HttpServletRequest, which has all the client information. 
 * This processor can only be used in combination with a camel-jetty consumer!
 * 
 * @author tmielke
 */
public class SimpleProcessor implements Processor{

    protected final static Logger LOG = LoggerFactory.getLogger(SimpleProcessor.class);

    public void process(Exchange exchange) {
        String reply = "<html><body><h1>Camel Route Reply</h1><br>";
        LOG.debug("SimpleProcessor.checkHeader() invoked.");
        Message inMsg = exchange.getIn();

        // extract the Jetty HttpServletRequest, from there we can extract
        // the remote client address.
        HttpServletRequest req = exchange.getIn().getBody(HttpServletRequest.class);
        if (req != null) {
            String remoteAddr = ((Request) req).getConnection().getEndPoint().getRemoteAddr(); 
            int remotePort = ((Request) req).getConnection().getEndPoint().getRemotePort(); 
            LOG.info("Client called from " + remoteAddr + ":" + remotePort);

            // generate a reply message
            reply += "You called from address " + remoteAddr + ":" + remotePort + 
                " with data: " + inMsg.getBody() + "</body></html>";
        } else {
            reply = "Could not extract client address!</body></html>";
        }
        exchange.getOut().setBody(reply);
    }
}