# Covoiturage multi-étapes

Backend REST API (Spring Boot 3.5.7 / Java 17 / Maven) pour une plateforme de
covoiturage avec trajets composés de plusieurs points d’arrêt.

## Base de données

PostgreSQL est installé en local et géré depuis pgAdmin. La base s’appelle
`covoiturage_db` (`postgres` / `0000`, port 5432). L’application s’y connecte
par défaut :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/covoiturage_db
spring.datasource.username=postgres
spring.datasource.password=0000
spring.jpa.hibernate.ddl-auto=update
```

Quand PostgreSQL est utilisé (profil `postgres`), au démarrage Hibernate crée
les tables manquantes et `data.sql` insère les données de test (5 lignes minimum
par table). Pour tout recréer depuis zéro, exécuter `docs/schema.sql` dans
pgAdmin puis lancer :

```powershell
$env:SPRING_PROFILES_ACTIVE = "postgres"
.\mvnw.cmd spring-boot:run
```

## Swagger UI sans base de données

Le profil `demo` est activé **par défaut** dans `application.properties` : il
utilise une base H2 en mémoire. Aucune installation de PostgreSQL n'est donc
nécessaire pour lancer l'application depuis IntelliJ ou en ligne de commande.

```powershell
.\mvnw.cmd spring-boot:run
```

puis ouvrir http://localhost:8080/swagger-ui/index.html
(JSON : http://localhost:8080/v3/api-docs).

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

> L’API écoute sur le port **8080**. Swagger UI est disponible sur
> `http://localhost:8080/swagger-ui/index.html` (JSON : `/v3/api-docs`).

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
