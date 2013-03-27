package org.apache.servicemix.demo.simpleejb.client;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

import org.apache.servicemix.demo.simpleejb.SimpleGreeter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Client {

    final static Logger LOG = LoggerFactory.getLogger(Client.class);


    public static void main(String[] args) throws Exception {
        LOG.info("main started.");
        invokeStatelessBean();
    }


	/**
     * Looks up a stateless bean and invokes on it
     *
     * @throws NamingException
     */
    private static void invokeStatelessBean() throws NamingException {
        // Let's lookup the remote stateless calculator
        final SimpleGreeter bean = lookupRemoteBean();
        LOG.info("Obtained a remote EJB reference for invocation.");

        LOG.debug("Invoking sayHi() on EJB...");
        String greeting = bean.greetMe("user");
        LOG.info("Result is: \"" + greeting + "\"");
        LOG.debug("Demo finished.");
    }


    /**
     * Looks up and returns the proxy to remote stateless bean
     *
     * @return
     * @throws NamingException
     */
    private static SimpleGreeter lookupRemoteBean() throws NamingException {
        Hashtable<String, String> props = new Hashtable<String, String>();
        props.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        props.put("java.naming.provider.url", "jnp://localhost:1099");
        final Context context = new InitialContext(props);

        String lookupName = "SimpleGreeterBean/remote";
        LOG.debug("Going to lookup " + lookupName);
        return (SimpleGreeter) context.lookup(lookupName);
    }
}