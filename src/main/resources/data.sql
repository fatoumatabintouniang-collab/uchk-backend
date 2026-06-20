-- ─── Rôles ────────────────────────────────────────────────────────────────────
INSERT IGNORE INTO role (nom) VALUES ('ADMIN');
INSERT IGNORE INTO role (nom) VALUES ('ADMINISTRATIF');
INSERT IGNORE INTO role (nom) VALUES ('ENSEIGNANT');
INSERT IGNORE INTO role (nom) VALUES ('ENSEIGNANT_ASSOCIE');
INSERT IGNORE INTO role (nom) VALUES ('RESPONSABLE_FORMATION');
INSERT IGNORE INTO role (nom) VALUES ('TUTEUR');
INSERT IGNORE INTO role (nom) VALUES ('APPUI_INSERTION');
INSERT IGNORE INTO role (nom) VALUES ('ETUDIANT');

-- ─── Compte ADMIN par défaut ──────────────────────────────────────────────────
-- Mot de passe : Admin@2026
INSERT IGNORE INTO utilisateur (nom, prenom, email, ine, mot_de_passe, actif, role_id)
SELECT 'Admin', 'UCHK', 'admin@uchk.edu.sn', NULL,
       '$2b$10$LtGuKNIY/9uMFtoukeM64uozL6kUBE8BRVDqQCZfrOERxpmTN736.',
       true,
       (SELECT id FROM role WHERE nom = 'ADMIN')
WHERE NOT EXISTS (
    SELECT 1 FROM utilisateur WHERE email = 'admin@uchk.edu.sn'
);