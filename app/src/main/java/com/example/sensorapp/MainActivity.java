package com.example.sensorapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private List<Sensor> sensorList;
    private ListView listView;
    private ArrayList<String> sensors;
    private ArrayAdapter<String> sensorAdapter;
    private TextView sensorCounterTextView;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        sensorCounterTextView = findViewById(R.id.counter);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        sensors = new ArrayList<String>();
        sensorAdapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,sensors);
        listView.setAdapter(sensorAdapter);

        for(Sensor sensor : sensorList){
            sensors.add(sensor.getName());
            counter++;
        }

        sensorCounterTextView.setText("Liczba dostępnych czujników: " + counter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                if(pos == 1){
                    Intent intent = new Intent(view.getContext(), SensorDetailsActivity.class);

                    intent.putExtra("sensorType", "gyro");
                    view.getContext().startActivity(intent);
                }
                else if(pos == 6){
                    Intent intent = new Intent(view.getContext(), SensorDetailsActivity.class);

                    intent.putExtra("sensorType", "light");
                    view.getContext().startActivity(intent);
                }
                else if(pos == 2){
                    Intent intent = new Intent(view.getContext(), LocationActivity.class);
                    view.getContext().startActivity(intent);
                }
            }
        });
    }
}