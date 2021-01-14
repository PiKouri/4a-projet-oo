# Général
- Modifier le fichier config.properties pour modifier les propriétés de l'agent
- Pour nettoyer l'historique de messages et des utilisateurs : supprimer database.db
- Ne pas oublier de commenter les cp pour le docker
- Note : l'application créer un fichier log.log : journal système
- Note : l'application créer un dossier ./image et un dossier ./file dans lesquels sont répértoriés les fichiers et images reçu par les utilisateurs (par addresse IP)

# Docker
## Installation
- Installer docker et xterm
- Installer openssh-server : "sudo apt-get install openssh-server"
- Modifier les fichiers de configuration :
    - /etc/ssh/ssh_config : décommenter et modifier les lignes : 
        * ForwardAgent yes
        * ForwardX11 yes
        * ForwardX11Trusted yes
    - /etc/ssh/sshd_config : décommenter et modifier les lignes : 
        * X11Forwarding yes
        * X11UseLocalhost yes
- Redémarrer la machine
## Lancement de l'application
- Exécuter ./preparerdocker.sh && ./multirundocker nombre_de_docker_voulu
