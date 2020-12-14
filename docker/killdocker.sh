#!/bin/bash
for i in `seq $1 $2`; do
sudo docker stop Clav$i > /dev/null
sudo docker rm Clav$i > /dev/null; done

