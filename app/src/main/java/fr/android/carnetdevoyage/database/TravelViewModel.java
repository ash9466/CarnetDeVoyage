package fr.android.carnetdevoyage.database;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class TravelViewModel extends AndroidViewModel {
    private TravelRepository mRepository;
    private LiveData<List<TravelEntry>> mAllEntries;

    // Constructor
    public TravelViewModel(Application application) {
        super(application);
        mRepository = new TravelRepository(application);
        mAllEntries = mRepository.getAllEntries();
    }

    // Get all entries - exposed to UI
    public LiveData<List<TravelEntry>> getAllEntries() {
        return mAllEntries;
    }

    // Insert entry
    public void insert(TravelEntry travelEntry) {
        mRepository.insert(travelEntry);
    }

    // Update entry
    public void update(TravelEntry travelEntry) {
        mRepository.update(travelEntry);
    }

    // Delete entry
    public void delete(TravelEntry travelEntry) {
        mRepository.delete(travelEntry);
    }

    // Get entry by ID
    public LiveData<TravelEntry> getEntryById(int id) {
        return mRepository.getEntryById(id);
    }

    // Get entries in geographical area
    public LiveData<List<TravelEntry>> getEntriesInArea(double minLat, double maxLat, double minLong, double maxLong) {
        return mRepository.getEntriesInArea(minLat, maxLat, minLong, maxLong);
    }

    // Search entries
    public LiveData<List<TravelEntry>> searchEntries(String query) {
        return mRepository.searchEntries(query);
    }
}