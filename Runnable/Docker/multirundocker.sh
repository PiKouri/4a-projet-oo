#!/bin/bash
for i in `seq 1 $1`; do
./rundocker.sh $i; done
