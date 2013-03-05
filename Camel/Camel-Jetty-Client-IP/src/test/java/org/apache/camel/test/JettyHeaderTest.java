package org.apache.camel.test;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.junit4.CamelSpringTestSupport;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Test the Camel route definition in 
 * src/main/resources/META-INF/spring/camel-context.xml
 * Starts the provided Camel route and sends a 
 * HTTP GET request using httpclient library against the
 * camel-jetty consumer. Verifies the response.
 * 
 * @author tmielke
 *
 */
public class JettyHeaderTest extends CamelSpringTestSupport {

    protected final static Logger LOG = LoggerFactory.getLogger(JettyHeaderTest.class);
    private static final String TARGET_ADDRESS = "http://localhost:9000/JettyHeaderTest";

    @EndpointInject(uri="jetty:" + TARGET_ADDRESS)
    private ProducerTemplate template;


    /* (non-Javadoc)
     * @see org.apache.camel.test.junit4.CamelTestSupport#setUp()
     */
    @Before
    public void setUp() throws Exception {
        LOG.debug("setUp() called.");
        super.setUp();
    }

    /* (non-Javadoc)
     * @see org.apache.camel.test.junit4.CamelTestSupport#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        LOG.debug("tearDown() called.");
        super.tearDown();
    }

    @Test
    public void test() throws Exception {
        LOG.debug("test() called.");

        sendSampleHttpGetRequest();

        LOG.debug("test() done.");
    }


    @Override
    protected AbstractApplicationContext createApplicationContext() {
        LOG.debug("createApplicationContext() called.");
        return new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
    }


    /** 
     * Sends a plain HTTP GET request against TARGET_ADDRESS
     * @throws Exception - any exception is propagated
     */
    protected void sendSampleHttpGetRequest() throws Exception {
        LOG.debug("Preparing HTTP GET request.");
        HttpClient client = new HttpClient();
        MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams conParams = new HttpConnectionManagerParams();
        manager.setParams(conParams);
        conParams.setDefaultMaxConnectionsPerHost(6);
        conParams.setMaxTotalConnections(MultiThreadedHttpConnectionManager.DEFAULT_MAX_TOTAL_CONNECTIONS);
        client.setHttpConnectionManager(manager);

        client.getParams().setParameter("http.useragent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        client.getParams().setVersion(HttpVersion.HTTP_1_1);

        GetMethod httpGet = new GetMethod(TARGET_ADDRESS);
        httpGet.setDoAuthentication(false);
        httpGet.setPath("/JettyHeaderTest");

        try {
            client.executeMethod(httpGet);
            LOG.debug("Sent HTTP GET request.");
            Thread.sleep(500);

            Assert.assertTrue("Unexpected failure: " + httpGet.getStatusLine().toString(),
                httpGet.getStatusCode() == HttpStatus.SC_OK);
            LOG.info(httpGet.getResponseBodyAsString());
        }
        finally {
            httpGet.releaseConnection();
        }
    }
}
