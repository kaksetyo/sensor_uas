package com.example.the_perfect_room;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;

import com.example.the_perfect_room.Model.AccelerometerData;
import com.example.the_perfect_room.Model.ProximityData;
import com.example.the_perfect_room.ViewModel.ViewModelAccelerometer;
import com.example.the_perfect_room.ViewModel.ViewModelProximity;
import com.example.the_perfect_room.databinding.ActivityMainBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ActivityMainBinding mainBinding;
    private ViewModelProximity viewModelProximity;
    private ViewModelAccelerometer viewModelAccelerometer;
    private Sensor  proximitySensor, accelerometerSensor;
    private SensorManager sensorManager;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mainBinding.proximitySensor.setText(String.valueOf(0));

        viewModelProximity = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ViewModelProximity.class);
        viewModelAccelerometer = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ViewModelAccelerometer.class);

        databaseTransaction();

        mainBinding.proximityCard.setOnClickListener(v ->{
            startActivity(new Intent(MainActivity.this, Proximity_Sensor_Activity.class));
        });
        mainBinding.accelerometerCard.setOnClickListener(v ->{
            startActivity(new Intent(MainActivity.this, Accelerometer_Activity.class));
        });

    }

    public void databaseTransaction() {
        count++;
        getSensorData();

        refresh(100000);
    }

    private void refresh(int milliseconds) {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                databaseTransaction();
            }
        };
        handler.postDelayed(runnable, milliseconds);
    }

    private void getSensorData() {

        String proximity = mainBinding.proximitySensor.getText().toString();
        String acceleratorX = mainBinding.accelerometer1.getText().toString();
        String acceleratorY = mainBinding.accelerometer2.getText().toString();
        String acceleratorZ = mainBinding.accelerometer3.getText().toString();


        ProximityData proximityData = new ProximityData(0, proximity, String.valueOf(Calendar.getInstance().getTime()));
        viewModelProximity.insert(proximityData);

        AccelerometerData accelerometerData = new AccelerometerData(0, acceleratorX, acceleratorY, acceleratorZ, String.valueOf(Calendar.getInstance().getTime()));
        viewModelAccelerometer.insert(accelerometerData);


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                float accelerometerValueX = event.values[0];
                mainBinding.accelerometer1.setText(String.valueOf(accelerometerValueX));

                float accelerometerValueY = event.values[1];
                mainBinding.accelerometer2.setText(String.valueOf(accelerometerValueY));

                float accelerometerValueZ = event.values[2];
                mainBinding.accelerometer3.setText(String.valueOf(accelerometerValueZ));

                break;
            case Sensor.TYPE_PROXIMITY:

                float proximityValue = event.values[0];
                mainBinding.proximitySensor.setText(String.valueOf(proximityValue));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}