package com.mastercoding.thriftly.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mastercoding.thriftly.MainActivity;
import com.mastercoding.thriftly.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        Log.d("FCM", "Message received: " + title + " - " + body);

        // Hiển thị thông báo cho người dùng
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channelId = "CustomerChannel";

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("navigate_to", 1);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // In ra log để kiểm tra giá trị của Extra
        Log.d("FCM", "Intent with navigate_to before startActivity: " + intent.getIntExtra("navigate_to", -1));

        // Khởi động MainActivity ngay lập tức
        startActivity(intent);

        // Tạo Notification Channel cho Android O trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Tạo và hiển thị thông báo
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_logoapp)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)  // Tự động hủy thông báo khi người dùng nhấn vào
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManager.notify(1, notification);
    }
}
