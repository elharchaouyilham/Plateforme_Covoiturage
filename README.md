# Covoiturage multi-étapes

Backend REST API (Spring Boot 3.5.7 / Java 17 / Maven) pour une plateforme de
covoiturage avec trajets composés de plusieurs points d’arrêt.

## Base de données

### Option A — PostgreSQL déjà présent sur le port 5432 (cas actuel)

Une base `covoiturage_db` existe déjà dans le conteneur `postgres-db`
(`postgres` / `0000`, port 5432). L’application s’y connecte par défaut :

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/covoiturage_db}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:0000}
spring.jpa.hibernate.ddl-auto=update
```

Pour **recréer toutes les tables + les données de test** dans pgAdmin :

```powershell
Get-Content docs/schema.sql -Raw | docker exec -i postgres-db psql -U postgres -d covoiturage_db
.\mvnw.cmd spring-boot:run
```

Le script `docs/schema.sql` supprime puis recrée les 6 tables ; `data.sql`
(chargé au démarrage de l’application) insère ensuite 5 lignes minimum par table.

### Option B — PostgreSQL du projet via Docker Compose

Le port hôte `5433` est utilisé pour éviter le conflit avec le PostgreSQL déjà
présent sur `5432` :

```bash
docker compose up -d
DB_URL=jdbc:postgresql://localhost:5433/covoiturage_db .\mvnw.cmd spring-boot:run
```

## Tables créées

| Table | Description |
|---|---|
| `conducteurs` | id, nom, prénom, email (unique), téléphone, numéro de permis (unique) |
| `passagers` | id, nom, prénom, email (unique), téléphone |
| `vehicules` | id, marque, modèle, immatriculation (unique), couleur, capacité, conducteur_id |
| `trajets` | id, ville_depart, ville_arrivee, date_depart, places_proposees, statut, conducteur_id, vehicule_id |
| `points_arret` | id, ville, ordre, heure_passage, prix_depuis_depart, trajet_id |
| `reservations` | id, nombre_places, montant_total, date_reservation, statut, passager_id, trajet_id, point_montee_id, point_descente_id |

### Et la table `utilisateurs` ?

**Il n’y a volontairement aucune table `utilisateurs`.** `Utilisateur` est une
classe abstraite annotée `@MappedSuperclass` : ses champs communs (id, nom,
prénom, email, téléphone) sont copiés dans les tables `conducteurs` et
`passagers`. Cela évite une jointure inutile et garantit un seul propriétaire
par profil.

> L’API écoute sur le port **8081** (`SERVER_PORT` surchargeable) pour éviter le
> conflit avec Jenkins qui occupe déjà le port 8080 sur cette machine.

## Vérification rapide dans pgAdmin

```sql
SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';
SELECT 'conducteurs' AS table, COUNT(*) FROM conducteurs
UNION ALL SELECT 'passagers', COUNT(*) FROM passagers
UNION ALL SELECT 'vehicules', COUNT(*) FROM vehicules
UNION ALL SELECT 'trajets', COUNT(*) FROM trajets
UNION ALL SELECT 'points_arret', COUNT(*) FROM points_arret
UNION ALL SELECT 'reservations', COUNT(*) FROM reservations;
```
