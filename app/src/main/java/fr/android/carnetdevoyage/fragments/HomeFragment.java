package fr.android.carnetdevoyage.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carnetdevoyage.R;
import fr.android.carnetdevoyage.adapters.TravelEntryAdapter;
import fr.android.carnetdevoyage.database.DatabaseConnection;
import fr.android.carnetdevoyage.database.TravelEntry;
import fr.android.carnetdevoyage.database.TravelEntryDAO;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements DatabaseConnection.OnDatabaseOperationListener {

    private RecyclerView recyclerView;
    private TextView emptyView;
    private TravelEntryAdapter adapter;
    private List<TravelEntry> entryList;
    private TravelEntryDAO travelEntryDAO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        emptyView = view.findViewById(R.id.empty_view);

        // Initialiser la liste et l'adaptateur
        entryList = new ArrayList<>();
        adapter = new TravelEntryAdapter(getContext(), entryList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Initialiser le DAO
        travelEntryDAO = new TravelEntryDAO(this);

        // Charger les données
        loadData();

        return view;
    }

    private void loadData() {
        // Charger les données depuis la base de données externe
        travelEntryDAO.getAllEntries();
    }

    // Méthodes de callback pour les opérations de base de données
    @Override
    public void onSelectComplete(List<TravelEntry> entries) {
        entryList.clear();
        entryList.addAll(entries);
        adapter.notifyDataSetChanged();

        // Afficher le message "empty" si aucune entrée n'est disponible
        if (entryList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onOperationComplete(boolean success) {
        if (success) {
            // Recharger les données après une opération réussie
            loadData();
        }
    }
}