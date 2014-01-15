#!/bin/sh
echo "Starting BehaviorBuilder on Mac/Unix..."

BINDIR=`dirname "$0"`
cd $BINDIR

java -classpath ../dist/EMBOTS.jar de.dfki.embots.behaviorbuilder.BehaviorBuilder