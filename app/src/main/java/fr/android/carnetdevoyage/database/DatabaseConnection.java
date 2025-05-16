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

    // Database credentials - consider storing these securely
    private static final String DB_URL = "jdbc:mariadb://10.0.2.2:3307/carnet_voyage";
    private static final String USER = "root";
    private static final String PASS = "";

    // Interface for database operation callbacks
    public interface OnDatabaseOperationListener {
        void onSelectComplete(List<TravelEntry> entries);
        void onOperationComplete(boolean success);
    }

    // Method to get database connection
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Load the MariaDB JDBC driver
            Class.forName("org.mariadb.jdbc.Driver");

            // Get connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Log.d(TAG, "Database connection established successfully");
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "MariaDB JDBC Driver not found", e);
        } catch (SQLException e) {
            Log.e(TAG, "Failed to connect to database", e);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error connecting to database", e);
        }
        return conn;
    }

    // Method to execute SELECT query
    public static void executeSelect(String query, OnDatabaseOperationListener listener, String... params) {
        new DBTask(listener, "SELECT").execute(prepareParams(query, params));
    }

    // Method to execute INSERT query
    public static void executeInsert(String query, OnDatabaseOperationListener listener, String... params) {
        new DBTask(listener, "INSERT").execute(prepareParams(query, params));
    }

    // Method to execute UPDATE query
    public static void executeUpdate(String query, OnDatabaseOperationListener listener, String... params) {
        new DBTask(listener, "UPDATE").execute(prepareParams(query, params));
    }

    // Method to execute DELETE query
    public static void executeDelete(String query, OnDatabaseOperationListener listener, String... params) {
        new DBTask(listener, "DELETE").execute(prepareParams(query, params));
    }

    // Helper method to prepare parameters
    private static String[] prepareParams(String query, String... params) {
        String[] allParams = new String[params.length + 1];
        allParams[0] = query;
        System.arraycopy(params, 0, allParams, 1, params.length);
        return allParams;
    }

    // AsyncTask for database operations
    public static class DBTask extends AsyncTask<String, Void, Boolean> {
        private OnDatabaseOperationListener listener;
        private String operation;
        private List<TravelEntry> entriesList;

        public DBTask(OnDatabaseOperationListener listener, String operation) {
            this.listener = listener;
            this.operation = operation;
            this.entriesList = new ArrayList<>();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                // Get connection
                conn = getConnection();

                // Check if connection is successful
                if (conn == null) {
                    Log.e(TAG, "Database connection is null");
                    return false;
                }

                // Prepare statement
                String sql = params[0];
                stmt = conn.prepareStatement(sql);

                // Set parameters
                for (int i = 1; i < params.length; i++) {
                    if (params[i] != null) {
                        stmt.setString(i, params[i]);
                    } else {
                        stmt.setNull(i, java.sql.Types.VARCHAR);
                    }
                }

                // Execute query based on operation type
                if ("SELECT".equals(operation)) {
                    rs = stmt.executeQuery();

                    while (rs != null && rs.next()) {
                        TravelEntry entry = new TravelEntry();

                        try {
                            entry.setId(rs.getInt("id"));
                            entry.setTitle(rs.getString("title"));
                            entry.setDescription(rs.getString("description"));
                            entry.setLatitude(rs.getDouble("latitude"));
                            entry.setLongitude(rs.getDouble("longitude"));
                            entry.setImagePath(rs.getString("image_path"));
                            entry.setDate(rs.getString("date"));
                            entriesList.add(entry);
                        } catch (SQLException e) {
                            Log.e(TAG, "Error reading column from result set", e);
                        }
                    }
                    return true;
                } else {
                    int rowsAffected = stmt.executeUpdate();
                    return rowsAffected > 0;
                }
            } catch (SQLException e) {
                Log.e(TAG, "SQL Error: " + e.getMessage(), e);
                return false;
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error: " + e.getMessage(), e);
                return false;
            } finally {
                // Close all resources
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    Log.e(TAG, "Error closing database resources", e);
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (listener != null) {
                if ("SELECT".equals(operation)) {
                    listener.onSelectComplete(entriesList);
                } else if ("INSERT".equals(operation) ||
                        "UPDATE".equals(operation) ||
                        "DELETE".equals(operation)) {
                    listener.onOperationComplete(result);
                }
            }
        }
    }
}