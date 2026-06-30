-- ============================================================
-- INJECTION DE DONNÉES DE TEST — HolyFlame (etab_id = 5)
-- ============================================================
SET @etab = 5;
SET FOREIGN_KEY_CHECKS = 0;

-- ── 1. NOUVELLES CLASSES (2 lignes) ──────────────────────────
INSERT INTO classes (nom, niveau, annee_scolaire, etablissement_id) VALUES
('3ème A',      '3ème',      '2025-2026', @etab),
('Terminale S', 'Terminale', '2025-2026', @etab);
-- IDs attendus : 7, 8

-- ── 2. PERSONNELS : enseignants + staff (6 lignes) ───────────
INSERT INTO personnels (nom, prenom, fonction, matiere_enseignee, statut, telephone, email, date_embauche, etablissement_id) VALUES
('KOKOU',    'Etienne',   'ENSEIGNANT',  'Mathématiques', 'ACTIF', '0700100001', 'e.kokou@holyflame.edu',    '2022-09-01', @etab),
('BALOUA',   'Sylviane',  'ENSEIGNANT',  'Français',      'ACTIF', '0700100002', 's.baloua@holyflame.edu',   '2021-09-01', @etab),
('NADJI',    'Hermas',    'ENSEIGNANT',  'Sciences',      'ACTIF', '0700100003', 'h.nadji@holyflame.edu',    '2023-01-15', @etab),
('MOUSSA',   'Ali',       'ENSEIGNANT',  'Histoire-Géo',  'ACTIF', '0700100004', 'a.moussa@holyflame.edu',   '2022-09-01', @etab),
('GBEADJI',  'Florence',  'SECRETAIRE', NULL,             'ACTIF', '0700100005', 'f.gbeadji@holyflame.edu',  '2020-03-01', @etab),
('DEMBA',    'Oumar',     'SURVEILLANT', NULL,            'ACTIF', '0700100006', 'o.demba@holyflame.edu',    '2024-09-01', @etab);

-- ── 3. ÉLÈVES : 20 nouveaux (20 lignes) ──────────────────────
-- Classe 1 (6ème A) → 5 élèves ; IDs attendus 8-12
INSERT INTO eleves (nom, prenom, matricule, date_naissance, telephone_parent, email_parent, statut_inscription, adresse, classe_id, etablissement_id) VALUES
('MBAYE',     'Abdoulaye', 'HF-2026-008', '2014-03-15', '0600111001', 'mbaye.parent@gmail.com',    'INSCRIT', 'Quartier Nord',        1, @etab),
('OUATTARA',  'Djeneba',   'HF-2026-009', '2014-07-22', '0600111002', 'ouattara.p@gmail.com',      'INSCRIT', 'Quartier Sud',         1, @etab),
('SAWADOGO',  'Bakary',    'HF-2026-010', '2014-01-10', '0600111003', NULL,                        'INSCRIT', 'Centre-ville',         1, @etab),
('TOURE',     'Aissatou',  'HF-2026-011', '2014-11-05', '0600111004', 'toure.parent@gmail.com',    'INSCRIT', 'Rue des Fleurs 12',    1, @etab),
('KABORE',    'Seydou',    'HF-2026-012', '2013-05-30', '0600111005', NULL,                        'INSCRIT', 'Quartier Est',         1, @etab),
-- Classe 2 (5ème A) → 4 élèves ; IDs 13-16
('SANOGO',    'Mariam',    'HF-2026-013', '2012-08-18', '0600111006', 'sanogo.m@gmail.com',        'INSCRIT', 'Rue de la Paix 5',     2, @etab),
('DEMBELE',   'Boubacar',  'HF-2026-014', '2012-02-27', '0600111007', NULL,                        'INSCRIT', 'Quartier Ouest',       2, @etab),
('KOUYATE',   'Fanta',     'HF-2026-015', '2013-06-14', '0600111008', 'kouyate.f@gmail.com',      'INSCRIT', 'Avenue Centrale 8',    2, @etab),
('DIARRA',    'Kadiatou',  'HF-2026-016', '2012-12-20', '0600111009', NULL,                        'INSCRIT', 'Rue du Marché',        2, @etab),
-- Classe 5 (2nde A) → 4 élèves ; IDs 17-20
('CISSE',     'Mamadou',   'HF-2026-017', '2010-04-08', '0600111010', 'cisse.m@gmail.com',         'INSCRIT', 'Quartier Résidentiel', 5, @etab),
('SIDIBE',    'Fatoumata', 'HF-2026-018', '2011-10-17', '0600111011', 'sidibe.f@gmail.com',        'INSCRIT', 'Impasse des Manguiers',5, @etab),
('CAMARA',    'Alpha',     'HF-2026-019', '2010-07-01', '0600111012', NULL,                        'INSCRIT', 'Boulevard Principal',  5, @etab),
('KEITA',     'Aminata',   'HF-2026-020', '2011-03-25', '0600111013', 'keita.a@gmail.com',         'INSCRIT', 'Rue des Jardins 3',    5, @etab),
-- Classe 6 (6ème C) → 3 élèves ; IDs 21-23
('DOUMBIA',   'Yusuf',     'HF-2026-021', '2014-01-14', '0600111014', NULL,                        'INSCRIT', 'Quartier Populaire',   6, @etab),
('BALDE',     'Hawa',      'HF-2026-022', '2014-05-22', '0600111015', 'balde.h@gmail.com',         'INSCRIT', 'Rue du Stade 7',       6, @etab),
('FOFANA',    'Ibrahim',   'HF-2026-023', '2013-11-30', '0600111016', NULL,                        'INSCRIT', 'Cité Mixte',           6, @etab),
-- Classe 7 (3ème A) → 2 élèves ; IDs 24-25
('KONATE',    'Oumou',     'HF-2026-024', '2010-08-06', '0600111017', 'konate.o@gmail.com',        'INSCRIT', 'Avenue de la Gare',    7, @etab),
('SYLLA',     'Lansana',   'HF-2026-025', '2010-03-19', '0600111018', NULL,                        'INSCRIT', 'Quartier Admin',       7, @etab),
-- Classe 8 (Terminale S) → 2 élèves ; IDs 26-27
('BARRY',     'Mariama',   'HF-2026-026', '2008-09-12', '0600111019', 'barry.m@gmail.com',         'INSCRIT', 'Résidence Centrale',   8, @etab),
('DIALLO',    'Thierno',   'HF-2026-027', '2009-02-04', '0600111020', NULL,                        'INSCRIT', 'Quartier Universitaire',8, @etab);

-- ── 4. NOTES : 25 lignes réparties sur T1 et T2 ─────────────
-- Élèves 8-12 (6ème A) — Trimestre 1
INSERT INTO notes (eleve_id, matiere_id, valeur, coefficient, trimestre, type, date_evaluation, commentaire) VALUES
(8,  1, 14.5, 4, 1, 'DEVOIR',   '2025-10-10', 'Bon travail'),
(8,  2, 12.0, 4, 1, 'DEVOIR',   '2025-10-12', NULL),
(9,  1, 16.5, 4, 1, 'DEVOIR',   '2025-10-10', 'Excellent'),
(9,  3, 11.5, 3, 1, 'DEVOIR',   '2025-10-15', NULL),
(10, 1,  9.0, 4, 1, 'DEVOIR',   '2025-10-10', 'Peut mieux faire'),
(10, 2, 13.5, 4, 1, 'DEVOIR',   '2025-10-12', NULL),
(11, 2, 15.0, 4, 1, 'DEVOIR',   '2025-10-12', 'Très bien'),
(11, 4, 14.0, 3, 1, 'DEVOIR',   '2025-10-18', NULL),
(12, 1, 11.0, 4, 1, 'DEVOIR',   '2025-10-10', NULL),
-- Élèves 13-16 (5ème A) — Trimestre 1
(13, 1, 13.0, 4, 1, 'DEVOIR',   '2025-10-11', NULL),
(13, 5, 17.0, 3, 1, 'DEVOIR',   '2025-10-20', 'Excellent en anglais'),
(14, 2,  8.5, 4, 1, 'DEVOIR',   '2025-10-13', 'Insuffisant'),
(14, 3, 12.0, 3, 1, 'DEVOIR',   '2025-10-16', NULL),
(15, 1, 18.0, 4, 1, 'DEVOIR',   '2025-10-11', 'Félicitations'),
(15, 2, 14.5, 4, 1, 'DEVOIR',   '2025-10-13', NULL),
-- Élèves 17-20 (2nde A) — Trimestre 2
(17, 1, 12.5, 4, 2, 'COMPOSITION', '2026-01-15', NULL),
(17, 2, 11.0, 4, 2, 'COMPOSITION', '2026-01-17', NULL),
(18, 1, 15.5, 4, 2, 'COMPOSITION', '2026-01-15', 'Bonne progression'),
(18, 5, 16.0, 3, 2, 'COMPOSITION', '2026-01-22', NULL),
(19, 3, 10.5, 3, 2, 'COMPOSITION', '2026-01-19', NULL),
-- Élèves 21-23 (6ème C) — Trimestre 1
(21, 2, 13.0, 4, 1, 'DEVOIR',   '2025-10-14', NULL),
(22, 1, 14.0, 4, 1, 'DEVOIR',   '2025-10-14', NULL),
(23, 3,  7.5, 3, 1, 'DEVOIR',   '2025-10-16', 'Très insuffisant'),
-- Terminale — Trimestre 2
(26, 1, 16.0, 4, 2, 'COMPOSITION', '2026-01-20', 'Excellent'),
(27, 2, 12.0, 4, 2, 'COMPOSITION', '2026-01-22', NULL);

-- ── 5. ABSENCES : 12 lignes ──────────────────────────────────
INSERT INTO absences (eleve_id, date, motif, est_justifiee, periode) VALUES
(8,  '2026-03-03', 'Maladie',            b'1', 'MATIN'),
(9,  '2026-03-05', NULL,                 b'0', 'APRES_MIDI'),
(10, '2026-02-17', 'Rendez-vous médical',b'1', 'JOURNEE'),
(10, '2026-02-20', NULL,                 b'0', 'MATIN'),
(11, '2026-03-10', NULL,                 b'0', 'APRES_MIDI'),
(13, '2026-02-24', 'Décès familial',     b'1', 'JOURNEE'),
(14, '2026-03-01', NULL,                 b'0', 'MATIN'),
(14, '2026-03-04', NULL,                 b'0', 'MATIN'),
(14, '2026-03-07', NULL,                 b'0', 'APRES_MIDI'),
(17, '2026-02-10', 'Convocation',        b'1', 'MATIN'),
(21, '2026-03-12', NULL,                 b'0', 'APRES_MIDI'),
(26, '2026-01-08', 'Voyage scolaire',    b'1', 'JOURNEE');

-- ── 6. PAIEMENTS : 10 lignes ─────────────────────────────────
INSERT INTO paiements (eleve_id, type_paiement, montant_verse, mode_paiement, date_paiement, recu_numero) VALUES
(8,  'SCOLARITE', 50000,  'ESPECES',   '2026-01-05 08:30:00', 'REC-2026-001'),
(9,  'SCOLARITE', 50000,  'MOBILE',    '2026-01-06 10:00:00', 'REC-2026-002'),
(10, 'SCOLARITE', 25000,  'ESPECES',   '2026-01-07 09:15:00', 'REC-2026-003'),
(11, 'SCOLARITE', 50000,  'CHEQUE',    '2026-01-08 11:00:00', 'REC-2026-004'),
(12, 'SCOLARITE', 50000,  'ESPECES',   '2026-01-10 08:00:00', 'REC-2026-005'),
(13, 'SCOLARITE', 55000,  'MOBILE',    '2026-01-12 14:30:00', 'REC-2026-006'),
(15, 'SCOLARITE', 55000,  'ESPECES',   '2026-01-15 09:00:00', 'REC-2026-007'),
(17, 'SCOLARITE', 60000,  'CHEQUE',    '2026-01-18 10:30:00', 'REC-2026-008'),
(18, 'INSCRIPTION', 15000,'ESPECES',   '2026-01-04 08:00:00', 'REC-2026-009'),
(26, 'SCOLARITE', 75000,  'MOBILE',    '2026-01-20 16:00:00', 'REC-2026-010');

-- ── 7. LIGNES BUDGET : 5 lignes ──────────────────────────────
INSERT INTO lignes_budget (categorie, designation, montant_prevu, montant_reel, type_ligne, annee_scolaire, mois, date_creation, etablissement_id) VALUES
('PERSONNEL',  'Salaires enseignants T1',      2500000, 2500000, 'DEPENSE', '2025-2026', 10, '2025-10-01', @etab),
('MATERIEL',   'Achat manuels scolaires',        450000,  420000, 'DEPENSE', '2025-2026', 10, '2025-10-15', @etab),
('ENTRETIEN',  'Réparation toiture bâtiment A',  280000,  295000, 'DEPENSE', '2025-2026', 11, '2025-11-05', @etab),
('SCOLARITE',  'Collecte frais scolarité T1',   3200000, 2950000, 'RECETTE', '2025-2026',  9, '2025-09-30', @etab),
('SUBVENTION', 'Subvention ministère annuelle',   500000,  500000, 'RECETTE', '2025-2026',  9, '2025-09-01', @etab);

-- ── 8. CRÉNEAUX HORAIRES : 10 lignes ─────────────────────────
INSERT INTO creneaux_horaires (jour, heure_debut, heure_fin, classe_id, matiere_id, enseignant_nom, salle, etablissement_id) VALUES
(1, '07:30', '09:30', 1, 1, 'KOKOU Etienne',  'Salle A1', @etab),
(1, '09:45', '11:45', 1, 2, 'BALOUA Sylviane','Salle A1', @etab),
(2, '07:30', '09:30', 1, 3, 'NADJI Hermas',   'Salle A1', @etab),
(2, '09:45', '11:45', 1, 4, 'MOUSSA Ali',     'Salle A1', @etab),
(3, '07:30', '09:30', 2, 1, 'KOKOU Etienne',  'Salle B2', @etab),
(3, '09:45', '11:45', 2, 5, 'BALOUA Sylviane','Salle B2', @etab),
(4, '07:30', '09:30', 5, 1, 'KOKOU Etienne',  'Salle C1', @etab),
(4, '09:45', '11:45', 5, 3, 'NADJI Hermas',   'Salle C1', @etab),
(5, '07:30', '09:30', 7, 4, 'MOUSSA Ali',     'Salle D1', @etab),
(5, '09:45', '11:45', 8, 1, 'KOKOU Etienne',  'Salle D2', @etab);

SET FOREIGN_KEY_CHECKS = 1;

-- ── RÉSUMÉ ────────────────────────────────────────────────────
SELECT 'Classes'    AS Entite, COUNT(*) AS Total FROM classes    WHERE etablissement_id = 5
UNION SELECT 'Élèves',  COUNT(*) FROM eleves    WHERE etablissement_id = 5
UNION SELECT 'Personnels', COUNT(*) FROM personnels WHERE etablissement_id = 5
UNION SELECT 'Notes',    COUNT(*) FROM notes     WHERE eleve_id IN (SELECT id FROM eleves WHERE etablissement_id = 5)
UNION SELECT 'Absences', COUNT(*) FROM absences  WHERE eleve_id IN (SELECT id FROM eleves WHERE etablissement_id = 5)
UNION SELECT 'Paiements',COUNT(*) FROM paiements WHERE eleve_id IN (SELECT id FROM eleves WHERE etablissement_id = 5)
UNION SELECT 'Budgets',  COUNT(*) FROM lignes_budget WHERE etablissement_id = 5
UNION SELECT 'Créneaux', COUNT(*) FROM creneaux_horaires WHERE etablissement_id = 5;
