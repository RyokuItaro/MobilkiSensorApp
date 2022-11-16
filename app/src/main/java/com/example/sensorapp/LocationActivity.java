package com.example.sensorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LocationActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private Button getLocationButton;
    private Button getAddressButton;
    private Button getBackButton;

    private TextView locationTextView;
    private TextView addressTextView;
    private Location lastLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        getLocationButton = findViewById(R.id.getLocationButton);
        getBackButton = findViewById(R.id.getBackButton);
        getAddressButton = findViewById(R.id.getAddress);
        locationTextView = findViewById(R.id.locationText);
        addressTextView = findViewById(R.id.addressText);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new Intent(view.getContext(), MainActivity.class));
            }
        });

        getLocationButton.setOnClickListener(v -> getLocation());
        getAddressButton.setOnClickListener(v -> executeGeocoding());
    }

    private void getLocation(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }else{
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(
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

    private String locationGeocoding(Context context, Location location){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        String resultMessage = "";
        try{
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1
            );
        }
        catch (IOException ex){
            resultMessage = "Service not available";
        }

        if(addresses == null || addresses.isEmpty()){
            if(resultMessage.isEmpty()){
                resultMessage = "No address found";
            }
        }
        else{
            Address address = addresses.get(0);
            List<String> addressParts = new ArrayList<>();

            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++){
                addressParts.add(address.getAddressLine(i));
            }
            resultMessage = TextUtils.join("\n", addressParts);
        }

        return resultMessage;
    }

    private void executeGeocoding() {
        if(lastLocation != null){
            ExecutorService executor = Executors.newSingleThreadExecutor();
            String benc = locationGeocoding(getApplicationContext(), lastLocation);
            Future<String> returnedAddress = executor.submit(() -> benc);
            try{
                String result = returnedAddress.get();
                addressTextView.setText("Adres: " + result + System.currentTimeMillis());
            } catch (ExecutionException | InterruptedException ex){
                Thread.currentThread().interrupt();
            }
        }
    }
}
