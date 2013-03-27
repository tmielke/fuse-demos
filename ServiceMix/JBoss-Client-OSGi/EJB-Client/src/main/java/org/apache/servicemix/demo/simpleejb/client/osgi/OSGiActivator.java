package org.apache.servicemix.demo.simpleejb.client.osgi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.servicemix.demo.simpleejb.client.Client;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


/**
 * @author TMIELKE
 * 
 * A standard Bundle Activator
 */
public class OSGiActivator implements BundleActivator {

    private final static Logger LOG = LoggerFactory.getLogger(OSGiActivator.class);

    private Client client = null;


    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        LOG.debug("OSGiActivator.start() called.");
        client = new Client();
        client.main(null);
	}
	

    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
    	LOG.debug("OSGiActivator.stop() called.");

        if (null != client) {
            client = null;
        }
    }
}
