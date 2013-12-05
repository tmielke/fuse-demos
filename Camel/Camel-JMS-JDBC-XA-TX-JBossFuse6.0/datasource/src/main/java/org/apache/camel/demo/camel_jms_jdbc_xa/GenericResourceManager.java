package org.apache.camel.demo.camel_jms_jdbc_xa;

import javax.resource.spi.ManagedConnectionFactory;
import org.apache.geronimo.connector.outbound.ConnectionManagerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic resource manager wrapper.
 * Only needed for working around
 * http://fusesource.com/issues/browse/ENTESB-633.
 * Simply delegates the call to doRecovery() to the configured
 * Geronimo ConnectionManagerContainer.
 */
public class GenericResourceManager {

    Logger logger = LoggerFactory.getLogger(GenericResourceManager.class.getName());

    private ConnectionManagerContainer connectionManagerContainer;
    private ManagedConnectionFactory managedConnectionFactory;

    public synchronized void setConnectionManagerContainer(
        ConnectionManagerContainer connectionManagerContainer) {
    this.connectionManagerContainer = connectionManagerContainer;
    }

    public synchronized void setManagedConnectionFactory(
        ManagedConnectionFactory managedConnectionFactory) {
    this.managedConnectionFactory = managedConnectionFactory;
    }


    public void doRecovery() {
    logger.info("Recovering XA resource " + managedConnectionFactory);
    connectionManagerContainer.doRecovery(managedConnectionFactory);
    }
}