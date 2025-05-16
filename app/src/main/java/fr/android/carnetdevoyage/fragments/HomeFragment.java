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
import fr.android.carnetdevoyage.database.TravelViewModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyView;
    private TravelEntryAdapter adapter;
    private TravelViewModel travelViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        emptyView = view.findViewById(R.id.empty_view);

        adapter = new TravelEntryAdapter(getContext(), new ArrayList<>());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        travelViewModel = new ViewModelProvider(requireActivity()).get(TravelViewModel.class);

        travelViewModel.getAllEntries().observe(getViewLifecycleOwner(), entries -> {
            adapter.updateEntries(entries);

            if (entries.isEmpty()) {
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