#!/bin/sh


for i in jboss-client-all.jar ./target/classes ./target/simple-ejb-client-1.0.0.jar ~/.m2/repository/org/slf4j/slf4j-api/1.6.6/slf4j-api-1.6.6.jar ~/.m2/repository/org/slf4j/slf4j-log4j12/1.6.6/slf4j-log4j12-1.6.6.jar ~/.m2/repository/log4j/log4j/1.2.8/log4j-1.2.8.jar ../Simple-EJB/target/simple-ejb-demo-1.0.0-client.jar 
do 
  CLASSPATH=$CLASSPATH:$i 
done

echo "Using CLASSPATH=$CLASSPATH"
java -cp $CLASSPATH org.apache.servicemix.demo.simpleejb.client.Client
