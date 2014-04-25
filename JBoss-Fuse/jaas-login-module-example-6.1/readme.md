# JAAS Login Module Example

Verified on JBoss Fuse 6.1.

This demo re-configures the JAAS authentication in a Fabric managed JBoss Fuse 6.1 environment
to offer a fallback mechanism. A user that is not authenticated by the default ZookeeperLoginModule
will be tried to be authenticated using LDAP based authentication mechanism before raising an authentication error.
In other words this JAAS configuration will authenticate any user that is defined in either the default
ZookeeperLoginModule or in LDAP.

This is achieved by configuring a new ZookeeperLoginModule using a higher rank 4 than the default ZookeeperLoginModule
and by assigning it the flag sufficient.
In addition an LDAP login module is configured next with the same flag sufficient.
The documenation for these flags can be found at
http://docs.oracle.com/javase/6/docs/api/javax/security/auth/login/Configuration.html

In essence the configuration is as follows (some details omitted for clarity).

```xml
  <jaas:config name="karaf" rank="4">
    <jaas:module className="io.fabric8.jaas.ZookeeperLoginModule" flags="sufficient">
      ...
    </jaas:module>

    <jaas:module className="org.apache.karaf.jaas.modules.ldap.LDAPLoginModule" flags="required">
      ...
    </jaas:module>
  </jaas:config>
```

See src/main/resources/OSGI-INF/blueprint/jaas.xml.

As per the docs at
https://access.redhat.com/site/documentation/en-US/Red_Hat_JBoss_Fuse/6.1/html/Security_Guide/files/ESBSecureJAASRealmDef.html
a jaas configuration with a higher rank takes precedence over a jaas config with a lower rank.
The default ZookeeperLoginModule uses rank 1, the ZookeeperLoginModule defined in this example uses rank 4 and will
therefore be used instead when authenticating users.


The configuration of the LDAPLoginModule configures for an LDAP tree that is setup similar but not equal to the LDAP
tutorial in the JBoss Fuse 6.1 Security Guide at
https://access.redhat.com/site/documentation/en-US/Red_Hat_JBoss_Fuse/6.1/html/Security_Guide/files/FESBLDAPTutorial.html.
The values for user.base.dn, role.base.dn will most likely need to be changed at the least to work with your LDAP system.



## Compiling

Simply run `mvn install` to compile the demo.


## Deploying
Make sure the LDAPLoginModule is correctly configured to work against your LDAP server.
If thats the case, follow these steps:

- Start JBoss Fuse 6.1
- create a Fabric (if not already done) using fabric:create --clean
- add this demo bundle to the `fabric` profile:
  fabric:profile-edit --bundles io/fabric8/jaas/test/jaas-login-module-example/6.1 fabric

- run `jaas:realms` to verify the login modules got deployed. The output should be similar to

  JBossFuse:karaf@root> jaas:realms
  Index Realm                Module Class
      1 karaf                org.apache.karaf.jaas.modules.properties.PropertiesLoginModule
      2 karaf                org.apache.karaf.jaas.modules.publickey.PublickeyLoginModule
      3 karaf                io.fabric8.jaas.ZookeeperLoginModule
      4 karaf                org.apache.karaf.jaas.modules.ldap.LDAPLoginModule
      5 karaf                io.fabric8.jaas.ZookeeperLoginModule


- add a sample user to the new ZookeeperLoginModule definition:
  jaas:manage --index 5
  jaas:useradd testUser secret
  jaas:roleadd testUser admin
  jaas:update
  jaas:manage --index 5
  jaas:users

  should list the new user testUser with the last command.

- test the JAAS configuration using the bin/client script of JBoss Fuse 6.1
  `bin/client -u testUser -p secret` should succeed the authentication

  `bin/client -u <ldapUser> -p <ldapPassword>`
   replacing <ldapUser> and <ldapPassword> with the user credentials of a
   user defined in LDAP should also succeed.


## Trouble Shooting

In case the LDAPLoginModule does not work as expected I suggest to enable DEBUG logging for
org.apache.karaf.jaas.modules.ldap and retry. The Karaf log file should contain some useful logging.
In addition I find it useful to enable verbose logging in the LDAP server as well.


