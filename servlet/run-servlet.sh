#!/bin/bash

THIS_PID=$$

LOGFILE=/tmp/jds-servlet.log
log() {
    echo "[$(date): $*]" >> $LOGFILE
}



start() {
    log "Gearing up"
    while (true)
    do
	log "Start servlet"
	java -jar winstone.jar --webroot=webroot --httpPort=9997  --ajp13Port=8998 2>>$LOGFILE >>$LOGFILE 
	log " * Servlet died: $?"
    done
}

killprocsig() {
    PID=$1
    SIG=$2
    if [ "$SIG" != "" ]
    then
	SIG="-TERM"
    fi
    if [ "$PID" != "" ]
    then
	kill $SIG $PID
    fi
}

killproc() {
    killprocsig "$1" -TERM
    sleep 0
    killprocsig "$1" -INT
    sleep 0
    killprocsig "$1" -KILL
    sleep 0
}

killscript() {
    PROC_ID=$( ps auxww | grep java | grep -e "--httpPort=9997"  | grep -v grep | grep -v emacs | awk ' { print $2}')
    echo  "SCRIPT PID: $PROC_ID"
    killproc "$PROC_ID"
}

killservlet() {
    PROC_ID=$(ps auxww | grep run-servlet.sh | grep -v grep | grep -v emacs | grep -v "$THIS_PID" | awk ' { print $2}')
    killproc "$PROC_ID"
}


stop() {
    echo "Kill servlet"
    killservlet
    echo "Kill script"
    killscript
}


case "$1" in
    "start"|"--start")
	start &
	exit 0
	;;
    "stop"|"--stop")
	stop
	exit 0
	;;
    "restart"|"--restart")
	stop
	sleep 1		  
	start
	exit 0
	;;
esac
