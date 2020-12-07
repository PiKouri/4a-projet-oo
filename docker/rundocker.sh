#!/bin/bash
sudo bash ./killdocker.sh $1 > /dev/null
sudo xterm -e bash -c "docker run --name $1 -it clavardage " & if [ "$2" = "1" ]; then sudo xterm -e bash -c "sleep 2 ; docker inspect -f '{{ .NetworkSettings.IPAddress }}' $1 & echo 'Press enter to exit' & read"; fi
