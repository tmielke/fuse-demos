/*
 * Copyright 2012 FuseSource
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
package org.apache.karaf.demo.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.karaf.demo.Ping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Simple bean.
 * Note this bean has setter methods for all of its config.
 * At bean instantiation time these setters will be called.
 * These setters will also be used on config:edit if 
 * managed properties are set to container-managed in 
 * blueprint. 
 * If set to component-managed, the update() method below
 * will be called. 
 */
public class PingBean implements Ping {

    private final Logger LOG = LoggerFactory.getLogger(PingBean.class);
    private String brokerUrl = "";
    private String jettyPort = "";
    private String pingFrequency = "";


    public String ping() {
        LOG.info("ping() invoked.");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "Hello at " + sdf.format(new Date());
    }


    public void setBrokerUrl(String url) {
        System.out.println("setBrokerUrl(" + url + ").");
        LOG.info("setBrokerUrl(" + url + ").");
        this.brokerUrl = url;
    }


    public void setJettyPort(String port) {
        System.out.println("setJettyPort(" + port + ").");
        LOG.info("setJettyPort(" + port + ").");
        this.jettyPort = port;
    }


    public void setPingFrequency(String freq) {
        System.out.println("setPingFrequency(" + freq + ").");
        LOG.info("setPingFrequency(" + freq + ").");
        this.pingFrequency = freq;
    }


    public void update(Map<String,?> properties) {
        System.out.println("Received properties " + properties);
        LOG.info("Received properties {}", properties.toString());
    }

}
