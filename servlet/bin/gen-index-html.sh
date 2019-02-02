#!/bin/bash

DATE=$(date '+%Y%m%d')
MONTH=$(date --date "-1 month" '+%Y%m%d')
YEAR=$(date --date "-1 year" '+%Y%m%d')
START_DATE=20170313

WIDE_DATE=$(date '+%Y-%m-%d')
WIDE_MONTH=$(date --date "-1 month" '+%Y-%m-%d')
WIDE_YEAR=$(date --date "-1 year" '+%Y-%m-%d')
WIDE_START_DATE=2017-03-13

BOOK_PAGES=600
LOC_PER_BOOK_PAGE=50
FILM_LENGTH=100
JD_FILM_LENGTH=7

wiki() {
    cat /var/www/html/junedaywiki-stats/$1/jd-stats.json | jq '.["book-summary"].pages' | sed 's,\",,g'
}

daily() {
    START=$1
    STOP=$2
    AMOUNT=$3
    DAYS=$(( ($(date --date="$STOP" +%s) - $(date --date="$START" +%s) )/(60*60*24) ))
    RATIO=$( echo "scale = 2; $AMOUNT / $DAYS" | bc -l)
#    echo "$1 $2 >= $DAYS | $AMOUNT / $DAYS => $RATIO"
    echo "$RATIO"
}

wiki_to_book(){
    AMOUNT=$1
    BOOK_EQ=$(( AMOUNT / BOOK_PAGES ))

    if [ $BOOK_EQ -lt 1 ]
    then
	BOOK_EQ_P=$( echo "scale = 2; $AMOUNT / $BOOK_PAGES * 100" | bc -l)
	echo "$BOOK_EQ_P% of a book"
    else
	BOOK_EQ_E=$( echo "scale = 2; $AMOUNT / $BOOK_PAGES " | bc -l)
	echo "$BOOK_EQ_E books "
    fi
}

loc_to_book() {
 #   echo "loc_to_book() $1 | $AMOUNT / $LOC_PER_BOOK_PAGE / $BOOK_PAGES "
    AMOUNT=$1
    BOOK_EQ=$(( AMOUNT / LOC_PER_BOOK_PAGE / BOOK_PAGES ))

#    echo "loc_to_book() BOOK_EQ: $BOOK_EQ"
    if [ $BOOK_EQ -lt 1 ]
    then
	BOOK_EQ_P=$( echo "scale = 2; $AMOUNT / $LOC_PER_BOOK_PAGE / $BOOK_PAGES * 100" | bc -l)
	echo "$BOOK_EQ_P% of a book"
    else
	BOOK_EQ_E=$( echo "scale = 2; $AMOUNT / $LOC_PER_BOOK_PAGE / $BOOK_PAGES" | bc -l)
	echo "$BOOK_EQ_E books "
	#($AMOUNT / $LOC_PER_BOOK_PAGE / $BOOK_PAGES )"
    fi
}

video_to_book() {
    AMOUNT=$1
    MINS=$(( AMOUNT * $JD_FILM_LENGTH / $FILM_LENGTH))

    if [ $MINS -lt 1 ]
    then
	MINS_P=$( echo "scale = 2; $AMOUNT * $JD_FILM_LENGTH / $FILM_LENGTH * 100" | bc -l)
	echo "$MINS_P% of a DVD"
    else
	MINS_P=$( echo "scale = 2; $AMOUNT * $JD_FILM_LENGTH / $FILM_LENGTH" | bc -l)
	echo "$MINS_P DVDs"
    fi
}

sources() {
    cat /var/www/html/junedaywiki-stats/$1/jd-stats.json  | jq '."source-code"[]."lines-of-code"' | sed 's,\",,g' | awk '{s+=$1} END {print s}' 
}

videos() {
    cat /var/www/html/junedaywiki-stats/$1/jd-stats.json  | jq '."vimeo-stats".videos' | sed 's,\",,g' 
}

WIKI_DATE=$(wiki $DATE)
WIKI_MONTH=$(wiki $MONTH)
WIKI_YEAR=$(wiki $YEAR)
WIKI_START=$(wiki $START_DATE)

SOURCE_DATE=$(sources $DATE)
SOURCE_MONTH=$(sources $MONTH)
SOURCE_YEAR=$(sources $YEAR)
SOURCE_START=$(sources $START_DATE)

VIDEO_DATE=$(videos $DATE)
VIDEO_MONTH=$(videos $MONTH)
VIDEO_YEAR=$(videos $YEAR)
VIDEO_START=$(videos $START_DATE)


WIKI_LAST_MONTH=$(( WIKI_DATE - WIKI_MONTH ))
WIKI_LAST_YEAR=$(( WIKI_DATE - WIKI_YEAR ))
WIKI_START=$(( WIKI_DATE - WIKI_START ))

SOURCE_LAST_MONTH=$(( SOURCE_DATE - SOURCE_MONTH ))
SOURCE_LAST_YEAR=$(( SOURCE_DATE - SOURCE_YEAR ))

VIDEO_LAST_MONTH=$(( VIDEO_DATE - VIDEO_MONTH ))
VIDEO_LAST_YEAR=$(( VIDEO_DATE - VIDEO_YEAR ))

echo "----------------------------"
echo "$DATE:       $WIKI_DATE  | $SOURCE_DATE  | $VIDEO_DATE "
echo "$MONTH:      $WIKI_MONTH | $SOURCE_MONTH | $VIDEO_MONTH"e
echo "$YEAR:       $WIKI_YEAR  | $SOURCE_YEAR  | $VIDEO_YEAR"
echo "$START_DATE: $WIKI_START | $SOURCE_START | $VIDEO_START"
echo "----------------------------"

echo "----------------------------"
echo "$WIKI: $WIKI_LAST_MONTH "
echo "----------------------------"

# Wiki
WM_DAILY=$(daily $MONTH $DATE $WIKI_LAST_MONTH)
WM_EQ=$(wiki_to_book $WIKI_LAST_MONTH)

WY_DAILY=$(daily $YEAR $DATE $WIKI_LAST_YEAR)
WY_EQ=$(wiki_to_book $WIKI_LAST_YEAR)

W_START=$(wiki_to_book $WIKI_DATE )

# Source
SM_DAILY=$(daily $MONTH $DATE $SOURCE_LAST_MONTH)
SM_EQ=$(loc_to_book $SOURCE_LAST_MONTH)

SY_DAILY=$(daily $YEAR $DATE $SOURCE_LAST_YEAR)
SY_EQ=$(loc_to_book $SOURCE_LAST_YEAR)

S_START=$(loc_to_book $SOURCE_DATE )

# Video
VM_DAILY=$(daily $MONTH $DATE $VIDEO_LAST_MONTH)
VM_EQ=$(video_to_book $VIDEO_LAST_MONTH)
VM_START=$(video_to_book $VIDEO_START)

VY_DAILY=$(daily $YEAR $DATE $VIDEO_LAST_YEAR)
VY_EQ=$(video_to_book $VIDEO_LAST_YEAR)

V_START=$(video_to_book $VIDEO_START)
#



cat etc/index.tmpl \
    | sed "s,__TODAY__,$WIDE_DATE,g"  \
    | sed "s,__BOOK_PAGES__,$BOOK_PAGES,g"  \
    | sed "s,__FILM_LENGTH__,$FILM_LENGTH,g"  \
    | sed "s,__MONTH_PERIOD__,$WIDE_MONTH,g"  \
    | sed "s,__WIKI_MONTH__,$WIKI_LAST_MONTH,g"  \
    | sed "s,__WIKI_MONTH_EQ__,$WM_EQ,g" \
    | sed "s,__WIKI_MONTH_DAILY__,$WM_DAILY,g"  \
    | sed "s,__YEAR_PERIOD__,$WIDE_YEAR,g"  \
    | sed "s,__WIKI_YEAR__,$WIKI_LAST_YEAR,g"  \
    | sed "s,__WIKI_YEAR_EQ__,$WY_EQ,g" \
    | sed "s,__WIKI_YEAR_DAILY__,$WY_DAILY,g"  \
    | sed "s,__SOURCE_MONTH__,$SOURCE_LAST_MONTH,g"  \
    | sed "s,__SOURCE_MONTH_EQ__,$SM_EQ,g" \
    | sed "s,__SOURCE_MONTH_DAILY__,$SM_DAILY,g"  \
    | sed "s,__YEAR_PERIOD__,$WIDE_YEAR,g"  \
    | sed "s,__SOURCE_YEAR__,$SOURCE_LAST_YEAR,g"  \
    | sed "s,__SOURCE_YEAR_EQ__,$SY_EQ,g" \
    | sed "s,__SOURCE_YEAR_DAILY__,$SY_DAILY,g"  \
    | sed "s,__VIDEO_MONTH__,$VIDEO_LAST_MONTH,g"  \
    | sed "s,__VIDEO_MONTH_EQ__,$VM_EQ,g" \
    | sed "s,__VIDEO_MONTH_DAILY__,$VM_DAILY,g"  \
    | sed "s,__YEAR_PERIOD__,$WIDE_YEAR,g"  \
    | sed "s,__VIDEO_YEAR__,$VIDEO_LAST_YEAR,g"  \
    | sed "s,__VIDEO_YEAR_EQ__,$VY_EQ,g" \
    | sed "s,__VIDEO_YEAR_DAILY__,$VY_DAILY,g"  \
    | sed "s,__TOTAL_BOOKS__,$W_START,g"  \
    | sed "s,__TOTAL_LOC_BOOKS__,$S_START,g"  \
    | sed "s,__TOTAL_FILMS__,$V_START,g"  \
	  > webroot/index.html
