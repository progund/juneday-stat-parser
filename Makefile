
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
  ./se/juneday/junedaystat/net/StatisticsParser.java \
  ./se/juneday/junedaystat/domain/Measurement.java \
  ./se/juneday/junedaystat/ui/JDCli.java

JAVA_CLASSES=$(JAVA_SRC:.java=.class)

LIB_DIR=lib
DATA_DIR=data

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

info: 
	@echo "Classes: $(JAVA_CLASSES)"

clean:
	rm -f $(JAVA_CLASSES)

%.json:
	mkdir -p $(DATA_DIR)
	curl http://rameau.sandklef.com/junedaywiki-stats/`echo $@ | sed 's,data/jd-stats-\([0-9]*\)\.json,\1/jd-stats.json, g'` -o $@

run: data/jd-stats-20181231.json data/jd-stats-20181101.json data/jd-stats-20181213.json data/jd-stats-20190114.json  data/jd-stats-20190112.json $(JAVA_CLASSES)
	java -cp $(CLASSPATH) $(CLI)

2018: data/jd-stats-20180101.json data/jd-stats-20190101.json
	java -cp $(CLASSPATH) $(CLI) 20180101 20190101 

book: data/jd-stats-20180101.json data/jd-stats-20190101.json
	java -cp $(CLASSPATH) $(CLI) 20180101 20190101 --books
