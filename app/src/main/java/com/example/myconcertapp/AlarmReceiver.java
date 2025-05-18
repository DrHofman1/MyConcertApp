package com.example.myconcertapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String concertName = intent.getStringExtra("concertName");

        // Értesítési csatorna létrehozása, ha szükséges
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "concert_channel",
                    "Koncert értesítések",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // Értesítés létrehozása és megjelenítése
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "concert_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("🎵 Közelgő koncert!")
                .setContentText("Ne felejtsd: hamarosan kezdődik a(z) " + concertName)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}

