package fr.android.carnetdevoyage.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {TravelEntry.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    // DAO accessor
    public abstract TravelEntryDAO travelEntryDao();

    // Singleton instance
    private static volatile AppDatabase INSTANCE;

    // Get database instance
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "carnet_voyage.db")
                            .fallbackToDestructiveMigration()  // Reset database if migration fails
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}