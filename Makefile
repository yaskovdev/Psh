SOURCES = *.java  org/spiderland/Psh/*.java org/spiderland/Psh/ProbClass/*.java org/spiderland/Psh/Coevolution/*.java
CLASSES    = *.class org/spiderland/Psh/*.class org/spiderland/Psh/ProbClass/*.class org/spiderland/Psh/Coevolution/*.class

.PHONY: docs

all: Psh.jar docs

Psh.jar: $(SOURCES) 
    javac -source 1.6 -target 1.6 -Xlint $(SOURCES)
    jar cf Psh.jar Manifest LICENSE NOTICE README.md $(CLASSES)

clean:
    rm -f org/spiderland/Psh/*.class *.class Psh.jar

tilde:
    rm -f *~
    rm -f gpsamples/*~
    rm -f pushsamples/*~
    rm -f tools/*~
    rm -f org/spiderland/Psh/*~

test:
    java -cp junit-4.4.jar:. junit.textui.TestRunner org.spiderland.Psh.ProgramTest

docs:
    javadoc -d docs/api org.spiderland.Psh
