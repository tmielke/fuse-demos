package org.apache.karaf.demo.osgitest.internal;

import org.apache.karaf.demo.osgitest.OSGiTestInterface;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Simple bundle that gets exported in the OSGi service registry
 * using the interface OSGiTestInterface
 */
public class OSGiTestImpl implements OSGiTestInterface {

    private final Logger LOG = LoggerFactory.getLogger(OSGiTestImpl.class);
    private static int counter;


    /**
     * Implementation of service interface.
     * @param resourceName the name of the resource in url format
     *        bundle:[id].[rev]/[path]
     */
    public void invokeService(String resourceName) {
        LOG.info("OSGiTestImpl.invokeService(" + resourceName + ") called.");

        try {
            // convert String to URL
            URL url = new URL(resourceName);
            
            // open url and read the content. Note the resource that gets read
            // here resides in a different bundle
            InputStream stream = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line = null;
            LOG.debug("Going to read external resource.");
            do { 
                line = reader.readLine();
                LOG.info(line);
            } while (line != null);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }
        LOG.debug("Done with invokeService()");
    }
}
