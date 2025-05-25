package fr.android.carnetdevoyage.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import fr.android.carnetdevoyage.R;
import fr.android.carnetdevoyage.database.TravelEntry;
import fr.android.carnetdevoyage.database.TravelViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEntryFragment extends Fragment {
    private static final int PERM_REQUEST = 1;
    private static final int SETTINGS_RESULT = 1;

    private static final int CAMERA_REQUEST = 2;
    private Uri imageUri;

    private TextInputEditText editTitle, editDescription;
    private TextView textLocationInfo;
    private ImageView imagePreview;
    private Button buttonLocation, buttonTakePhoto, buttonSave;

    private TravelViewModel travelViewModel;
    private double latitude = 0, longitude = 0;
    private String imagePath = "";

    private FusedLocationProviderClient fusedLocationClient;
    private LocationManager locationManager;
    private LocationCallback locationCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_entry, container, false);

        editTitle = view.findViewById(R.id.edit_title);
        editDescription = view.findViewById(R.id.edit_description);
        textLocationInfo = view.findViewById(R.id.text_location_info);
        imagePreview = view.findViewById(R.id.image_preview);
        buttonLocation = view.findViewById(R.id.button_location);
        buttonTakePhoto = view.findViewById(R.id.button_take_photo);
        buttonSave = view.findViewById(R.id.button_save);

        travelViewModel = new ViewModelProvider(this).get(TravelViewModel.class);
        locationManager = (LocationManager) requireActivity().getSystemService(requireActivity().LOCATION_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    textLocationInfo.setText(String.format(Locale.getDefault(),
                            "Lat: %.6f, Long: %.6f", latitude, longitude));
                }
            }
        };

        buttonLocation.setOnClickListener(v -> getLocation());
        buttonTakePhoto.setOnClickListener(v -> takePhoto());
        buttonSave.setOnClickListener(v -> saveEntry());

        return view;
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                enableGPS();
            } else {
                requestLocationUpdates();
            }
        } else {
            String[] perms = new String[] { Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION };
            requestPermissions(perms, PERM_REQUEST);
        }
    }

    private void enableGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(R.string.gps_disabled_message)
                .setCancelable(false)
                .setPositiveButton(R.string.enable_gps, (dialog, id) -> {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                            SETTINGS_RESULT);
                })
                .setNegativeButton(R.string.quit, (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void requestLocationUpdates() {
        LocationRequest.Builder lrb = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY);
        lrb.setMinUpdateDistanceMeters(10);
        lrb.setMinUpdateIntervalMillis(5000);

        LocationRequest lr = lrb.build();
        fusedLocationClient.requestLocationUpdates(lr, locationCallback, Looper.getMainLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERM_REQUEST) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    requestLocationUpdates();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            }
        }
    }

    private void takePhoto() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
            return;
        }

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(requireContext(),
                        requireContext().getPackageName() + ".fileprovider", photoFile);
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
            imagePath = image.getAbsolutePath(); // store path for saving in DB
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && imageUri != null) {
            imagePreview.setImageURI(imageUri);
        }
    }

    private void saveEntry() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (title.isEmpty()) {
            editTitle.setError(getString(R.string.error_title_required));
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        TravelEntry entry = new TravelEntry();
        entry.setTitle(title);
        entry.setDescription(description);
        entry.setLatitude(latitude);
        entry.setLongitude(longitude);
        entry.setImagePath(imagePath);
        entry.setDate(currentDate);

        travelViewModel.insert(entry);

        Toast.makeText(getContext(), R.string.entry_saved, Toast.LENGTH_SHORT).show();

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