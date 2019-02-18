#!/bin/bash

log() {
    echo "[$(date)] $*" >> /tmp/pdf-diff.log
}

START="$1"
STOP="$2"
FILE="$3"

log " args: \"$1\" \"$2\" \"$3\""
#echo " args: \"$1\" \"$2\" \"$3\""

FILE_DIR=/var/www/html/juneday-pdf/
START_FILE="${FILE_DIR}/${START}/junedaywiki/${FILE}.pdf"
STOP_FILE="${FILE_DIR}/${STOP}/junedaywiki/${FILE}.pdf"

RES_DIFF_DIR=webroot/diff-pdf/
RES_DIFF_FILE="${RES_DIFF_DIR}/$START-$STOP-$FILE".png
#echo "FILE: $RES_DIFF_FILE  <--- $FILE"

#ls -al $START_FILE
#ls -al $STOP_FILE

if [ ! -f $RES_DIFF_FILE ]
then
    log "pdf-diff $START_FILE $STOP_FILE > $RES_DIFF_FILE"
    pdf-diff $START_FILE $STOP_FILE > $RES_DIFF_FILE 2>error.txt
    RET=$?
#    mogrify -verbose $RES_DIFF_FILE 2>> /tmp/pdf-diff.log >> /tmp/pdf-diff.log
#    mogrify -geometry 400% $RES_DIFF_FILE  2>> /tmp/pdf-diff.log >> /tmp/pdf-diff.log
#    mogrify -verbose $RES_DIFF_FILE  2>> /tmp/pdf-diff.log >> /tmp/pdf-diff.log
    log " ==> $RET"
    if [ $RET -ne 0 ]
    then
	EMPTY=$(tail -1 error.txt | grep "There are no text differences" | wc -l)
	if [ $EMPTY -ne 0 ]
	then
	    log " * creating no diff image"
	    convert -size 640x480 xc:white -pointsize 40 -fill black -draw 'text 20,46 "No diff found"' $RES_DIFF_FILE
	    ls -al $RES_DIFF_FILE
	else
	    log " * error - removing file ($RES_DIFF_FILE) just in case"
	    # in case a "crap" file has been created
	    rm -f $RES_DIFF_FILE
	fi
    fi
    ls -al $RES_DIFF_DIR
else
    log "$RES_DIFF_FILE already exists...." 
fi
