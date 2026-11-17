-- Schéma de base de données - Plateforme de covoiturage multi-étapes
-- PostgreSQL

DROP TABLE IF EXISTS reservations CASCADE;
DROP TABLE IF EXISTS points_arret CASCADE;
DROP TABLE IF EXISTS trajets CASCADE;
DROP TABLE IF EXISTS vehicules CASCADE;
DROP TABLE IF EXISTS passagers CASCADE;
DROP TABLE IF EXISTS conducteurs CASCADE;

CREATE TABLE conducteurs (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telephone VARCHAR(30) NOT NULL,
    numero_permis VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE passagers (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telephone VARCHAR(30) NOT NULL
);

CREATE TABLE vehicules (
    id BIGSERIAL PRIMARY KEY,
    marque VARCHAR(100) NOT NULL,
    modele VARCHAR(100) NOT NULL,
    immatriculation VARCHAR(50) NOT NULL UNIQUE,
    couleur VARCHAR(50) NOT NULL,
    capacite INTEGER NOT NULL CHECK (capacite BETWEEN 1 AND 8),
    conducteur_id BIGINT NOT NULL,
    CONSTRAINT fk_vehicule_conducteur FOREIGN KEY (conducteur_id)
        REFERENCES conducteurs(id)
);

CREATE TABLE trajets (
    id BIGSERIAL PRIMARY KEY,
    ville_depart VARCHAR(120) NOT NULL,
    ville_arrivee VARCHAR(120) NOT NULL,
    date_depart TIMESTAMP NOT NULL,
    places_proposees INTEGER NOT NULL CHECK (places_proposees >= 1),
    statut VARCHAR(20) NOT NULL CHECK (statut IN ('PUBLIE', 'COMPLET', 'TERMINE', 'ANNULE')),
    conducteur_id BIGINT NOT NULL,
    vehicule_id BIGINT NOT NULL,
    CONSTRAINT fk_trajet_conducteur FOREIGN KEY (conducteur_id)
        REFERENCES conducteurs(id),
    CONSTRAINT fk_trajet_vehicule FOREIGN KEY (vehicule_id)
        REFERENCES vehicules(id)
);

CREATE TABLE points_arret (
    id BIGSERIAL PRIMARY KEY,
    ville VARCHAR(120) NOT NULL,
    ordre INTEGER NOT NULL CHECK (ordre >= 0),
    heure_passage TIMESTAMP NOT NULL,
    prix_depuis_depart NUMERIC(10, 2) NOT NULL CHECK (prix_depuis_depart >= 0),
    trajet_id BIGINT NOT NULL,
    CONSTRAINT fk_point_arret_trajet FOREIGN KEY (trajet_id)
        REFERENCES trajets(id) ON DELETE CASCADE,
    CONSTRAINT uk_trajet_ordre UNIQUE (trajet_id, ordre),
    CONSTRAINT uk_trajet_ville UNIQUE (trajet_id, ville)
);

CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    nombre_places INTEGER NOT NULL CHECK (nombre_places >= 1),
    montant_total NUMERIC(10, 2) NOT NULL CHECK (montant_total >= 0),
    date_reservation TIMESTAMP NOT NULL,
    statut VARCHAR(20) NOT NULL CHECK (statut IN ('CONFIRMEE', 'ANNULEE')),
    passager_id BIGINT NOT NULL,
    trajet_id BIGINT NOT NULL,
    point_montee_id BIGINT NOT NULL,
    point_descente_id BIGINT NOT NULL,
    CONSTRAINT fk_reservation_passager FOREIGN KEY (passager_id)
        REFERENCES passagers(id),
    CONSTRAINT fk_reservation_trajet FOREIGN KEY (trajet_id)
        REFERENCES trajets(id),
    CONSTRAINT fk_reservation_point_montee FOREIGN KEY (point_montee_id)
        REFERENCES points_arret(id),
    CONSTRAINT fk_reservation_point_descente FOREIGN KEY (point_descente_id)
        REFERENCES points_arret(id),
    CONSTRAINT ck_reservation_points_differents CHECK (point_montee_id <> point_descente_id)
);

CREATE INDEX idx_vehicules_conducteur_id ON vehicules(conducteur_id);
CREATE INDEX idx_trajets_conducteur_id ON trajets(conducteur_id);
CREATE INDEX idx_trajets_vehicule_id ON trajets(vehicule_id);
CREATE INDEX idx_trajets_statut ON trajets(statut);
CREATE INDEX idx_trajets_date_depart ON trajets(date_depart);
CREATE INDEX idx_points_arret_trajet_id ON points_arret(trajet_id);
CREATE INDEX idx_points_arret_ville ON points_arret(ville);
CREATE INDEX idx_reservations_passager_id ON reservations(passager_id);
CREATE INDEX idx_reservations_trajet_id ON reservations(trajet_id);
CREATE INDEX idx_reservations_statut ON reservations(statut);
