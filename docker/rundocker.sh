#!/bin/bash
sudo bash ./killdocker.sh $1 $1 > /dev/null
sudo xterm -xrm 'XTerm.vt100.allowTitleOps: false' -T Clav$1 -e bash -c "docker run \
-e DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix \
--name Clav$1 \
-it clavardage " & if [ "$2" = "1" ]; then sudo xterm -e bash -c "sleep 2 ; docker inspect -f '{{ .NetworkSettings.IPAddress }}' Clav$1 & echo 'Press enter to exit' & read"; fi
