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
 * Note this bean does not have any setter methods.
 * Instead it gets its configuration via setConfig() at
 * instantiation time and whenever the 
 * configuration was updated in the ConfigAdmin service.
 * This requires proper blueprint config to work.
 */
public class PingBean2 implements Ping {

    private final Logger LOG = LoggerFactory.getLogger(PingBean2.class);
    private String brokerUrl = "";
    private String jettyPort = "";
    private String pingFrequency = "";

    public String ping() {
        LOG.info("ping() invoked.");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "Hello at " + sdf.format(new Date());
    }


    public void setConfig(Map<String,?> properties) {
        LOG.info("setConfig() called. Received properties {}", properties.toString());
    }

}
