# 4IR Projet COO/POO
## 4IR-GRP A1
## Trinôme
  * BENAZZOUZ Abir
  * BONNEAU Clémentine
  * HOK Jean-Rémy  
## Sommaire
1. [Présentation](#presentation)
2. [Démonstrations (vidéos)](#demonstration)
3. [Anciens diagrammes](#oldDiagrams)
    1. [Diagramme des cas d'utilisation](#oldUseCase)
    2. [Diagrammes de séquence](#oldSequence)
    3. [Diagramme de classe](#oldClass)
4. [Nouveaux diagrammes](#newDiagrams)
    1. [Diagramme de classe](#newClass)
        1. [Légende des icônes pour les diagrammes de classe](#objectAIDLegend)
        2. [Résumé](#newClassSummary)
        3. [Détails](#newClassDetails)
    2. [Diagramme de structure composite](#newComposite)
    3. [Diagramme de machines à états](#newState)
## Presentation <a name="presentation"></a>
Partie POO : <br>
  On peut exporter le projet vers un fichier Runnable Jar en utilisant la classe Interface pour la configuration de lancement<br>
  Voir [ici](Runnable/) pour lancer l'application soit en réél (Clavardage.jar) soit en simulation (Docker) <br><br>
  L'application Clavardage.jar est fonctionnelle :  <br>
  * En serveur local :
      - [X] Connexion, Déconnexion, Changement de pseudo
      - [X] Affichage des utilisateurs connectés / déconnectés
      - [X] Affichage de l'historique des messages (stockage dans un fichier local database.db)
      - [X] Envoi de message : Text, Image, Fichier
      - [X] Configuration de l'application avec le fichier config.properties
      - [X] Journal de log sauvegardé dans le fichier log.log
  * Avec le serveur de présence :
      - [X] Connexion, Déconnexion, Changement de pseudo
      - [X] Affichage des utilisateurs connectés / déconnectés
      - [ ] Affichage de l'historique des messages (stockage dans un fichier local database.db)
      - [ ] Envoi de message : Text, Image, Fichier
      - [X] Configuration de l'application avec le fichier config.properties
      - [X] Journal de log sauvegardé dans le fichier log.log
      - [X] Servlet : Journal de log et autres informations disponibles en accédant au serveur tomcat via web (GET request)
      - A noter : l'affichage de l'historique et l'envoi de message est disponible mais seulement sur le serveur local, envoi vers un utilisateur externe impossible
      <br>
Partie COO : Diagrammes à refaire
- [x] Diagramme des cas d'utilisation
- [X] Diagrammes de séquence
- [X] Diagramme de classe
- [ ] Diagramme de structure composite]
- [ ] Diagramme de machines à états
## Démonstrations (vidéos) <a name="demonstration"></a>
Voici trois vidéos de démonstration de l’utilisation de notre application.
  * Premier cas, utilisation dans un réseau local : https://youtu.be/Ojc-RVkksYk
  * Second cas, utilisation du serveur de présence : https://youtu.be/KS4yV88tEgw

Schémas réseau correspondant <br>
![Schéma réseau](https://github.com/PiKouri/4a-projet-oo/blob/main/img/image_2021-04-08_134626.png)<br>
RT = routeur, mais le routage est direct : câble réseau virtuel entre la machine virtuelle VM et l’hôte.
  * Autres fonctionnalités : https://youtu.be/YL51-GbzIHA
## Anciens Diagrammes <a name="oldDiagrams"></a>
### Diagramme des cas d'utilisation <a name="oldUseCase"></a>
![Ancien Diagramme des cas d'utilisation](https://github.com/PiKouri/4a-projet-oo/blob/main/img/UseCase%20Diagram.png)
### Diagrammes de séquence <a name="oldSequence"></a>
  * chooseUsername() 
  ![Ancien Diagramme de séquence chooseUsername()](https://github.com/PiKouri/4a-projet-oo/blob/main/img/chooseUsername.png)
  * getMessageHistory()
  ![Ancien Diagramme de séquence getMessageHistory()](https://github.com/PiKouri/4a-projet-oo/blob/main/img/getMessageHistory.png)
  * receiveMessage()
  ![Ancien Diagramme de séquence receiveMessage()](https://github.com/PiKouri/4a-projet-oo/blob/main/img/receiveMessage.png)
  * sendMessage()
  ![Ancien Diagramme de séquence sendMessage()](https://github.com/PiKouri/4a-projet-oo/blob/main/img/sendMessage.png)
  * viewActiveUsernames()
  ![Ancien Diagramme de séquence viewActiveUsernames()](https://github.com/PiKouri/4a-projet-oo/blob/main/img/viewActiveUsernames.png)
### Diagrammes de classe <a name="oldClass"></a>
![Ancien Diagramme de classe](https://github.com/PiKouri/4a-projet-oo/blob/main/img/ClassDiagram%20v2.png)
## Nouveaux Diagrammes <a name="newDiagrams"></a>
### Diagrammes de classe <a name="newClass"></a>
### Légende des icônes pour les diagrammes de classes suivants <a name ="objectAIDLegend"></a>
![Légende ObjectAID UML](https://github.com/PiKouri/4a-projet-oo/blob/main/img/Nouveaux%20Diagrammes/Legend%20ObjectAID%20UML.png)
#### Résumé <a name="newClassSummary"></a>
![Résumé Diagramme de Classe](https://github.com/PiKouri/4a-projet-oo/blob/main/img/Nouveaux%20Diagrammes/Class/Résumé%20Diagramme%20de%20Classe.png)
#### Détails <a name="newClassDetails"></a>
  * Package agent <br>
![agent Diagramme de Classe](https://github.com/PiKouri/4a-projet-oo/blob/main/img/Nouveaux%20Diagrammes/Class/agent%20Class%20Diagram.png)
  * Package userInterface <br>
![userInterface Diagramme de Classe](https://github.com/PiKouri/4a-projet-oo/blob/main/img/Nouveaux%20Diagrammes/Class/userInterface%20Class%20Diagram.png)
  * Package datatypes <br>
![datatypes Diagramme de Classe](https://github.com/PiKouri/4a-projet-oo/blob/main/img/Nouveaux%20Diagrammes/Class/datatypes%20Class%20Diagram.png)
  * Servlet (Serveur de présence) <br>
![servlet Diagramme de Classe](https://github.com/PiKouri/4a-projet-oo/blob/main/img/Nouveaux%20Diagrammes/Class/servlet%20Class%20Diagram.png)
### Diagramme de structure composite <a name="newComposite"></a>
### Diagramme de machines à états <a name="newState"></a>
