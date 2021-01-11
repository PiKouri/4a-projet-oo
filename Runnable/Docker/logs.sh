#!/bin/bash
for i in `seq 1 $1`; do
docker cp Clav$i:log.log ./logs/logClav$i.log;
docker cp Clav$i:database.db ./logs/databaseClav$i.db; done
