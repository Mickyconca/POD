#!/bin/bash
#
#PATH_TO_CODE_BASE=`pwd`
#
#JAVA_OPTS="-Djava.rmi.server.codebase=file://$PATH_TO_CODE_BASE/lib/jars/tpe1-g13-parent-client-1.0-SNAPSHOT.jar"
#
#MAIN_CLASS="ar.edu.itba.pod.client.Client"
#
#java $JAVA_OPTS -cp 'lib/jars/*'  $MAIN_CLASS $*
java "$@" -cp 'lib/jars/*' "ar.edu.itba.pod.client.Client"
