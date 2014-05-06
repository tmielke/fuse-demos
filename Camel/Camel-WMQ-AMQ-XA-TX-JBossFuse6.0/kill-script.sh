#!/bin/sh
# Script that periodically kills the SMX instance during a load test.
# Script runs a fixed number of times

sleepTime=120
iterations=10

echo "Going to kill SMX $iterations times"

i=0
while [ $i -lt "$iterations" ] ; do
  pid=`jps -l -m | grep "org.apache.karaf.main.Main" | cut -c1-6`
  echo "Killing ESB instance with pid $pid. Counter is at $i."
  kill -9 $pid
  echo "Sleeping $sleepTime seconds"
  sleep $sleepTime
  i=`echo $i+1 | bc`
done
