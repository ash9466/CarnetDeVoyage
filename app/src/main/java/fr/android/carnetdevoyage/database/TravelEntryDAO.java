package fr.android.carnetdevoyage.database;

import java.util.List;

public class TravelEntryDAO {

    // Requêtes SQL
    private static final String SQL_INSERT = "INSERT INTO travel_entries (title, description, latitude, longitude, image_path, date) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE travel_entries SET title = ?, description = ?, latitude = ?, longitude = ?, image_path = ?, date = ? WHERE id = ?";
    private static final String SQL_DELETE = "DELETE FROM travel_entries WHERE id = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM travel_entries";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM travel_entries WHERE id = ?";

    // Interface pour les callbacks
    private DatabaseConnection.OnDatabaseOperationListener listener;

    public TravelEntryDAO(DatabaseConnection.OnDatabaseOperationListener listener) {
        this.listener = listener;
    }

    // Insérer une nouvelle entrée
    public void insert(TravelEntry entry) {
        new DatabaseConnection.DBTask(listener, "INSERT").execute(
                SQL_INSERT,
                entry.getTitle(),
                entry.getDescription(),
                String.valueOf(entry.getLatitude()),
                String.valueOf(entry.getLongitude()),
                entry.getImagePath(),
                entry.getDate()
        );
    }

    // Mettre à jour une entrée existante
    public void update(TravelEntry entry) {
        new DatabaseConnection.DBTask(listener, "UPDATE").execute(
                SQL_UPDATE,
                entry.getTitle(),
                entry.getDescription(),
                String.valueOf(entry.getLatitude()),
                String.valueOf(entry.getLongitude()),
                entry.getImagePath(),
                entry.getDate(),
                String.valueOf(entry.getId())
        );
    }

    // Supprimer une entrée
    public void delete(int id) {
        new DatabaseConnection.DBTask(listener, "DELETE").execute(
                SQL_DELETE,
                String.valueOf(id)
        );
    }

    // Récupérer toutes les entrées
    public void getAllEntries() {
        new DatabaseConnection.DBTask(listener, "SELECT").execute(SQL_SELECT_ALL);
    }

    // Récupérer une entrée par son ID
    public void getEntryById(int id) {
        new DatabaseConnection.DBTask(listener, "SELECT").execute(
                SQL_SELECT_BY_ID,
                String.valueOf(id)
        );
    }
}