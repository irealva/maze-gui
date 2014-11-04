#3137 homework 6 Makefile
JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
	
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	DisjSets.java \
	Maze.java \
	MazeGUI.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class *~

#end of file
