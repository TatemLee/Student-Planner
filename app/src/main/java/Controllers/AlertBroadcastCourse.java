package Controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.studentplanner.R;

public class AlertBroadcastCourse extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyCourseStartTime")
                .setSmallIcon(R.drawable.ic_alert_enabled_icon)
                .setContentTitle("Course Reminder: ")
                .setContentText(intent.getStringExtra("TEXT"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        //set vibration
        builder.setVibrate(new long [] {1000, 1000});
        builder.setLights(Color.RED, 3000, 3000);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(intent.getIntExtra("ID", 0), builder.build());
    }
}
