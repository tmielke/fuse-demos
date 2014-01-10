package org.apache.karaf.demo.osgitest.client;

import org.apache.karaf.demo.osgitest.OSGiTestInterface;

import java.net.URL;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Bundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple client bundle that gets the service injected via 
 * blueprint and calls it straight away, passing in the url
 * to an xml file that resides locally in this bundle.
 */
public class Client {

    private Logger LOG = LoggerFactory.getLogger(Client.class);
    private BundleContext context = null;


    /* (non-Javadoc)
     * @see com.fusesource.test.osgitest.OSGiTestInterface#doIt()
     */
    public void doIt() {
        LOG.info("OSGiTestImpl.doIt() invoked.");
        LOG.debug("Done with doIt()");
    }


    /**
     * Injects the OSGi service proxy.
     * @param service
     */
    public void setService(OSGiTestInterface service) {
        LOG.debug("setService() invoked.");

        //ensure the bundle context is injected before proceeding
        if (context == null) {
            LOG.error("BundleContext not set");
            return;
        }
        // Use the BundleContext to get a ref to this OSGi bundle
        URL url = null;
        try {
            Bundle bundle = context.getBundle();
            
            // Use Bundle.getResource() to create a url of the form
            // bundle:[id].[rev]/[path]
            // pointing to the resource file that is then passed to the 
            // OSGi service invocation.
            url = bundle.getResource("OSGI-INF/blueprint/blueprint.xml");

            LOG.info("url is " + url);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }
        // Invoke the service, pass in the resource url
        service.invokeService(url.toString());
        LOG.debug("service() called succesfully.");
    }


    /**
     * Sets the OSGi BundleContext. Its needed to 
     * create the resource url.
     * @param context
     */
    public void setContext(BundleContext context) {
        LOG.debug("setContext() invoked.");
        this.context = context;
    }
}
