LIB_DIR=lib
SERVLET_DIR=webroot/WEB-INF/
SERVLET_LIB_DIR=$(SERVLET_DIR)/lib

SERVLET_JAVA=\
   ./webroot/WEB-INF/classes/se/juneday/junedaystat/JunedayStatAPI.java
SERVLET_CLASSES=$(SERVLET_JAVA:.java=.class)

JSON_FILE=org.json.jar
JSON_URL=https://search.maven.org/remotecontent?filepath=org/json/json/20171018/json-20171018.jar
JSON_JAR=$(SERVLET_LIB_DIR)/$(JSON_FILE)

JDS_JAR_FILE=jds-stat.jar
JDS_JAR=$(SERVLET_LIB_DIR)/$(JDS_JAR_FILE)


DEPS=$(JSON_JAR) $(JAVA_CLASSES) $(WINSTONE_JAR) $(JDS_JAR)


WINSTONE_JAR=winstone.jar

CLASSPATH=.:$(JSON_JAR):$(WINSTONE_JAR):$(JDS_JAR)

%.class:%.java
	javac -cp $(CLASSPATH) $<

all: $(DEPS) $(SERVLET_CLASSES) $(JDS_JAR)

../$(JDS_JAR_FILE):
	cd .. && make clean && make && make jar

jds:
	-rm ../$(JDS_JAR_FILE) 
	-rm ./$(JDS_JAR)

$(JDS_JAR): ../$(JDS_JAR_FILE)
	cp ../$(JDS_JAR_FILE) $(JDS_JAR)


$(WINSTONE_JAR):
	wget 'https://sourceforge.net/projects/winstone/files/latest/download?source=typ_redirect' -O $(WINSTONE_JAR)


lib:
	mkdir $(LIB_DIR)


$(JSON_JAR): $(LIB_DIR)
	curl -o $(JSON_JAR) $(JSON_URL)


run:
	java -jar winstone.jar --webroot=webroot
