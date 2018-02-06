package org.bubulescu.boundservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BoundService boundService;
    private boolean serviceBound = false;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BoundService.BoundServiceBinder boundServiceBinder = (BoundService.BoundServiceBinder) service;
            boundService = boundServiceBinder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private Button btnStart, btnStop, btnChkCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidgets();
        setupListeners();
    }

    private void initWidgets() {
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnChkCounter = findViewById(R.id.btnChkCounter);
        setupButtons();
    }

    private void setupButtons() {
        if (serviceBound) {
            btnStart.setVisibility(View.INVISIBLE);
            btnStop.setVisibility(View.VISIBLE);
            btnChkCounter.setVisibility(View.VISIBLE);
        }else {
            btnStart.setVisibility(View.VISIBLE);
            btnStop.setVisibility(View.INVISIBLE);
            btnChkCounter.setVisibility(View.INVISIBLE);
        }
    }

    private void setupListeners() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!serviceBound) {
                    Intent i = new Intent(getApplicationContext(), BoundService.class);
                    startService(i);
                    bindService(i, serviceConnection, BIND_AUTO_CREATE);
                    serviceBound = true;
                    setupButtons();
                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serviceBound) {
                    unbindService(serviceConnection);
                    serviceBound = false;
                    Intent i = new Intent(getApplicationContext(), BoundService.class);
                    stopService(i);
                    setupButtons();
                }
            }
        });
        btnChkCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serviceBound) {
                    String counterValue = boundService.getCounterValue();
                    Toast.makeText(getApplicationContext(), counterValue, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
