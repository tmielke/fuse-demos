#Overview

This directory contains some JBoss-Fuse / Karaf / OSGi specific demos.

## JBoss-Client-OSGi
OSGi bundle that invokes a J2EE bean deployed in JBoss EAP.
 
## OSGiConfigAdminManagedPropertiesExample
Demo that shows how to use the OSGi Configuration Admin Service and managed configuration properties.

## OSGiServiceLoadingResourcesFromExternalBundle
This demo consists of two OSGi bundles. 
serviceBundle exposes a simple OSGi service which takes a resource string as an argument.
clientBundle invokes the service and passes in a resource url that points to a resource within 
the clientBundle jar file. 
The service that is invoked is able to load the resource from the other bundle.



