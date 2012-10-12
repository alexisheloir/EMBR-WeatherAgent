#!/bin/sh
echo "********* Starting EMBOTS on UNIX... **********"
BINDIR=`dirname "$0"`
BINDIR=`(cd $BINDIR ; pwd)`

# *** INSERT YOUR PATH TO MARY BELOW ***
MARYDIR=/Users/kipp/Applications/MARY

if [ ! -d "$MARYDIR" ] ; then
  echo "MARY directory not found in $MARYDIR. Cannot start."
  exit 1
fi

JARDIR=`(cd $BINDIR/../lib ; pwd)`
DISTDIR=`(cd $BINDIR/../dist ; pwd)`

if [ ! -e $JARDIR/semaine.jar ] ; then
  echo "No semaine.jar -- you need to do 'ant jars' in the java/ folder first\!"
  exit 1
fi

CONFIG=$1
if [ -z "$CONFIG" ] ; then
  CONFDIR=`(cd $BINDIR/../config ; pwd)`
  CONFIG=$CONFDIR/embots.config
fi

JMS_URL_SETTING=""
if [ -n "$CMS_URL" ] ; then
  echo "Connecting to JMS server at $CMS_URL"
  JMS_URL_SETTING="-Djms.url=$CMS_URL"
fi

cd $BINDIR

java -Xmx1g $JMS_URL_SETTING -classpath $DISTDIR/embots.jar:$JARDIR/jama.jar:$JARDIR/jsr173_1.0_api.jar:$JARDIR/xml-text-editor-0.0.2.jar:$JARDIR/semaine.jar:$JARDIR/semaine-mary.jar:$JARDIR/semaine-dialogue.jar:$JARDIR/JBML.jar:$JARDIR/log4j-1.2.15.jar:$JARDIR/commons-logging-1.1.1.jar:$JARDIR/activemq-all-5.1.0.jar:$JARDIR/JaCoP-3.1.jar:$JARDIR/jdom.jar:$JARDIR/jgraph.jar:$JARDIR/affect.jar -Dmary.base="$MARYDIR" eu.semaine.system.ComponentRunner $CONFIG
