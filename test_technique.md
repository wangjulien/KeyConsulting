# Test Technique Full Stack

Le candidat devra réaliser une application comportant :
 
- Une API Restful permettant de gérer une liste de tâches à faire.
 
- Une application permettant d'intéragir avec l'API.

Le code produit devra être publié sur un repo github ou un équivalent.
 
## Partie 1 : l'API 
 
 A partir de cette API, on doit pouvoir :
- Récupérer la liste de toutes les tâches
- Récupérer les tâches à effectuer
- Récupérer une tâche par son ID
- Ajouter des tâches
- Changer le statut d'une tâche


Une tâche est représentée par les propriétés suivantes :
- id : l'identifiant de la tâche
- label : l'intitulé de la tâche
- description: une petite description de la tâche
- completed : indique si la tâche est effectuée ou non


> Il n'est pas nécessaire d'avoir une Base de données. On pourra charger une liste de données en mémoire.

### Stack techniques à utiliser :
- spring-boot
- maven
- Java ou Kotlin au choix

### Points d'attention :
- Tests unitaires
- Respect des bonnes pratiques Rest
- S\'il reste du temps ne pas hésiter à proposer de nouvelles fonctionnalités (documentation, pagination, sécurité...)
 

 ## Partie 2 : l'application

L'application devra donner la possibilité :
- D\'afficher la liste des tâches
- Afficher le détail d'une tâche
- Filtrer les tâches en fonction de leur statut
- Modifier le statut d'une tâche

 ### Stack techniques à utiliser :
- Angular
- Possibilité d'utiliser des librairies de composant ou de style type Material, taigaUi, tailwind ou autre.
- Possibilité d'utiliser une librairie de gestion d'état type NGRX ou autre.

### Points d'attention :
- L'ensemble des routes de l'api doivent être exploité dans l'application
- Tests unitaires
- Clean Code