cp /media/sf_Transfert/Clavardage.jar Clavardage.jar
cp /media/sf_Transfert/config.properties config.properties
#cp /media/sf_Transfert/UDPServer.jar UDPServer.jar
#cp /media/sf_Transfert/TestEmission.jar TestEmission.jar
cp /media/sf_Transfert/ChatMignon.jpg ChatMignon.jpg
cp /media/sf_Transfert/Pangolin.jpg Pangolin.jpg

sudo docker build -t clavardage .
