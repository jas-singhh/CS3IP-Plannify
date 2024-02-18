package uk.ac.aston.cs3mdd.mealplanner.notifications;

import static uk.ac.aston.cs3mdd.mealplanner.notifications.NotificationPublisher.CHANNEL_ID;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import java.util.Calendar;

import uk.ac.aston.cs3mdd.mealplanner.R;

public class NotificationHelper {

    private static AlarmManager alarmManager;

    public static void scheduleRTCNotification(Context context, Calendar calendar, PendingIntent pendingIntent) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );
    }

    /**
     * Create a notification channel to send all notifications on.
     * Reference: <a href="https://developer.android.com/develop/ui/views/notifications/channels">...</a>
     */
    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel
        CharSequence name = context.getString(R.string.channel_name);
        String description = "This is the description";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
