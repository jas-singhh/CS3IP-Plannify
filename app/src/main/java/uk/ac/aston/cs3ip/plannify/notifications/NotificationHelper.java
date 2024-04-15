package uk.ac.aston.cs3ip.plannify.notifications;

import static uk.ac.aston.cs3ip.plannify.notifications.NotificationPublisher.TITLE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

import uk.ac.aston.cs3ip.plannify.enums.EnumMealType;
import uk.ac.aston.cs3ip.plannify.shared_prefs.SharedPreferencesManager;
import uk.ac.aston.cs3ip.plannify.utils.Utilities;

public class NotificationHelper {

    private static AlarmManager alarmManager;

    /**
     * Starts the notifications for each meal type to trigger at the specified
     * time using the alarm manager.
     *
     * @param context context required to start the alarm manager.
     */
    public static void startNotificationsIfNotAlreadySet(Context context) {

        // check if notifications have already been set
        // return if they have been already set up as we do not want to set them multiple times
        if (SharedPreferencesManager.readBoolean(context, SharedPreferencesManager.ARE_NOTIFICATIONS_SETUP)) return;

        for (EnumMealType mealType: EnumMealType.values()) {
            // set up the intent for the specific meal type
            Intent intent = new Intent(context, NotificationPublisher.class);
            intent.putExtra(TITLE, mealType.getMealType());
            intent.setAction("uk.ac.aston.cs3mdd.mealplanner.START_ALARM");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    mealType.hashCode(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );

            // set up the appropriate time for each meal notification
            int mealHour = Utilities.getTimeHourFromMealType(mealType);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

            // check if meal time has already passed
            // if meal time has passed - schedule it for the next day
            if (currentHour >= mealHour) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);// schedule starting from tomorrow
            }
            calendar.set(Calendar.HOUR_OF_DAY, mealHour);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            // schedule the notification
            NotificationHelper.scheduleRTCNotification(context, calendar, pendingIntent);
        }
    }

    /**
     * Schedules a Real-Time Clock notification for the specified time with the given pending intent.
     *
     * @param context context required to get a reference to the alarm manager.
     * @param calendar calendar containing the specified time and date for the notification.
     * @param pendingIntent pending intent containing the alarm intent for the notification.
     */
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
     * Cancels all the active notifications for each meal type.
     *
     * @param context context is required to cancel the alarm manager.
     */
    public static void cancelAllNotifications(Context context) {

        if (alarmManager == null) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            alarmManager.cancelAll();
        } else {
            for (EnumMealType mealType : EnumMealType.values()) {
                // set up the intent for the specific meal type
                Intent intent = new Intent(context, NotificationPublisher.class);
                intent.setAction("uk.ac.aston.cs3mdd.mealplanner.START_ALARM");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context,
                        mealType.hashCode(),
                        intent,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                );

                alarmManager.cancel(pendingIntent);
            }
        }

    }
}
