package fr.android.carnetdevoyage.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.carnetdevoyage.R;
import fr.android.carnetdevoyage.database.TravelEntry;
import fr.android.carnetdevoyage.database.TravelViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEntryFragment extends Fragment {
    private TextInputEditText editTitle, editDescription;
    private TextView textLocationInfo;
    private ImageView imagePreview;
    private Button buttonLocation, buttonTakePhoto, buttonSave;

    private TravelViewModel travelViewModel;
    private double latitude = 0, longitude = 0;
    private String imagePath = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_entry, container, false);

        // Initialize views
        editTitle = view.findViewById(R.id.edit_title);
        editDescription = view.findViewById(R.id.edit_description);
        textLocationInfo = view.findViewById(R.id.text_location_info);
        imagePreview = view.findViewById(R.id.image_preview);
        buttonLocation = view.findViewById(R.id.button_location);
        buttonTakePhoto = view.findViewById(R.id.button_take_photo);
        buttonSave = view.findViewById(R.id.button_save);

        // Initialize ViewModel
        travelViewModel = new ViewModelProvider(this).get(TravelViewModel.class);

        // Configure button listeners
        buttonLocation.setOnClickListener(v -> getLocation());
        buttonTakePhoto.setOnClickListener(v -> takePhoto());
        buttonSave.setOnClickListener(v -> saveEntry());

        return view;
    }

    private void getLocation() {
        // This method would be implemented by your partner for the geolocation part
        Toast.makeText(getContext(), "Geolocation feature not implemented", Toast.LENGTH_SHORT).show();
    }

    private void takePhoto() {
        // This method would be implemented by your partner for the photo part
        Toast.makeText(getContext(), "Photo feature not implemented", Toast.LENGTH_SHORT).show();
    }

    private void saveEntry() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (title.isEmpty()) {
            editTitle.setError(getString(R.string.error_title_required));
            return;
        }

        // Get current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        // Create new entry
        TravelEntry entry = new TravelEntry();
        entry.setTitle(title);
        entry.setDescription(description);
        entry.setLatitude(latitude);
        entry.setLongitude(longitude);
        entry.setImagePath(imagePath);
        entry.setDate(currentDate);

        // Save to database using ViewModel
        travelViewModel.insert(entry);

        // Show success message
        Toast.makeText(getContext(), R.string.entry_saved, Toast.LENGTH_SHORT).show();

        // Clear fields after saving
        editTitle.setText("");
        editDescription.setText("");
        textLocationInfo.setText(R.string.location_not_available);
        imagePreview.setImageResource(0);

        // Return to home fragment
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}