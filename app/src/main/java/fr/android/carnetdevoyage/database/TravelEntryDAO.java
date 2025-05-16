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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TravelEntry travelEntry);

    @Update
    int update(TravelEntry travelEntry);

    @Query("SELECT * FROM travel_entries ORDER BY date DESC")
    LiveData<List<TravelEntry>> getAllEntries();
}