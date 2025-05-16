package fr.android.carnetdevoyage.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TravelEntryDAO {
    // Insert a new travel entry
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TravelEntry travelEntry);

    // Update an existing travel entry
    @Update
    int update(TravelEntry travelEntry);

    // Delete a travel entry
    @Delete
    int delete(TravelEntry travelEntry);

    // Get all travel entries
    @Query("SELECT * FROM travel_entries ORDER BY date DESC")
    LiveData<List<TravelEntry>> getAllEntries();

    // Get a specific travel entry by ID
    @Query("SELECT * FROM travel_entries WHERE id = :id")
    LiveData<TravelEntry> getEntryById(int id);

    // Delete all travel entries
    @Query("DELETE FROM travel_entries")
    void deleteAll();

    // Get entries within a geographical area
    @Query("SELECT * FROM travel_entries WHERE latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLong AND :maxLong")
    LiveData<List<TravelEntry>> getEntriesInArea(double minLat, double maxLat, double minLong, double maxLong);

    // Search entries by title or description
    @Query("SELECT * FROM travel_entries WHERE title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%'")
    LiveData<List<TravelEntry>> searchEntries(String searchQuery);
}