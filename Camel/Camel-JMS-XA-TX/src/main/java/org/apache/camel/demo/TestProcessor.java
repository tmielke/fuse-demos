package org.apache.camel.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.jms.JmsMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Dummy Camel Processor that simulates msg processing.
 * Increments a static counter for each msgs received.
 * 
 * Can also be used to raise an exception for every X msg 
 * to illustrate how an exception causes the transaction to be rolled
 * back.
 * 
 * @author tmielke
 * 
 */
public class TestProcessor implements Processor {
    private static AtomicInteger counter = new AtomicInteger(0);
    private static Long startTime = new Long(0);

    // statistics to be printed every statsAfterMsgs messages
    private long statsAfterMsgs = 10000;

    // whether to simulate a processing error by raising an exception
    private boolean simulateProcessingError = false;

    // if simulateProcessingError=true, then throw exception
    // every errorAfterMsgs messages.
    private int errorAfterMsgs = 5;

    private Logger log = LoggerFactory.getLogger(TestProcessor.class);
    private static Map<String, String> connections = new HashMap<String, String>();


    public void process(Exchange exchange) throws Exception {

        if (log.isTraceEnabled())
            log.trace("Starting to process messages.");

        // take startup time in case counter is at 0
        // access need to be synchronized
        if (counter.get() == 0) {
            synchronized (counter) {
                if (counter.get() == 0) {
                    startTime = System.currentTimeMillis();
                    log.info("Taking startup time.");
                }
            }
        }
        // increment counter
        counter.incrementAndGet();

        // print statistics after every statsAfterMsgs messages 
        if (counter.get() == statsAfterMsgs) {
            long endTime = System.currentTimeMillis();

            double throughput = (double) counter.get()/((double)(endTime - startTime.longValue())/(double)1000);
            log.info("Processing " + statsAfterMsgs + " took " + (endTime - startTime.longValue()) + " msecs, roughly " + (int) throughput + " msgs/sec");
            counter.set(0);
        }

        // should we simulate a processing error?
        if (simulateProcessingError) {
            if (counter.get() % errorAfterMsgs == 0) {
                throw new RuntimeException("Thrown from Camel Processor to simulate an exception in Camel route");
            }
        }
        // do something with the payload and/or exchange here
        String payload = exchange.getIn().getBody(String.class);

        exchange.getIn().setBody("Changed body");
        log.trace("Body of in message changed.");

        /*
        // simulate more complex processing
        Thread.sleep(1000);
         */

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

    public long getStatsAfterMsgs() {
        return statsAfterMsgs;
    }

    public void setStatsAfterMsgs(long statsAfterMsgs) {
        this.statsAfterMsgs = statsAfterMsgs;
    }
}
