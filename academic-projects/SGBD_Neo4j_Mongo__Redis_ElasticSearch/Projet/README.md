# Projet DON5 – Architecture & Bases de Données

## Objectif
Ce projet vise à concevoir un **système d’information simulant un réseau social orienté rencontres physiques (IRL)**.  
L’objectif principal n’est **pas l’interface**, mais la **qualité de l’architecture**, la **modélisation des données** et l’**utilisation pertinente de plusieurs systèmes de gestion de données**.

Les données sont simulées et l’interface est volontairement minimale afin de se concentrer sur les choix techniques et la logique métier.

---

## Technologies utilisées
- Java 17
- Maven
- Docker / Docker Compose
- MySQL 8.4 (base relationnelle)
- MongoDB 7 (base documentaire)
- Neo4j 5 (base graphe)
- Redis 7 (cache / sécurité)
- Elasticsearch 8 (moteur de recherche plein texte)

---

## Architecture générale
Le projet est structuré en **3 couches clairement séparées** :

- **HumanMeetingsApplication / Main (CLI)** : application Spring Boot exposant l’API REST et une interface de démonstration (web ou console)
- **Services** : logique métier
- **Repositories** : accès aux bases de données (un repository par technologie)

**Principe fondamental :**  
Les services n’accèdent jamais directement aux bases de données. Toute interaction passe par les repositories, ce qui garantit une architecture modulaire et évolutive.

---

## Répartition des données et justification des choix

### 1) MySQL – Données structurées et intégrité
**Rôle :** base de référence pour les données critiques.

- Table `users` : identifiant et nom d’utilisateur
- Table `meetings` : participants, thème, date
- Table `points` : points attribués par rencontre

**Pourquoi MySQL ?**
- contraintes fortes (UNIQUE, clés étrangères)
- cohérence référentielle
- agrégations fiables (calcul des points)
- adapté aux données strictement structurées

---

### 2) MongoDB – Profils utilisateurs flexibles
**Rôle :** stockage des profils utilisateurs et de leurs centres d’intérêt.

- Collection `user_profiles`
  - `userId` (référence logique vers MySQL)
  - `interests` (liste de chaînes)

**Pourquoi MongoDB ?**
- schéma flexible
- structure naturelle pour les listes
- évolution facile sans migration SQL
- adapté aux données semi-structurées

---

### 3) Neo4j – Réseau de rencontres
**Rôle :** représenter les relations sociales entre utilisateurs.

- Nœuds `User`
- Relations `MET` avec propriétés (`interest`, `timestamp`)

**Utilisation concrète :**
- création des relations lors d’une rencontre
- recommandations basées sur le principe *friend-of-a-friend*

**Pourquoi Neo4j ?**
- modélisation naturelle d’un réseau social
- requêtes de parcours efficaces
- logique relationnelle difficile à exprimer en SQL

---

### 4) Redis – Cache et sécurité
**Rôle :**
- cache temporaire des points utilisateurs
- limitation du nombre d’actions par utilisateur (rate limiting)

**Utilisation concrète :**
- cache des totaux de points avec TTL
- invalidation lors de l’ajout de points
- rate limiting pour éviter le spam d’actions coûteuses (ex : simulation de rencontres)

**Pourquoi Redis ?**
- stockage en mémoire très rapide
- réduction des requêtes SQL répétitives
- adapté aux données temporaires

---

### 5) Elasticsearch – Recherche plein texte
**Rôle :** moteur de recherche dédié.

**Utilisation concrète :**
- recherche fuzzy sur les utilisateurs (username + intérêts)
- recherche fuzzy sur les thèmes de meetings
- recommandations basées sur similarité d’intérêts

**Pourquoi Elasticsearch ?**
- recherche multi-champs
- fuzziness automatique
- performances adaptées à la recherche textuelle

---

## Cohérence et synchronisation des données
- **MySQL est la source de vérité** pour l’existence des utilisateurs.
- MongoDB, Redis et Elasticsearch stockent des données dérivées ou temporaires.
- Neo4j est alimenté **au moment des rencontres**, afin de représenter uniquement les relations sociales effectives.
- La cohérence est assurée au niveau des services, qui coordonnent les écritures entre les différents systèmes.
- Les centres d’intérêt sont stockés dans MongoDB et projetés dans Neo4j pour permettre des recommandations avancées.

---

## Fonctionnalités implémentées
- création d’utilisateurs avec validation
- gestion des centres d’intérêt
- simulation de rencontres entre utilisateurs
- attribution automatique de points de sociabilité
- consultation des points (avec cache Redis)
- recherche fuzzy d’utilisateurs par username ou interest (Elasticsearch)
- recherche fuzzy sur intérêts de meeting (Elasticsearch)
- recommandations basées sur :
  - relations sociales (Neo4j)
  - intérêts communs (Elasticsearch)
- recommandations avancées Phase 3 (amis des amis + intérêts)

---

## Phase 3 – Recommandations avancées (Neo4j)

### Principe
- nœuds `User`
- relations `MET`
- nœuds `Interest`
- relations `HAS_INTEREST`

### Algorithme
Recommande des utilisateurs :
- à **distance 2** (amis des amis)
- non connectés directement
- partageant un ou plusieurs centres d’intérêt  
  Classement par nombre d’intérêts communs.

### Endpoint
`GET /api/recommendations/graph/advanced/{userId}`

---

## Démonstration (CLI)
Scénario type :
1. Créer un utilisateur
2. Lister les utilisateurs
3. Ajouter des intérêts à un utilisateur
4. Lister les intérêts existants
5. Simuler une rencontre
6. Voir les rencontres d’un utilisateur
7. Afficher les points d’un utilisateur
8. Rechercher des utilisateurs (Elasticsearch, fuzzy)
9. Rechercher des intérêts de meeting (Elasticsearch, fuzzy)
10. Recos basées graphe social
11. Recos basées intérêts
0. Quitter

---

## Lancer le projet

### 1) Démarrer les bases (Docker)
Assure-toi que **Docker Desktop** est lancé, puis :

```bash
cd docker
docker compose up -d
```


Services exposés :
- MySQL : `localhost:3306`
- MongoDB : `localhost:27017`
- Neo4j : `localhost:7474` / `localhost:7687`
- Redis : `localhost:6379`
- Elasticsearch : `localhost:9200`

---

### 2) Lancer l’application
Depuis la racine du projet :

```bash
mvn clean spring-boot:run
```

## Arrêter le projet
```bash
cd docker
docker compose down
```