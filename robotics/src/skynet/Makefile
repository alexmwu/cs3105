JC=javac
JFLAGS=-cp ../:ToolBox.jar
CLASSES=\
CourseManager.java \
Goal.java \
Object.java \
PFRobot.java \
Robot.java \
RRTRobot.java \
FileChooser.java \
Makefile \
Obstacle.java \
PotFields.java \
RRT.java

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
