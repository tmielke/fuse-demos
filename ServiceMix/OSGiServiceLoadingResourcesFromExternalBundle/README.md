# OSGi Service loading resource from a different bundle

This simple demo illustrates how a bundle A invoke an OSGi service on bundle B
and passes in a resource name which resides in the bundle A. 
The service at bundle B is able to load the resource from bundle A.

The main idea is to pass along a url that encodes the bundle id.
It has the format bundle:[id].[rev]/[path].
Such url can be constructed using the Bundle.getResource() API.


The demo consists of two sub modules: serviceBundle and clientBundle
### serviceBundle
contains the interface and implementation of the bundle 
providing the OSGi service. It uses blueprint to instantiate the service
and to register it in the OSGi service registry.

### clientBundle
contains the client. Its also instantiated using blueprint.
The client gets the BundleContext injected, which is needed to construct the 
resource url.
In addition it gets the reference to the OSGi service injected and calls the 
service straight away in method 
  public void setService(OSGiTestInterface service);

Have a look at how the resource url is constructed in the client and how it is
passed on to the service invocation.



## Compiling
Simply run mvn install from the top level demo directory


## Running

This demo has been tested on JBoss Fuse 6.0.
- Start JBoss Fuse 6.0
- log:set DEBUG org.apache.karaf.demo.osgitest
- install mvn:org.apache.karaf.test.service-resource-loading-example/service-bundle/0.0.1-SNAPSHOT
- install mvn:org.apache.karaf.test.service-resource-loading-example/client-bundle/0.0.1-SNAPSHOT
- start both bundles, observe the Karaf logging output. It should be similar to [1].




[1] Logging output when starting both bundles:
14:43:14,563 | DEBUG | NAPSHOT-thread-1 | Client       | 290 - client-bundle - 0.0.1.SNAPSHOT | setContext() invoked.
14:43:14,564 | DEBUG | NAPSHOT-thread-1 | Client       | 290 - client-bundle - 0.0.1.SNAPSHOT | setService() invoked.
14:43:14,564 | INFO  | NAPSHOT-thread-1 | Client       | 290 - client-bundle - 0.0.1.SNAPSHOT | url is bundle://290.0:1/test.xml
14:43:14,564 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT | OSGiTestImpl.invokeService(bundle://290.0:1/test.xml) called.
14:43:14,565 | DEBUG | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT | Going to read external resource.
14:43:14,565 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT | <!--
14:43:14,565 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT | ~ Copyright 2011 FuseSource
14:43:14,565 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT | ~
14:43:14,565 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT | ~ Licensed under the Apache License, Version 2.0 (the "License");
14:43:14,565 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT | ~ you may not use this file except in compliance with the License.
14:43:14,565 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT | ~ You may obtain a copy of the License at
14:43:14,565 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT | ~
14:43:14,565 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT | ~ http://www.apache.org/licenses/LICENSE-2.0
14:43:14,565 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT | ~
14:43:14,565 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT | ~ Unless required by applicable law or agreed to in writing, software
14:43:14,565 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT | ~ distributed under the License is distributed on an "AS IS" BASIS,
14:43:14,565 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT | ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
14:43:14,565 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT | ~ See the License for the specific language governing permissions and
14:43:14,565 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT | ~ limitations under the License.
14:43:14,565 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT | -->
14:43:14,566 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT | <blueprint xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0"
14:43:14,566 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT |     xmlns:cm="http://aries.apache.org/blueprint/xmlns/blueprint-cm/v1.0.0">
14:43:14,566 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT |
14:43:14,566 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT |   <reference id="service" interface="org.apache.karaf.demo.osgitest.OSGiTestInterface" availability="mandatory"/>
14:43:14,566 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT |
14:43:14,566 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT |   <bean id="client" class="org.apache.karaf.demo.osgitest.client.Client">
14:43:14,566 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT |     <property name="service" ref="service" />
14:43:14,566 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT |   </bean>
14:43:14,566 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT | </blueprint>
14:43:14,566 | INFO  | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT |
14:43:14,566 | DEBUG | NAPSHOT-thread-1 | OSGiTestImpl | 289 - service-bundle - 0.0.1.SNAPSHOT | Done with invokeService()
14:43:14,566 | DEBUG | NAPSHOT-thread-1 | Client       | 290 - client-bundle - 0.0.1.SNAPSHOT | service() called succesfully.

