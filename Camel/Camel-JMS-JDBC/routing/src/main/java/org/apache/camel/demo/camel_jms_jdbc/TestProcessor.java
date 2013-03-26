package org.apache.camel.demo.camel_jms_jdbc;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dummy Camel Processor that simulates msg processing.
 * Increments a static counter for each msgs received.
 * 
 * Can also be used to raise an exception for every X msg 
 * to illustrate how an exception causes the XA transaction to be rolled back.
 */
public class TestProcessor implements Processor {
    private static AtomicInteger counter = new AtomicInteger(0);

    // whether to simulate a processing error by raising an exception
    private boolean simulateProcessingError = false;

    // if simulateProcessingError=true, then throw exception
    // every errorAfterMsgs messages.
    private int errorAfterMsgs = 5;

    private Logger log = LoggerFactory.getLogger(TestProcessor.class);


    public void process(Exchange exchange) throws Exception {

        if (log.isTraceEnabled())
            log.trace("starting to process messages.");

        // increment counter
        counter.incrementAndGet();

        // should we simulate a processing error?
        if (simulateProcessingError) {
            if (counter.get() % errorAfterMsgs == 0) {
                throw new RuntimeException("Thrown from Camel Processor to simulate an exception in Camel route");
            }
        }
        if (log.isTraceEnabled())
            log.trace("Finished msg processing. Counter is at " + counter.get());		
    }


    /* Getter and Setter methods   */
    public boolean isSimulateProcessingError() {
        return simulateProcessingError;
    }


    public void setSimulateProcessingError(boolean simulateProcessingError) {
        this.simulateProcessingError = simulateProcessingError;
    }

    public int getErrorAfterMsgs() {
        return errorAfterMsgs;
    }


    public void setErrorAfterMsgs(int errorAfterMsgs) {
        this.errorAfterMsgs = errorAfterMsgs;
    }
}
