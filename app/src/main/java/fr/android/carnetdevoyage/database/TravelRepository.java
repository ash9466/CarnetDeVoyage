package fr.android.carnetdevoyage.database;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TravelRepository {
    private TravelEntryDAO mTravelEntryDao;
    private LiveData<List<TravelEntry>> mAllEntries;
    private ExecutorService executor;

    // Constructor
    public TravelRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mTravelEntryDao = db.travelEntryDao();
        mAllEntries = mTravelEntryDao.getAllEntries();
        executor = Executors.newSingleThreadExecutor();
    }

    // Get all travel entries - LiveData will automatically update UI when data changes
    public LiveData<List<TravelEntry>> getAllEntries() {
        return mAllEntries;
    }

    // Insert travel entry
    public void insert(TravelEntry travelEntry) {
        executor.execute(() -> {
            mTravelEntryDao.insert(travelEntry);
        });
    }

    // Update travel entry
    public void update(TravelEntry travelEntry) {
        executor.execute(() -> {
            mTravelEntryDao.update(travelEntry);
        });
    }

    // Delete travel entry
    public void delete(TravelEntry travelEntry) {
        executor.execute(() -> {
            mTravelEntryDao.delete(travelEntry);
        });
    }

    // Get entry by ID
    public LiveData<TravelEntry> getEntryById(int id) {
        return mTravelEntryDao.getEntryById(id);
    }

    // Get entries in geographical area
    public LiveData<List<TravelEntry>> getEntriesInArea(double minLat, double maxLat, double minLong, double maxLong) {
        return mTravelEntryDao.getEntriesInArea(minLat, maxLat, minLong, maxLong);
    }

    // Search entries
    public LiveData<List<TravelEntry>> searchEntries(String query) {
        return mTravelEntryDao.searchEntries(query);
    }
}
