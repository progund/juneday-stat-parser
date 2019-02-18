#!/bin/bash

DATE=$(date '+%Y%m%d')
#DATE=20190202
MONTH=$(date --date "-1 month" '+%Y%m%d')
YEAR=$(date --date "-1 year" '+%Y%m%d')
START_DATE=20170313

WIDE_DATE=$(date '+%Y-%m-%d')
#WIDE_DATE=2019-02-02
WIDE_MONTH=$(date --date "-1 month" '+%Y-%m-%d')
WIDE_YEAR=$(date --date "-1 year" '+%Y-%m-%d')
WIDE_START_DATE=2017-03-13

BOOK_PAGES=600
LOC_PER_BOOK_PAGE=50
FILM_LENGTH=102
JD_FILM_LENGTH=7

TOT_PDF_BOOK=0


wiki() {
    cat /var/www/html/junedaywiki-stats/$1/jd-stats.json | jq '.["book-summary"].pages' | sed 's,\",,g'
}

presentations() {
    cat /var/www/html/junedaywiki-stats/$1/jd-stats.json | jq '.["book-summary"]."uniq-presentations-pages"' | sed 's,\",,g'
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
	BOOK_EQ=$( echo "scale = 2; $AMOUNT / $BOOK_PAGES * 100" | bc -l)
	echo "$BOOK_EQ% of a book<sup>1</sup>"
    else
	BOOK_EQ=$( echo "scale = 2; $AMOUNT / $BOOK_PAGES " | bc -l)
	echo "$BOOK_EQ books<sup>1</sup> "
    fi
}

pres_to_book(){
    AMOUNT=$1
    BOOK_EQ=$(( AMOUNT / BOOK_PAGES ))

    if [ $BOOK_EQ -lt 1 ]
    then
	BOOK_EQ=$( echo "scale = 2; $AMOUNT / $BOOK_PAGES * 100" | bc -l)
	echo "$BOOK_EQ% of a book<sup>3</sup>"
    else
	BOOK_EQ=$( echo "scale = 2; $AMOUNT / $BOOK_PAGES " | bc -l)
	echo "$BOOK_EQ books<sup>3</sup> "
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
	echo "$BOOK_EQ_P% of a book<sup>2</sup>"
    else
	BOOK_EQ_E=$( echo "scale = 2; $AMOUNT / $LOC_PER_BOOK_PAGE / $BOOK_PAGES" | bc -l)
	echo "$BOOK_EQ_E books<sup>2</sup> "
	#($AMOUNT / $LOC_PER_BOOK_PAGE / $BOOK_PAGES )"
    fi
}

film_lengthold() {
    AMOUNT=$1
    MINUTES=$(( $AMOUNT * $JD_FILM_LENGTH))
    HOURS=$(( $MINUTES / 60 ))
    if [ $HOURS -ne 0 ] 
    then
	REMAINDER=$(( $MINUTES / ($HOURS * 60 ) ))
	if [ $REMAINDER -eq 0 ]
	then
	    echo "$HOURS hours"
	elif [ $REMAINDER -le 1 ]
	then
	    echo "$HOURS hours and $REMAINDER minute"
	else
	    echo "$HOURS hours and $REMAINDER minute(s)"
	fi
    else
	REMAINDER=0
	echo "$MINUTES minutes"
    fi
}

film_length() {
    AMOUNT=$1
    MINUTES=$(( $AMOUNT * $JD_FILM_LENGTH))
    echo "$MINUTES"
}

video_to_dvd() {
    AMOUNT=$1
    MINS=$(( $AMOUNT  / $FILM_LENGTH))

    if [ $MINS -lt 1 ]
    then
	MINS_P=$( echo "scale = 2; $AMOUNT / $FILM_LENGTH * 100" | bc -l)
	echo "$MINS_P% of a DVD<sup>4</sup> "
    else
	MINS_P=$( echo "scale = 2; $AMOUNT / $FILM_LENGTH" | bc -l)
	echo "$MINS_P DVDs<sup>4</sup>"
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

PRES_DATE=$(presentations $DATE)
PRES_MONTH=$(presentations $MONTH)
PRES_YEAR=$(presentations $YEAR)
PRES_START=$(presentations $START_DATE)

WIKI_LAST_MONTH=$(( WIKI_DATE - WIKI_MONTH ))
WIKI_LAST_YEAR=$(( WIKI_DATE - WIKI_YEAR ))
WIKI_START=$(( WIKI_DATE - WIKI_START ))

SOURCE_LAST_MONTH=$(( SOURCE_DATE - SOURCE_MONTH ))
SOURCE_LAST_YEAR=$(( SOURCE_DATE - SOURCE_YEAR ))

VIDEO_LAST_MONTH=$(film_length $(( VIDEO_DATE - VIDEO_MONTH )) )
VIDEO_LAST_YEAR=$(film_length $(( VIDEO_DATE - VIDEO_YEAR )) )

PRES_LAST_MONTH=$(( PRES_DATE - PRES_MONTH ))
PRES_LAST_YEAR=$(( PRES_DATE - PRES_YEAR ))


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
VM_EQ=$(video_to_dvd $VIDEO_LAST_MONTH)
VM_START=$(video_to_dvd $VIDEO_START)

VY_DAILY=$(daily $YEAR $DATE $VIDEO_LAST_YEAR)
VY_EQ=$(video_to_dvd $VIDEO_LAST_YEAR)

V_START=$(video_to_dvd $(film_length $VIDEO_DATE) )
echo "VIDEO_DATE: $VIDEO_DATE => $V_START"

# Presentations
PM_DAILY=$(daily $MONTH $DATE $PRES_LAST_MONTH)
PM_EQ=$(pres_to_book $PRES_LAST_MONTH)
PM_START=$(pres_to_book $PRES_START)

PY_DAILY=$(daily $YEAR $DATE $PRES_LAST_YEAR)
PY_EQ=$(pres_to_book $PRES_LAST_YEAR)

P_START=$(pres_to_book $PRES_DATE)
#

add_tot_pdf() {
    EXPR=$1
    RES=$(echo "scale = 2;  $EXPR " | bc -l)
    TOT_PDF_BOOK=$( echo "scale = 2;  $TOT_PDF_BOOK + $RES" | bc -l)
}

echo "TOT_PDF_BOOK: $TOT_PDF_BOOK"
add_tot_pdf "$WIKI_DATE / $BOOK_PAGES"
echo "TOT_PDF_BOOK: $TOT_PDF_BOOK"
add_tot_pdf "$SOURCE_DATE / $LOC_PER_BOOK_PAGE / $BOOK_PAGES"
echo "TOT_PDF_BOOK: $TOT_PDF_BOOK"
add_tot_pdf "$PRES_DATE / $BOOK_PAGES"
echo "TOT_PDF_BOOK: $TOT_PDF_BOOK"

echo "----------------------------"
echo "$DATE:       $WIKI_DATE  | $SOURCE_DATE  | $VIDEO_DATE | $PRES_DATE "
echo "$MONTH:      $WIKI_MONTH | $SOURCE_MONTH | $VIDEO_MONTH| $PRES_MONTH"
echo "$YEAR:       $WIKI_YEAR  | $SOURCE_YEAR  | $VIDEO_YEAR | $PRES_YEAR"
echo "$START_DATE: $WIKI_START | $SOURCE_START | $VIDEO_START| $PRES_START"
echo "----------------------------"

mv webroot/index.html webroot/index.html.save 
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
    | sed "s,__PRES_MONTH__,$PRES_LAST_MONTH,g"  \
    | sed "s,__PRES_MONTH_EQ__,$PM_EQ,g" \
    | sed "s,__PRES_MONTH_DAILY__,$PM_DAILY,g"  \
    | sed "s,__YEAR_PERIOD__,$WIDE_YEAR,g"  \
    | sed "s,__PRES_YEAR__,$PRES_LAST_YEAR,g"  \
    | sed "s,__PRES_YEAR_EQ__,$PY_EQ,g" \
    | sed "s,__PRES_YEAR_DAILY__,$PY_DAILY,g"  \
    | sed "s,__TOTAL_BOOKS__,$W_START,g"  \
    | sed "s,__TOTAL_PRES__,$P_START,g"  \
    | sed "s,__TOTAL_LOC_BOOKS__,$S_START,g"  \
    | sed "s,__TOTAL_FILMS__,$V_START,g"  \
    | sed "s,__TOTAL_PDF_BOOK__,$TOT_PDF_BOOK,g"  \
	  > webroot/index.html
#=$(( W_START + P_START + S_START))


RET=$?

if [ $RET -ne 0 ]
then
    mv webroot/index.html.save webroot/index.html
fi
