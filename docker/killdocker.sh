#!/bin/bash
sudo docker stop $1 > /dev/null
sudo docker rm $1 > /dev/null
