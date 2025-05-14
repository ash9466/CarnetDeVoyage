-- Création de la base de données
CREATE DATABASE IF NOT EXISTS carnet_voyage;
USE carnet_voyage;

-- Création de la table pour les entrées de voyage
CREATE TABLE IF NOT EXISTS travel_entries (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    latitude DOUBLE,
    longitude DOUBLE,
    image_path VARCHAR(255),
    date VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Index pour améliorer les performances des requêtes
CREATE INDEX idx_title ON travel_entries(title);
CREATE INDEX idx_date ON travel_entries(date);

-- Insertion d'une entrée de test
INSERT INTO travel_entries (title, description, latitude, longitude, date)
VALUES ('Première entrée de test', 'Ceci est une entrée de test', 48.8566, 2.3522, CURRENT_TIMESTAMP);