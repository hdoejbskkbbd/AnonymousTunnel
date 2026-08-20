package com.anonymous.tunnel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import androidx.core.app.NotificationCompat;

public class AnonymousVpnService extends VpnService {

    private static final int NOTIF_ID = 1;
    private static final String CHANNEL_ID = "vpn_channel";
    private ParcelFileDescriptor vpnInterface;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startVpn();
        return START_STICKY;
    }

    private void startVpn() {
        Builder builder = new Builder();
        builder.setSession("AnonymousTunnel")
               .addAddress("10.0.0.2", 24)
               .addDnsServer("1.1.1.1")
               .addRoute("0.0.0.0", 0);

        vpnInterface = builder.establish();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AnonymousTunnel")
            .setContentText("VPN Active")
            .setSmallIcon(android.R.drawable.ic_secure)
            .setContentIntent(pendingIntent)
            .build();

        startForeground(NOTIF_ID, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "VPN Service", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        if (vpnInterface != null) {
            try { vpnInterface.close(); } catch (Exception e) {}
        }
        super.onDestroy();
    }
}
