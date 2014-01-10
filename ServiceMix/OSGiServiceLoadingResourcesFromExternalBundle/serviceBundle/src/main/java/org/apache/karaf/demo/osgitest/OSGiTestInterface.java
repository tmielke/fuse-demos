package org.apache.karaf.demo.osgitest;

/**
 * This is the service interface.
 */
public interface OSGiTestInterface {

	/** 
	 * Simple test method to show the application got invoked.
	 */
	public void invokeService(String resourceName);
}

