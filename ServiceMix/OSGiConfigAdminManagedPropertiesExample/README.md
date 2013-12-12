## Karaf Configuration Admin with Managed Properties Demo

Simple demo that injects two Java beans with a particular OSGi configuration 
using the OSGi Configuraton Admin service and managed properties.

See chapter "11.1.2. Managed Properties" on
  http://www.eclipse.org/gemini/blueprint/documentation/reference/1.0.2.RELEASE/html/compendium.html

or chapter "10.1.2.1. Configuration Admin Runtime Updates" on
  http://docs.spring.io/osgi/docs/current/reference/html/compendium.html#compendium:cm

Both of the above docs are almost copy-paste of each other.

Note: Karaf uses different keywords for the update strategy than in the docs
above. Rather than using 'container-managed' or 'bean-managed' karaf wants 
either '[none, component-managed, container-managed]'.


This demo only consists of two similar Java bean classes PingBean.java,
PingBean2.java and a blueprint.xml.

Please carefully read the comments in blueprint.xml regarding the use
of different update strategies with Managed Properties.


## Compiling

mvn install


## Deploying

osgi:install mvn:org.apache.karaf.demo/config-admin-managed-properties-example/1.0-SNAPSHOT


## Running

I provide slightly different steps for running in a Fabric or non-Fabric 
managed environment. 


### Non-Fabric managed environment
- create the file etc/org.apache.karaf.demo.config_admin_demo.cfg
  with these values

  ```
  brokerUrl=tcp://localhost:61616
  jettyPort=8080
  pingFrequency=5000
  ```

- osgi:install mvn:org.apache.karaf.demo/config-admin-example/1.0-SNAPSHOT
- start <bundle>

  Notice the output to the Karaf log file as well as on the Karaf console.
  If you make any changes to this configuration via config:edit this will cause
  the new configuration to be set on the bean again. 

- Verify configuration changes get pushed to the beans:
    config:edit org.apache.karaf.demo.config_admin_demo
    config:propset jettyPort 9500
    config:update
  This should trigger an update to the bean. Check the Karaf log file for the 
  lines containing
    setJettyPort(9500)
    setConfig() called. Received properties {...}


### Fabric managed environment

Start Fabric and run the following commands

```
fabric:profile-create --parents default ConfigAdminTest
fabric:profile-edit --bundles mvn:org.apache.karaf.demo/config-admin-managed-properties-example/1.0-SNAPSHOT ConfigAdminTest
fabric:profile-edit --pid org.apache.karaf.demo.config_admin_demo/brokerUrl="tcp://localhost:61616" ConfigAdminTest
fabric:profile-edit --pid org.apache.karaf.demo.config_admin_demo/jettyPort=8080 ConfigAdminTest
fabric:profile-edit --pid org.apache.karaf.demo.config_admin_demo/pingFrequency=20000 ConfigAdminTest
fabric:profile-display ConfigAdminTest
fabric:container-add-profile root ConfigAdminTest
```

Notice the output to the Karaf log file as well as on the Karaf console.
If you make any changes to this configuraton either directly via config:edit or
by modifying the config file in your Fabric profile, this will cause the new 
config to be set on the bean again. 

Verify configuration changes get pushed to the beans:
  config:edit org.apache.karaf.demo.config_admin_demo
  config:propset jettyPort 9500
  config:update
This should trigger an update to the bean. Check the Karaf log file for the 
lines containing
  setJettyPort(9500)
  setConfig() called. Received properties {...}


