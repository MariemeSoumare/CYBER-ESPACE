# Cyber Escape — Backend

API REST Spring Boot pour gérer les joueurs, les parties, les questions, les scores et le classement.

## Prérequis

- Java 17 ou supérieur
- Maven 3.6 ou supérieur
- MySQL 8 (base `cyber_escape`)

## Configuration MySQL

Le backend utilise les variables d'environnement suivantes :

| Variable | Valeur par défaut |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/cyber_escape?...` |
| `DB_USERNAME` | `root` |
| `DB_PASSWORD` | vide |
| `SHOW_SQL` | `true` |
| `SEED_DEMO_DATA` | `true` |

Définissez `DB_PASSWORD` avec le mot de passe MySQL de votre installation avant de lancer l'application. Ne mettez pas ce mot de passe dans le dépôt.

## Lancer le serveur

Depuis le dossier `backend` :

```powershell
$env:DB_PASSWORD = "votre_mot_de_passe"
mvn spring-boot:run
```

Le serveur écoute sur `http://localhost:8080`. Les origines React `http://localhost:3000` et `http://localhost:5173` sont autorisées.

Le backend est mappé sur les tables du fichier SQL de Cyber Escape (`player`, `level`, `question`, `answer_option`, `game_session`, `game_answer`) et utilise les questions déjà importées. Importe ce schéma avant le premier démarrage : `spring.jpa.hibernate.ddl-auto=none` empêche Spring de modifier la base. Les réponses correctes ne sont jamais renvoyées dans l'API de question. L'historique de chaque tentative est enregistré dans `game_answer`.

Les données de démonstration ne sont pas ajoutées par défaut. Pour les ajouter uniquement à une base vide, définissez `SEED_DEMO_DATA=true`.

## API

### Créer un joueur

`POST /api/players`

```json
{ "nickname": "Ami" }
```

### Démarrer une partie

`POST /api/game/start`

```json
{ "nickname": "Ami" }
```

Le joueur est créé automatiquement s'il n'existe pas encore. Le score commence à 0 et les vies à 3.

### Obtenir la question active

`GET /api/game/{id}/question`

La réponse contient le texte et les propositions, mais jamais l'indicateur de bonne réponse.

### Envoyer une réponse

`POST /api/game/{id}/answer`

Envoyez soit l'identifiant d'une proposition :

```json
{ "questionId": 1, "answerOptionId": 3 }
```

Soit son texte :

```json
{ "questionId": 1, "answer": "M@rie2026!X" }
```

Une bonne réponse ajoute 10 points. Une mauvaise réponse retire une vie et laisse la question active. Une partie se termine après trois erreurs ou lorsque tous les niveaux sont terminés.

### Consulter l'état d'une partie

`GET /api/game/{id}`

### Terminer une partie

`POST /api/game/{id}/finish`

Le schéma SQL fourni ne prévoit que les statuts `IN_PROGRESS`, `WON` et `LOST`. Une fin volontaire avant d'avoir terminé les niveaux est donc enregistrée comme `LOST`.

### Classement

`GET /api/leaderboard`

Le classement renvoie les dix meilleures parties terminées.

Les erreurs API utilisent le format `ProblemDetail` de Spring et renvoient un code HTTP explicite.
