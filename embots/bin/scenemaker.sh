#!/bin/sh
echo "********* Starting SceneMaker on UNIX... **********"
BINDIR=`dirname "$0"`
BINDIR=`(cd $BINDIR ; pwd)`

JARDIR=`(cd $BINDIR/../lib ; pwd)`

cd $BINDIR
java -classpath $JARDIR/JCup.jar:$JARDIR/JFlex.jar:$JARDIR/JOSC.jar:$JARDIR/SceneMaker3.jar de.dfki.scenemaker.SceneMaker
