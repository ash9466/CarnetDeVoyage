package fr.android.carnetdevoyage.database;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private static final String TAG = "DatabaseConnection";

    // Configuration de la base de données - votre binôme devra modifier ces valeurs
    private static final String DB_URL = "jdbc:mysql://10.0.2.2:3306/carnet_voyage";
    private static final String USER = "root";
    private static final String PASS = "";

    // Connexion à la base de données
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // AsyncTask pour exécuter les requêtes en arrière-plan
    public static class DBTask extends AsyncTask<String, Void, Boolean> {
        private OnDatabaseOperationListener listener;
        private String operation;

        public DBTask(OnDatabaseOperationListener listener, String operation) {
            this.listener = listener;
            this.operation = operation;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                conn = getConnection();
                stmt = conn.prepareStatement(params[0]);

                // Paramètres de la requête
                for (int i = 1; i < params.length; i++) {
                    stmt.setString(i, params[i]);
                }

                if (operation.equals("SELECT")) {
                    ResultSet rs = stmt.executeQuery();
                    List<TravelEntry> entries = new ArrayList<>();
                    while (rs.next()) {
                        TravelEntry entry = new TravelEntry();
                        entry.setId(rs.getInt("id"));
                        entry.setTitle(rs.getString("title"));
                        entry.setDescription(rs.getString("description"));
                        entry.setLatitude(rs.getDouble("latitude"));
                        entry.setLongitude(rs.getDouble("longitude"));
                        entry.setImagePath(rs.getString("image_path"));
                        entry.setDate(rs.getString("date"));
                        entries.add(entry);
                    }
                    rs.close();
                    if (listener != null) {
                        listener.onSelectComplete(entries);
                    }
                } else {
                    int rowsAffected = stmt.executeUpdate();
                    return rowsAffected > 0;
                }
                return true;
            } catch (Exception e) {
                Log.e(TAG, "Erreur de connexion à la base de données: " + e.getMessage());
                return false;
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    Log.e(TAG, "Erreur lors de la fermeture des ressources: " + e.getMessage());
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (listener != null) {
                if (operation.equals("INSERT") || operation.equals("UPDATE") || operation.equals("DELETE")) {
                    listener.onOperationComplete(result);
                }
            }
        }
    }

    // Interface pour les callbacks
    public interface OnDatabaseOperationListener {
        void onSelectComplete(List<TravelEntry> entries);
        void onOperationComplete(boolean success);
    }
}