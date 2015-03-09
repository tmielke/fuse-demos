#!/bin/sh

# Helper script that create a single jar file containing all JBoss
# classes needed by an external EJB client.
# If additional J2EE features are used, more jar files may be 
# required. The jar files used here perhaps form the minimal list
# of JBoss jar files required.

JBOSS_HOME=~/Desktop/sandbox/JBoss/jboss-5.1.0.GA

# cleanup from previous runs
if [ -d tmp ]
then 
  echo "Deleting previous tmp folder"
  rm -r tmp
fi

# create tmp folder
mkdir tmp
cd tmp

# extract all required JBoss jar files into tmp folder
for i in jboss-aop-client.jar jboss-aspect-jdk50-client.jar jboss-ejb3-common-client.jar jboss-ejb3-core-client.jar jboss-ejb3-proxy-impl-client.jar jboss-ejb3-proxy-spi-client.jar jboss-ejb3-security-client.jar jboss-integration.jar jboss-javaee.jar jboss-logging-spi.jar jboss-messaging-client.jar jboss-security-spi.jar jbosssx-client.jar jnp-client.jar jboss-remoting.jar jboss-common-core.jar concurrent.jar
do 
  echo "Exrtracting $i into ./tmp" 
  jar xvf $JBOSS_HOME/client/$i 2>&1 > /dev/null
  if [ $? != 0 ]
  then 
    echo "Problem extracting jar $i" 
  fi
done


# create uber jar
echo "Creating uber jar from tmp..." 
jar cvf ../jboss-client-all.jar * 2>&1 > /dev/null
if [ $? = 0 ]
then 
  echo "Created jboss-client-all.jar" 
else
  echo "Something went wrong." 
fi



