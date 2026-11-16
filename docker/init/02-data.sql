-- Données initiales (>= 5 enregistrements par table)

-- Conducteurs
INSERT INTO conducteurs (id, nom, prenom, email, telephone, numero_permis) VALUES
(1, 'El Amrani', 'Hamid', 'hamid.elamrani@example.com', '0600000001', 'PERMIS-HAMID-001'),
(2, 'Bennani', 'Youssef', 'youssef.bennani@example.com', '0600000002', 'PERMIS-YOUSSEF-002'),
(3, 'Alaoui', 'Nadia', 'nadia.alaoui@example.com', '0600000003', 'PERMIS-NADIA-003'),
(4, 'Chakir', 'Omar', 'omar.chakir@example.com', '0600000004', 'PERMIS-OMAR-004'),
(5, 'Idrissi', 'Sara', 'sara.idrissi@example.com', '0600000005', 'PERMIS-SARA-005');

-- Passagers
INSERT INTO passagers (id, nom, prenom, email, telephone) VALUES
(1, 'Zahraoui', 'Salma', 'salma.zahraoui@example.com', '0700000001'),
(2, 'Kabbaj', 'Karim', 'karim.kabbaj@example.com', '0700000002'),
(3, 'Mansouri', 'Imane', 'imane.mansouri@example.com', '0700000003'),
(4, 'Fassi', 'Rachid', 'rachid.fassi@example.com', '0700000004'),
(5, 'Tazi', 'Lina', 'lina.tazi@example.com', '0700000005');

-- Véhicules
INSERT INTO vehicules (id, marque, modele, immatriculation, couleur, capacite, conducteur_id) VALUES
(1, 'Dacia', 'Logan', 'AA-111-AA', 'Gris', 4, 1),
(2, 'Renault', 'Clio', 'BB-222-BB', 'Blanc', 4, 2),
(3, 'Peugeot', '208', 'CC-333-CC', 'Noir', 4, 3),
(4, 'Toyota', 'Yaris', 'DD-444-DD', 'Bleu', 3, 4),
(5, 'Hyundai', 'i10', 'EE-555-EE', 'Rouge', 4, 5);

-- Trajets (date_depart = heure du premier point d'arrêt)
INSERT INTO trajets (id, date_depart, places_proposees, statut, conducteur_id, vehicule_id) VALUES
(1, '2026-12-01 17:00:00', 3, 'PUBLIE', 1, 1), -- trajet exemple Hamid
(2, '2026-12-02 08:00:00', 3, 'PUBLIE', 2, 2),
(3, '2026-12-03 09:30:00', 4, 'PUBLIE', 3, 3),
(4, '2026-12-04 14:00:00', 2, 'PUBLIE', 4, 4),
(5, '2026-12-05 07:15:00', 4, 'PUBLIE', 5, 5);

-- Points d'arrêt (prix strictement croissant, ordre unique, ville unique par trajet)
-- Trajet 1: Rabat -> Casablanca -> Settat -> Marrakech
INSERT INTO points_arret (id, ville, ordre, heure_passage, prix_depuis_depart, trajet_id) VALUES
(1, 'Rabat', 0, '2026-12-01 17:00:00', 0.00, 1),
(2, 'Casablanca', 1, '2026-12-01 18:15:00', 40.00, 1),
(3, 'Settat', 2, '2026-12-01 19:15:00', 70.00, 1),
(4, 'Marrakech', 3, '2026-12-01 21:30:00', 120.00, 1),

-- Trajet 2: Tanger -> Asilah -> Larache -> Kenitra
(5, 'Tanger', 0, '2026-12-02 08:00:00', 0.00, 2),
(6, 'Asilah', 1, '2026-12-02 08:45:00', 20.00, 2),
(7, 'Larache', 2, '2026-12-02 09:30:00', 35.00, 2),
(8, 'Kenitra', 3, '2026-12-02 11:00:00', 70.00, 2),

-- Trajet 3: Fes -> Meknes -> Khemisset -> Rabat
(9, 'Fes', 0, '2026-12-03 09:30:00', 0.00, 3),
(10, 'Meknes', 1, '2026-12-03 10:15:00', 25.00, 3),
(11, 'Khemisset', 2, '2026-12-03 11:30:00', 55.00, 3),
(12, 'Rabat', 3, '2026-12-03 12:30:00', 80.00, 3),

-- Trajet 4: Agadir -> Essaouira -> Safi -> El Jadida
(13, 'Agadir', 0, '2026-12-04 14:00:00', 0.00, 4),
(14, 'Essaouira', 1, '2026-12-04 16:30:00', 60.00, 4),
(15, 'Safi', 2, '2026-12-04 18:30:00', 100.00, 4),
(16, 'El Jadida', 3, '2026-12-04 20:30:00', 140.00, 4),

-- Trajet 5: Oujda -> Taourirt -> Guercif -> Taza
(17, 'Oujda', 0, '2026-12-05 07:15:00', 0.00, 5),
(18, 'Taourirt', 1, '2026-12-05 08:15:00', 30.00, 5),
(19, 'Guercif', 2, '2026-12-05 09:15:00', 55.00, 5),
(20, 'Taza', 3, '2026-12-05 10:30:00', 85.00, 5);

-- Réservations (exemples cohérents avec trajets/points)
INSERT INTO reservations (id, nombre_places, montant_total, date_reservation, statut, passager_id, trajet_id, point_montee_id, point_descente_id) VALUES
(1, 1, 50.00, '2026-11-20 10:00:00', 'CONFIRMEE', 1, 1, 3, 4), -- Salma: Settat -> Marrakech
(2, 1, 40.00, '2026-11-21 09:00:00', 'CONFIRMEE', 2, 2, 5, 6), -- Karim: Tanger -> Asilah
(3, 2, 110.00, '2026-11-22 11:30:00', 'CONFIRMEE', 3, 3, 10, 12), -- Imane: Meknes -> Rabat (2 places)
(4, 1, 80.00, '2026-11-23 15:00:00', 'CONFIRMEE', 4, 4, 14, 16), -- Rachid: Essaouira -> El Jadida
(5, 1, 55.00, '2026-11-24 08:20:00', 'ANNULEE', 5, 5, 17, 19); -- Lina: Oujda -> Guercif (annulée)

-- Synchroniser les séquences avec les IDs insérés
SELECT setval('conducteurs_id_seq', (SELECT MAX(id) FROM conducteurs));
SELECT setval('passagers_id_seq', (SELECT MAX(id) FROM passagers));
SELECT setval('vehicules_id_seq', (SELECT MAX(id) FROM vehicules));
SELECT setval('trajets_id_seq', (SELECT MAX(id) FROM trajets));
SELECT setval('points_arret_id_seq', (SELECT MAX(id) FROM points_arret));
SELECT setval('reservations_id_seq', (SELECT MAX(id) FROM reservations));
