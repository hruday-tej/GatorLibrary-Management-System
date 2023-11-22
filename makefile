SOURCES = $(wildcard *.java)
CLASSES = $(wildcard *.class)
TEXT_FILES = $(wildcard *.txt)

default:
	javac $(SOURCES)
clean:
	rm -rf $(CLASSES)
	rm -rf $(TEXT_FILES)