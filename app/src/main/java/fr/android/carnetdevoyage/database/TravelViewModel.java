package fr.android.carnetdevoyage.database;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class TravelViewModel extends AndroidViewModel {
    private TravelRepository mRepository;
    private LiveData<List<TravelEntry>> mAllEntries;

    public TravelViewModel(Application application) {
        super(application);
        mRepository = new TravelRepository(application);
        mAllEntries = mRepository.getAllEntries();
    }


    public LiveData<List<TravelEntry>> getAllEntries() {
        return mAllEntries;
    }

    public void insert(TravelEntry travelEntry) {
        mRepository.insert(travelEntry);
    }

    public void update(TravelEntry travelEntry) {
        mRepository.update(travelEntry);
    }

}