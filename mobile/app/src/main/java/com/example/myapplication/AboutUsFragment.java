package com.example.myapplication;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;

public class AboutUsFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_about_us, container, false);

        // Configuration de la MapView
        mapView = rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);  // L'appel de cette méthode est crucial pour l'initialisation de la carte

        // Initialisation du FusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Vérification et demande de la permission de localisation
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Demander la permission si elle n'est pas accordée
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Vérifiez si la permission de localisation est accordée avant d'afficher la carte
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Activer la fonctionnalité de localisation de l'utilisateur sur la carte
            googleMap.setMyLocationEnabled(true);

            // Demander la localisation de l'utilisateur
            fusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                // Récupérer la localisation
                                Location location = task.getResult();
                                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                                // Ajouter un marqueur à la position de l'utilisateur avec un titre personnalisé
                                googleMap.addMarker(new MarkerOptions()
                                        .position(userLocation)
                                        .title("Notre localisation"));

                                // Activer les contrôles de zoom
                                googleMap.getUiSettings().setZoomControlsEnabled(true);

                                // Activer les gestes de zoom (pincer pour zoomer)
                                googleMap.getUiSettings().setZoomGesturesEnabled(true);

                                // Définir le zoom et la position de la caméra sur la localisation de l'utilisateur
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                            } else {
                                // Si la localisation échoue, afficher un message ou centrer la carte ailleurs
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 2)); // C'est un fallback, vous pouvez ajuster cette valeur
                            }
                        }
                    });
        } else {
            // Si la permission n'est pas accordée, afficher un message
            Toast.makeText(getActivity(), "Permission de localisation requise", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission accordée, rechargez la carte
                Toast.makeText(getActivity(), "Permission accordée", Toast.LENGTH_SHORT).show();
                mapView.getMapAsync(this); // Recharger la carte si la permission est accordée
            } else {
                // Permission refusée
                Toast.makeText(getActivity(), "Permission de localisation refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }
}
