# Nom du Projet

TypoRacer

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white) ![SQLite](https://img.shields.io/badge/SQLite-003B57?style=flat&logo=sqlite&logoColor=white) ![Markdown](https://img.shields.io/badge/Markdown-000000?style=flat&logo=markdown&logoColor=white)    

## Auteurs

- Groupe D112
- g60733 Devran
- g62496 Abdelrahman

## Description du Projet
Ce projet est une application Java permettant de gérer un jeu de rapidité d'écriture. L'objectif est de taper un texte donné le plus rapidement possible. Plusieurs modes de jeu seront disponibles.

Fonctionnalités principales :
Saisie chronométrée : L'utilisateur tape un texte aléatoire et reçoit un score basé sur sa vitesse et son taux de réussite en temps réel.

Reconnaissance OCR : Possibilité d'insérer un texte extrait d'une image grâce à l'OCR, puis de le taper aussi vite que possible. L'utilisateur pourra ajouter ce texte à la base de données ou choisir parmis des textes existant.

Modes de jeu variés : Entraînement, classique, mort subite et devinette. La difficulté sera ajustable en fonction du temps imparti.

Suivi des performances : Statistiques détaillées sur la vitesse et la précision de frappe, ainsi qu'un classement global des meilleurs scores et joueurs par mode de jeu.

Un fichier trop court ne pourra pas être saisi.

## Diagramme de Classe

Le diagramme de classe ci-dessous illustre la structure du modèle de l'application. 

![Diagramme de classe](TypoRacer/src/main/resources/images/TyporacerUML.png)

## Choix de l'Architecture

L'architecture retenue pour ce projet est _model-view-controller_. 


## Plan de Tests Fonctionnels

Les tests fonctionnels élémentaires pour le projet sont les suivants :

T1 – Inscription

Entrée : Identifiants valides
Résultat attendu : Redirection vers le menu principal

T2 – Connexion

Entrée : Identifiants incorrects
Résultat attendu : Affichage d'un message d'erreur

T3 – Ajout de texte

Entrée : Texte contenant moins de 100 caractères
Résultat attendu : Message d'erreur de validation

T4 – Ajout OCR avec image invalide

Entrée : Fichier non image (ex : .txt, .exe)
Résultat attendu : Exception ou message d'erreur

T5 – Affichage du classement

Entrée : Texte, mode et difficulté sélectionnés
Résultat attendu : Affichage du top 20 et mise en évidence du score utilisateur

T6 – Frappe correcte dans le jeu

Entrée : Lettre correcte saisie
Résultat attendu : Progression, score et combo mis à jour

T7 – Frappe incorrecte

Entrée : Lettre incorrecte
Résultat attendu : Combo réinitialisé, score inchangé

T8 – Retour en arrière

Entrée : Suppression d'une lettre correcte (Backspace)
Résultat attendu : Réduction de score ou annulation de combo selon les règles du jeu

T9 – Mode Mort Subite

Entrée : Lettre incorrecte saisie
Résultat attendu : Fin immédiate de la partie

T10 – Mode Devinette

Entrée : Texte avec accents saisi correctement
Résultat attendu : Progression correcte même si le texte affiché est sans accents

T11 – Fin de partie par complétion

Entrée : Texte entièrement saisi correctement
Résultat attendu : Affichage du score final et option de recommencer

T12 – Fin de partie par temps écoulé

Entrée : Attendre que le temps soit écoulé
Résultat attendu : Interruption de la partie et affichage du score

T13 – Difficulté HARDCORE

Entrée : Démarrer une partie en difficulté HARDCORE
Résultat attendu : Temps limité à 30 secondes

T14 – Difficulté EASY

Entrée : Démarrer une partie en difficulté EASY
Résultat attendu : Temps limité à 90 secondes

T15 – Navigation entre menus

Entrée : Cliquer sur les différentes options du menu
Résultat attendu : Chargement correct des vues correspondantes

T16 – Persistance des choix

Entrée : Sélectionner un mode et une difficulté, puis naviguer vers une autre vue et revenir
Résultat attendu : Les choix précédents sont conservés

T17 – OCR avec image valide

Entrée : Image contenant du texte clair
Résultat attendu : Texte extrait correctement

T18 – Sauvegarde du texte OCR

Entrée : Texte extrait par OCR, titre unique
Résultat attendu : Texte sauvegardé dans la base de données

Les tests sont situés dans `src/test/java`.


## Calendrier Hebdomadaire des Tâches
Qui		Description

## Semaine 1 - 6H

Tous: Analyse du projet, définition des fonctionnalités, 

Abdelrahman: Initialisation de Git et mise en place du dépôt
Devran: Configuration de l’environnement de développement (JDK, dépendances, IDE, etc.)

## Semaine 2 - 6H
Tous :   Diagramme de classe UML et choix de l’architecture,Test fonctionnel,Vue FXML

Abdelrahman : Calendrier
Devran :  Description du projet

## Semaine 3 -6h 

Abdelrahman : Mise en place de la base de données (choix du SGBD, création des tables)
Devran : Implémentation de la partie OCR (Tesseract, conversion image → texte)

## Semaine 4 -6h 

Abdelrahman : Implementation du choix de texte et de l'interface fxml 
Devran :Réajustement de ConnectionManager,ajout des classement repository et dao

## Semaine 5 -6h 

Abdelrahman : Implémentation du mode de jeu classic et gestion de l'input 
Devran : Implémentation du jeu de base

## Semaine 6 -6h 

Abdelrahman : ajout du mode de jeu mort subite
Devran : Ajout de la fonctionnalité Ocr de fichier pour envoyer dans la database

## Semaine 7 -6h 

Abdelrahman : Ajout du classement
Devran :Ajout du mode Devinette dans le jeu

## Semaine 8 -6h 

Abdelrahman : Implémentation des test et fxml css 
Devran : Ajout de vérification dans la vue UploadFile, implémentation des tests



## Installation et lancement

1. **Cloner le dépôt**

   ```bash
   git clone https://git.esi-bru.be/62496/4prj1d-d112.git
   cd 4prj1d-d112/TypoRacer

2. **Exécuter**

    mvn clean install
    mvn javafx:run



## Problèmes connus de l'application

1. Problèmes de compatibilité Mac/Windows
Contexte : Le projet a été développé sur différents systèmes d’exploitation (macOS & Windows), ce qui a engendré :

des erreurs de chemins (base de données SQLite, fichiers OCR tessdata)

des difficultés liées aux dépendances natives (Tesseract, JavaFX)

Solutions envisagées :

Ajout de profils Maven distincts (macos / windows) pour gérer les dépendances spécifiques

Utilisation de chemins relatifs

2. Mise à jour en temps réel du score
Bug : Le score, le combo ou la précision ne s’actualisent pas toujours lors de la frappe.

Cause probable : Rafraîchissement manuel insuffisant ou problème d’abonnement à la logique de jeu.

Solution envisagée :

Vérifier que les vues sont bien synchronisées avec le modèle (Game) via les contrôleurs

Revoir les appels à Platform.runLater() si l’UI ne suit pas le modèle

3. Chargement incomplet des vues
Bug : Lors du passage d’une vue à une autre (via MainController), certaines parties de l’ancienne vue peuvent rester visibles.

Cause probable : Mauvaise gestion du StackPane, avec des vues non correctement remplacées.

Solution envisagée : Vérifier que contentArea.getChildren().setAll(loadFXML("nouvelleVue")) remplace bien toute la zone centrale.

4. Retour à la page précédente non implémenté
Observation : L’utilisateur ne peut pas revenir en arrière (ex. : de la vue "Classement" vers le menu).

Cause : Pas de bouton "Retour" global ni d’historique de navigation.

Amélioration possible :

Implémenter une pile de navigation ou un bouton dédié dans chaque vue secondaire

## Rétrospective

Au cours du développement, plusieurs écarts se sont créés entre le diagramme initial et l’implémentation finale :

- **Mode entraînement** : Initialement prévu pour permettre des sessions sans score enregistré. Supprimé car non nécessaire au vu du mode classic.

- **Pourcentage de lettres bien tapées en série** : Cette fonctionnalité semblait utile pour visualiser la régularité, mais plus utile car systeme de jeu changer pour eviter les erreur et s'orienter entrainement.

- **Pattern Observer/Observable** : Prévu au départ, mais écarté au profit des liaisons naturelles offertes par JavaFX et les controllers FXML.

- **Architecture MVC** bien respectée : avec une séparation claire entre les `models` (`Game`, `Rules`, `GameConfig`), les `views` (FXML) et les `controllers`.

- **Modularité** : Le découpage en DAO / Repository a facilité l’implémentation du mode `OCR` et des classements.

Malgré quelques changement, le projet correspond bien à nos attentes initiales. La logique du jeu est fonctionnelle, le design est propre et la base est suffisamment robuste pour de futures évolutions.

# Bug Connu 
- **CONNECTE** : Bug quand on creer un compte avec id existant 

- Bien que l'application sauvegarde correctement les configurations de partie,
 l'interface n'affiche pas les choix précédemment sélectionnés lorsque l'utilisateur
 quitte l'écran de configuration et y revient ultérieurement

-Dans l'écran de connexion, le champ ID est strictement limité aux valeurs numériques, tandis que les champs Nom et Mot de passe n'acceptent que des chaînes de caractères, restreignant ainsi les possibilités de saisie de l'utilisateur.
