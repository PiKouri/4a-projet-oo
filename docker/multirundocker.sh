#!/bin/bash
for i in `seq 1 $1`; do
./rundocker.sh Clav$i; done
