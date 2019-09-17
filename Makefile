VERSION=0.12
INSTALL_BIN_DIR=$(HOME)/bin
INSTALL_LIB_DIR=$(HOME)/.juneday/lib
INSTALL_CACHE_DIR=$(HOME)/.juneday/stat/cache

JAVA_SRC= \
  ./se/juneday/junedaystat/utils/Utils.java \
  ./se/juneday/junedaystat/domain/JunedayStat.java \
  ./se/juneday/junedaystat/domain/Chapter.java \
  ./se/juneday/junedaystat/domain/Book.java \
  ./se/juneday/junedaystat/domain/CodeSummary.java \
  ./se/juneday/junedaystat/domain/VideoStat.java \
  ./se/juneday/junedaystat/domain/Presentation.java \
  ./se/juneday/junedaystat/domain/PodStat.java \
  ./se/juneday/junedaystat/domain/BooksSummary.java \
  ./se/juneday/junedaystat/domain/exporter/GenericBookExporter.java \
  ./se/juneday/junedaystat/domain/exporter/JsonBookExporter.java \
  ./se/juneday/junedaystat/domain/exporter/GenericChapterExporter.java \
  ./se/juneday/junedaystat/domain/exporter/JsonChapterExporter.java \
  ./se/juneday/junedaystat/domain/exporter/GenericPresentationExporter.java \
  ./se/juneday/junedaystat/domain/exporter/JsonPresentationExporter.java \
  ./se/juneday/junedaystat/net/StatisticsParser.java \
  ./se/juneday/junedaystat/measurement/Measurement.java \
  ./se/juneday/junedaystat/measurement/exporter/HtmlExporter.java \
  ./se/juneday/junedaystat/ui/JDCli.java
#  ./se/juneday/junedaystat/domain/exporter/SQLChapterExporter.java \


JAVA_CLASSES=$(JAVA_SRC:.java=.class)

LIB_DIR=lib
DATA_DIR=$(HOME)/

# JSON
JSON_FILE=org.json.jar
JSON_URL=https://search.maven.org/remotecontent?filepath=org/json/json/20171018/json-20171018.jar
JSON_JAR=$(LIB_DIR)/$(JSON_FILE)

DEPS=$(JSON_JAR) $(JAVA_CLASSES)

CLASSPATH=$(JSON_JAR):.

CLI=se.juneday.junedaystat.ui.JDCli

all: $(DEPS)

lib:
	mkdir lib
$(JSON_JAR): lib
	curl -o $(JSON_JAR) $(JSON_URL)

%.class:%.java
	javac -cp $(CLASSPATH) $< 

install:
	mkdir -p $(INSTALL_BIN_DIR)
	mkdir -p $(INSTALL_LIB_DIR)
	mkdir -p $(INSTALL_CACHE_DIR)
	cat bin/juneday-stat.sh.tmpl | \
	sed -e 's,__INSTALL_BIN_DIR__,$(INSTALL_BIN_DIR),g' \
	 -e 's,__INSTALL_LIB_DIR__,$(INSTALL_LIB_DIR),g' \
	 -e 's,__INSTALL_CACHE_DIR__,$(INSTALL_CACHE_DIR),g' \
	> $(INSTALL_BIN_DIR)/juneday-stat.sh
	chmod a+x $(INSTALL_BIN_DIR)/juneday-stat.sh

info: 
	@echo "Classes: $(JAVA_CLASSES)"

clean:
	rm -f $(JAVA_CLASSES)
	find . -name "*.class" | xargs rm -fr

DATA_DIR=$(HOME)/.juneday/stat/cache
DATA_DIR_ARG=-Djuneday_data_dir=$(DATA_DIR)

%.json:
	@echo "-- Downloading $@ --"
	mkdir -p $(DATA_DIR)/$(shell echo $@ | sed -e "s,${DATA_DIR},,g" -e "s,/jd-stats\.json,,g")
	curl -s http://rameau.sandklef.com/junedaywiki-stats/$(shell echo $@ | sed "s,$(DATA_DIR),,g") -o $@
#	curl -s http://rameau.sandklef.com/junedaywiki-stats/$(shell echo $@ | sed "$(DATA_DIR)/jd-stats-\([0-9]*\)\.json,\1/jd-stats.json, g") -o $@


jar: $(JAVA_CLASSES)
	jar cvf juneday-stat-$(VERSION).jar se

JSON_FILES=$(DATA_DIR)/20190127/jd-stats.json $(DATA_DIR)/20181107/jd-stats.json $(DATA_DIR)/20181231/jd-stats.json $(DATA_DIR)/20190114/jd-stats.json $(DATA_DIR)/$(TODAY)/jd-stats.json $(DATA_DIR)/$(WEEKAGO)/jd-stats.json $(DATA_DIR)/20181107/jd-stats.json $(DATA_DIR)/20181231/jd-stats.json  $(DATA_DIR)/20190131/jd-stats.json   $(DATA_DIR)/20190130/jd-stats.json 

json: $(JSON_FILES)

html: $(JSON_FILES) $(JAVA_CLASSES)
	@java $(DATA_DIR_ARG) -cp $(CLASSPATH) $(CLI) 20181107  20181231 --html > /tmp/jd.html
	@cat /tmp/jd.html

run: $(JSON_FILES) $(JAVA_CLASSES)
	java $(DATA_DIR_ARG) -cp $(CLASSPATH) $(CLI)

run: $(JSON_FILES) $(JAVA_CLASSES)
	java $(DATA_DIR_ARG) -cp $(CLASSPATH) $(CLI)

2018: $(DATA_DIR)/20180101/jd-stats.json $(DATA_DIR)/20190101/jd-stats.json
	java $(DATA_DIR_ARG) -cp $(CLASSPATH) $(CLI) 20180101 20190101 

TODAY=$(shell date +%Y%m%d)
WEEKAGO=$(shell date --date="7 day ago" +%Y%m%d)

book: $(JSON_FILES)  $(JAVA_CLASSES)
	java $(DATA_DIR_ARG) -cp $(CLASSPATH) $(CLI) $(TODAY) $(WEEKAGO)  --books

book2: $(JSON_FILES)  $(JAVA_CLASSES)
	java $(DATA_DIR_ARG) -cp $(CLASSPATH) $(CLI) 20181107  20181231 --books

single31: $(JSON_FILES)   $(JAVA_CLASSES)
	java $(DATA_DIR_ARG) -cp $(CLASSPATH) $(CLI) 20190131 --single-book

single30: $(JSON_FILES)   $(JAVA_CLASSES)
	java $(DATA_DIR_ARG) -cp $(CLASSPATH) $(CLI) 20190130 --single-book

