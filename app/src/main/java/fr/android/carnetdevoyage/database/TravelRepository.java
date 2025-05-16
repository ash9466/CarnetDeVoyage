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

    public TravelRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mTravelEntryDao = db.travelEntryDao();
        mAllEntries = mTravelEntryDao.getAllEntries();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<TravelEntry>> getAllEntries() {
        return mAllEntries;
    }

    public void insert(TravelEntry travelEntry) {
        executor.execute(() -> {
            mTravelEntryDao.insert(travelEntry);
        });
    }

    public void update(TravelEntry travelEntry) {
        executor.execute(() -> {
            mTravelEntryDao.update(travelEntry);
        });
    }
}
