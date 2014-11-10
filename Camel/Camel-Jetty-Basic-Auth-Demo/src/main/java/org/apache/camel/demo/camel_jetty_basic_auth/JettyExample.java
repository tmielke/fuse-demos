package org.apache.camel.demo.camel_jetty_basic_auth;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.jetty.JettyHttpEndpoint;
import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.core.osgi.utils.BundleDelegatingClassLoader;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.ExplicitCamelContextNameStrategy;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.eclipse.jetty.plus.jaas.JAASLoginService;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.DefaultIdentityService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.util.security.Constraint;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component(immediate = true, metatype = false)
public class JettyExample implements Runnable {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected CamelContext camelctx;
    protected Thread starterThread;
    protected boolean activated = false;
    private int maxAttempts = 10;

    public List<RoutesBuilder> getRouteBuilders() {
        List<RoutesBuilder> routesBuilders = new Vector<>();
        routesBuilders.add(new JettyExampleRoute());
        return routesBuilders;
    }

    // Don't try to activate before Camel components start registering themselves
    @Reference(referenceInterface = org.apache.camel.spi.ComponentResolver.class)
    org.apache.camel.spi.ComponentResolver anyCamelComponent;

    @Activate
    public void activate(BundleContext bundleContext, Map<String, String> configuration) throws Exception {
        activated = true;
        // this.getClass().getClassLoader().loadClass();
        // Create a security handler for jetty and add it to registry
        Dictionary<String, Object> dictionary = new Hashtable<>();
        dictionary.put("name", "securityHandler");
        bundleContext.registerService("java.lang.Object", getJettySecurityHandler(), dictionary);

        // Create a new CamelContext and set up properties
        createCamelContext(bundleContext);

        // Set up CamelContext (name etc.)
        setupCamelContext("jettyexample");

        startCamelContext();
    }

    @Deactivate
    public void deactivate() throws Exception {
        activated = false;

        stopCamelContext();
    }

    public static SecurityHandler getJettySecurityHandler() {
        JAASLoginService loginService = new JAASLoginService();
        loginService.setName("karaf");
        loginService.setLoginModuleName("karaf");
        loginService.setRoleClassNames(new String[]{"org.apache.karaf.jaas.boot.principal.RolePrincipal"});

        Constraint constraint = new Constraint();
        constraint.setName("BASIC");
        constraint.setRoles(new String[]{"admin"});
        constraint.setAuthenticate(true);

        ConstraintMapping constraintMapping = new ConstraintMapping();
        constraintMapping.setConstraint(constraint);
        constraintMapping.setPathSpec("/*");

        ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
        securityHandler.setAuthenticator(new BasicAuthenticator());
        securityHandler.addConstraintMapping(constraintMapping);
        securityHandler.setLoginService(loginService);
        securityHandler.setStrict(false);
        securityHandler.setIdentityService(new DefaultIdentityService());

        return securityHandler;
    }

    protected void createCamelContext(BundleContext bundleContext) {
        log.debug("createCamelContext()");
        if (null != bundleContext) {
            // Create a OSGi aware CamelContext
            camelctx = new OsgiDefaultCamelContext(bundleContext);
            // setup the application context classloader with the bundle classloader
            camelctx.setApplicationContextClassLoader(new BundleDelegatingClassLoader(bundleContext.getBundle()));
            // and make sure the tccl is our classloader
            Thread.currentThread().setContextClassLoader(camelctx.getApplicationContextClassLoader());

            log.debug("OSGi aware Camel context created.");
        } else {
            camelctx = new DefaultCamelContext();
            log.debug("Default Camel context created.");
        }

        Map config = new HashMap();
        config.put("securityHandler", getJettySecurityHandler());
        Endpoint endpoint = camelctx.getEndpoint("jetty:http://0.0.0.0:9081/jettyexample");
        JettyHttpEndpoint jettyEndpoint = (JettyHttpEndpoint)endpoint;
        List handlers = new LinkedList();
        handlers.add(getJettySecurityHandler());
        jettyEndpoint.setHandlers(handlers);
    }

    protected void setupCamelContext(String camelContextName) throws Exception {
        // Set CamelContext name
        camelctx.setNameStrategy(new ExplicitCamelContextNameStrategy(camelContextName));

        camelctx.setUseMDCLogging(true);
        camelctx.setUseBreadcrumb(true);

        for (RoutesBuilder routesBuilder : getRouteBuilders()) {
            camelctx.addRoutes(routesBuilder);
        }
    }

    public void run() {
        log.debug("run()");
        for (int attempts = 1; activated && !Thread.interrupted() && attempts <= maxAttempts; attempts++) {
            try {
                camelctx.start();
                log.debug("Camel context started");
                break;
            } catch (InterruptedException e) {
                log.debug("Starter thread interrupted during start delay.");
                break;
            } catch (Exception e) {
                log.warn("Failed to start Camel context (attempt " + attempts + " of " + maxAttempts + ")", e);
            }
        }
        log.debug("run() finished");
    }

    public void startCamelContext() {
        if (null != starterThread && starterThread.isAlive()) {
            log.warn("Starter thread already running. Not starting a new one.");
        } else {
            log.debug("Starter thread created.");
            starterThread = new Thread(this);
            starterThread.setDaemon(true);
            starterThread.start();
        }
    }

    public void stopCamelContext() throws Exception {
        log.debug("stopCamelContext()");
        if (null != starterThread && starterThread.isAlive()) {
            log.debug("Starter thread is still running, interrupt it.");
            starterThread.interrupt();
            log.debug("Wait for starter thread to finish.");
            starterThread.join(5000);
        }
        camelctx.stop();
    }
}
