package fr.android.carnetdevoyage.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import fr.android.carnetdevoyage.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private GoogleMap map;
    private static final int PERM_REQUEST = 1;
    private static final int SETTINGS_RESULT = 1;

    private FusedLocationProviderClient fusedLocationClient;
    private TextView latitude, longitude;
    private LocationCallback locCallback;
    private LocationManager lm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        latitude = view.findViewById(R.id.latitude);
        longitude = view.findViewById(R.id.longitude);

        lm = (LocationManager) requireActivity().getSystemService(requireActivity().LOCATION_SERVICE);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        locCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Log.d("FUSED", "New location");
                Location l = locationResult.getLastLocation();
                latitude.setText(String.valueOf(l.getLatitude()));
                longitude.setText(String.valueOf(l.getLongitude()));

                LatLng p = new LatLng(l.getLatitude(), l.getLongitude());
                map.addMarker(new MarkerOptions().position(p));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(p, 20.0f));
            }
        };

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
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

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERM_REQUEST) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if (map != null) {
                    map.setMyLocationEnabled(true);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    requestLocationUpdates();
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void requestLocationUpdates() {
        LocationRequest.Builder lrb = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY);
        lrb.setMinUpdateDistanceMeters(10);
        lrb.setMinUpdateIntervalMillis(5000);

        LocationRequest lr = lrb.build();
        fusedLocationClient.requestLocationUpdates(lr, this, Looper.getMainLooper());
    }

    @Override
    public void onLocationChanged(@NonNull Location l) {
        latitude.setText(String.valueOf(l.getLatitude()));
        longitude.setText(String.valueOf(l.getLongitude()));

        LatLng p = new LatLng(l.getLatitude(), l.getLongitude());
        map.addMarker(new MarkerOptions().position(p));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(p, 20.0f));
    }
}