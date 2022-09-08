#!/bin/bash
TP_PATH=/mnt/c/Users/guigo/Documents/Github/POD/tpe1/tpe1-g13-parent

cd $TP_PATH
mvn install

cd server/target
tar -xvzf tpe1-g13-parent-server-1.0-SNAPSHOT-bin.tar.gz
cd tpe1-g13-parent-server-1.0-SNAPSHOT
chmod +x *.sh

cd $TP_PATH/client/target
tar -xvzf tpe1-g13-parent-client-1.0-SNAPSHOT-bin.tar.gz
cd tpe1-g13-parent-client-1.0-SNAPSHOT
chmod +x *.sh

cd $TP_PATH