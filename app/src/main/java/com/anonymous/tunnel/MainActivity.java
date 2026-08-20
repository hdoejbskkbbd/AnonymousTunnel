package com.anonymous.tunnel;

import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvStatus;
    private Button btnConnect;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tv_status);
        btnConnect = findViewById(R.id.btn_connect);

        btnConnect.setOnClickListener(v -> {
            if (!isConnected) {
                startVpn();
            } else {
                stopVpn();
            }
        });
    }

    private void startVpn() {
        Intent intent = VpnService.prepare(this);
        if (intent != null) {
            startActivityForResult(intent, 100);
        } else {
            onVpnReady();
        }
    }

    private void onVpnReady() {
        Intent service = new Intent(this, VpnService.class);
        startService(service);
        isConnected = true;
        tvStatus.setText("Connected");
        tvStatus.setTextColor(0xFF4CAF50);
        btnConnect.setText("DISCONNECT");
    }

    private void stopVpn() {
        Intent service = new Intent(this, VpnService.class);
        stopService(service);
        isConnected = false;
        tvStatus.setText("Disconnected");
        tvStatus.setTextColor(0xFFF44336);
        btnConnect.setText("CONNECT");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            onVpnReady();
        }
    }
}
