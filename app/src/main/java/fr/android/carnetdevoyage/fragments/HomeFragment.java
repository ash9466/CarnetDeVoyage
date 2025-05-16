package fr.android.carnetdevoyage.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import fr.android.carnetdevoyage.R;
import fr.android.carnetdevoyage.adapters.TravelEntryAdapter;
import fr.android.carnetdevoyage.database.TravelEntry;
import fr.android.carnetdevoyage.database.TravelViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyView;
    private TravelEntryAdapter adapter;
    private List<TravelEntry> entryList;
    private TravelViewModel travelViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        emptyView = view.findViewById(R.id.empty_view);

        // Initialize list and adapter
        entryList = new ArrayList<>();
        adapter = new TravelEntryAdapter(getContext(), entryList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Initialize ViewModel
        travelViewModel = new ViewModelProvider(this).get(TravelViewModel.class);

        // Observe the LiveData
        travelViewModel.getAllEntries().observe(getViewLifecycleOwner(), entries -> {
            entryList.clear();
            entryList.addAll(entries);
            adapter.notifyDataSetChanged();

            // Show empty view if no entries
            if (entryList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
        });

        return view;
    }
}