package com.example.sensorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SensorDetailsActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView sensorNameTextView;
    private TextView sensorTypeTextView;
    private TextView sensorVendorTextView;
    private TextView sensorResTextView;
    private TextView sensorVerTextView;
    private TextView sensorPowerTextView;
    private TextView sensorMaxRangeTextView;
    private Button backButton;
    private boolean isSensorGyro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);
        Intent intent = getIntent();
        String x = intent.getStringExtra("sensorType");
        isSensorGyro = x.equals("gyro");
        backButton = findViewById(R.id.backButton);
        sensorNameTextView = findViewById(R.id.sensorName);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(isSensorGyro ? Sensor.TYPE_GYROSCOPE : Sensor.TYPE_LIGHT);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new Intent(view.getContext(), MainActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        float currValue = sensorEvent.values[0];

        switch (sensorType){
            case Sensor.TYPE_LIGHT:
                sensorNameTextView.setText(getResources().getString(R.string.light_sensor_label) + " " + currValue);
                break;
            case Sensor.TYPE_GYROSCOPE:
                sensorNameTextView.setText(getResources().getString(R.string.gyro_sensor_label) + " " + currValue);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}