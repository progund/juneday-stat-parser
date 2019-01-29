#!/bin/bash

log() {
    echo "[$(date): $*]" >> /tmp/jds-servlet.log
}



log "Gearing up"
while (true)
do
    log "Start servlet"
    java -jar winstone.jar --webroot=webroot --httpPort=9997  --ajp13Port=8998
    log " * Servlet died: $?"
done
