#!/bin/bash


LOGFILE=/tmp/jds-servlet.log
log() {
    echo "[$(date): $*]" >> $LOGFILE
}



log "Gearing up"
while (true)
do
    log "Start servlet"
    java -jar winstone.jar --webroot=webroot --httpPort=9997  --ajp13Port=8998 2>>$LOGFILE >>$LOGFILE 
    log " * Servlet died: $?"
done
