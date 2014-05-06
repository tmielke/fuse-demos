/**
* Copyright (c) Red Hat, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.camel.demo.camel_wmq_amq_xa_tx;

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
 * to illustrate how an exception causes the transaction to be rolled
 * back.
 * 
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
