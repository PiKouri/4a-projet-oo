docker inspect -f '{{ .NetworkSettings.IPAddress }}' $1
