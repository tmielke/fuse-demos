package org.apache.karaf.demo.osgitest.client;

import org.apache.karaf.demo.osgitest.OSGiTestInterface;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple client bundle that gets the service injected via 
 * blueprint and calls it straight away, passing in the url
 * to an xml file that resides locally in this bundle.
 */
public class Client {

    private Logger LOG = LoggerFactory.getLogger(Client.class);
    private OSGiTestInterface service = null;

    public void doIt() {
        LOG.info("Client.doIt() invoked.");
        URL url = this.getClass().getClassLoader().getResource("OSGI-INF/blueprint/blueprint.xml");
        LOG.info("url is " + url);

        // Invoke the service, pass in the resource url
        service.invokeService(url.toString());
        LOG.debug("service() called succesfully.");
        LOG.debug("Done with doIt()");
    }


    /**
     * Injects the OSGi service proxy and directly invokes the service
     * (invocation for testing purposes).
     * @param service
     */
    public void setService(OSGiTestInterface service) {
        LOG.debug("setService() invoked.");
        this.service = service;
        doIt();
    }
}
