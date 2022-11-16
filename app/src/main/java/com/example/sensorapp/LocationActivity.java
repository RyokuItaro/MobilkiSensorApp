package com.example.sensorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

public class LocationActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private Button getLocationButton;
    private Button getBackButton;

    private TextView locationTextView;
    private Location lastLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        getLocationButton = findViewById(R.id.getLocationButton);
        getBackButton = findViewById(R.id.getBackButton);
        locationTextView = findViewById(R.id.locationText);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new Intent(view.getContext(), MainActivity.class));
            }
        });

        getLocationButton.setOnClickListener(v -> getLocation());
    }

    private void getLocation(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }else{
            Task<Location> locationhaha = fusedLocationProviderClient.getLastLocation();
            locationhaha.addOnSuccessListener(
                    location -> {
                        if (location != null) {
                            lastLocation = location;
                            locationTextView.setText(
                                    "Szerokosc geograficzna: " + location.getLatitude() + "\n" +
                                            "Dlugosc geograficzna: " + location.getLongitude() + "\n" +
                                            "Czas: " + location.getTime()
                            );
                        } else {
                            locationTextView.setText("Brak informacji o lokalizacji");
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(this, "Brak uprawnień", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}