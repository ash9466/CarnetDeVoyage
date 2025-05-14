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

import com.example.carnetdevoyage.R;
import fr.android.carnetdevoyage.database.DatabaseConnection;
import fr.android.carnetdevoyage.database.TravelEntry;
import fr.android.carnetdevoyage.database.TravelEntryDAO;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddEntryFragment extends Fragment implements DatabaseConnection.OnDatabaseOperationListener {

    private TextInputEditText editTitle, editDescription;
    private TextView textLocationInfo;
    private ImageView imagePreview;
    private Button buttonLocation, buttonTakePhoto, buttonSave;

    private TravelEntryDAO travelEntryDAO;
    private double latitude = 0, longitude = 0;
    private String imagePath = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_entry, container, false);

        // Initialiser les vues
        editTitle = view.findViewById(R.id.edit_title);
        editDescription = view.findViewById(R.id.edit_description);
        textLocationInfo = view.findViewById(R.id.text_location_info);
        imagePreview = view.findViewById(R.id.image_preview);
        buttonLocation = view.findViewById(R.id.button_location);
        buttonTakePhoto = view.findViewById(R.id.button_take_photo);
        buttonSave = view.findViewById(R.id.button_save);

        // Initialiser le DAO
        travelEntryDAO = new TravelEntryDAO(this);

        // Configurer les écouteurs de boutons
        buttonLocation.setOnClickListener(v -> getLocation());
        buttonTakePhoto.setOnClickListener(v -> takePhoto());
        buttonSave.setOnClickListener(v -> saveEntry());

        return view;
    }

    private void getLocation() {
        // Cette méthode serait implémentée par votre binôme pour la partie géolocalisation
        Toast.makeText(getContext(), "Fonctionnalité de géolocalisation non implémentée", Toast.LENGTH_SHORT).show();
    }

    private void takePhoto() {
        // Cette méthode serait implémentée par votre binôme pour la partie photo
        Toast.makeText(getContext(), "Fonctionnalité de photo non implémentée", Toast.LENGTH_SHORT).show();
    }

    private void saveEntry() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (title.isEmpty()) {
            editTitle.setError(getString(R.string.error_title_required));
            return;
        }

        // Obtenir la date actuelle
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        // Créer une nouvelle entrée
        TravelEntry entry = new TravelEntry();
        entry.setTitle(title);
        entry.setDescription(description);
        entry.setLatitude(latitude);
        entry.setLongitude(longitude);
        entry.setImagePath(imagePath);
        entry.setDate(currentDate);

        // Enregistrer dans la base de données externe
        travelEntryDAO.insert(entry);
    }

    // Méthodes de callback pour les opérations de base de données
    @Override
    public void onSelectComplete(List<TravelEntry> entries) {
        // Non utilisé dans ce fragment
    }

    @Override
    public void onOperationComplete(boolean success) {
        if (success) {
            Toast.makeText(getContext(), R.string.entry_saved, Toast.LENGTH_SHORT).show();

            // Effacer les champs après l'enregistrement
            editTitle.setText("");
            editDescription.setText("");
            textLocationInfo.setText(R.string.location_not_available);
            imagePreview.setImageResource(0);

            // Revenir au fragment d'accueil
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        } else {
            Toast.makeText(getContext(), R.string.error_saving_entry, Toast.LENGTH_SHORT).show();
        }
    }
}