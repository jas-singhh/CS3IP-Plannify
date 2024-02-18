package uk.ac.aston.cs3mdd.mealplanner.notifications;

import static kotlin.random.RandomKt.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import uk.ac.aston.cs3mdd.mealplanner.MainActivity;
import uk.ac.aston.cs3mdd.mealplanner.R;
import uk.ac.aston.cs3mdd.mealplanner.enums.EnumMealType;
import uk.ac.aston.cs3mdd.mealplanner.models.local_recipe.LocalRecipe;
import uk.ac.aston.cs3mdd.mealplanner.room.AppDatabase;
import uk.ac.aston.cs3mdd.mealplanner.utils.Utilities;

public class NotificationPublisher extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 14;
    public static final String CHANNEL_ID = "channel14";
    public static final String TITLE = "titleExtra";

    // reference 1: https://stackoverflow.com/questions/34517520/how-to-give-notifications-on-android-on-specific-time
    // reference 2: https://developer.android.com/develop/background-work/services/alarms/schedule
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(MainActivity.TAG, "Notification Publisher called");

        rescheduleNotification(context, intent);

        Notification notification = buildNotification(context, intent);
        // unique id is used to make sure that old notifications do not get replaced from new ones
        int uniqueId = Random(System.currentTimeMillis()).nextInt(1000);


        EnumMealType mealType = EnumMealType.getEnumMealTypeFromValue(intent.getStringExtra(TITLE));
        if (mealType != null) uniqueId = mealType.hashCode();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(uniqueId, notification);
    }

    private void rescheduleNotification(Context context, Intent intent) {

        EnumMealType mealType = EnumMealType.getEnumMealTypeFromValue(intent.getStringExtra(TITLE));

        if (mealType != null) {
            Intent intentToRepeat = new Intent(context.getApplicationContext(), NotificationPublisher.class);
            intentToRepeat.putExtra(TITLE, mealType.getMealType());
            intentToRepeat.setAction("uk.ac.aston.cs3mdd.mealplanner.START_ALARM");

            int hour = Utilities.getTimeHourFromMealType(mealType);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.add(Calendar.DAY_OF_MONTH, 1);// set it for tomorrow
            cal.set(Calendar.HOUR_OF_DAY, hour);

            int requestCode = mealType.hashCode();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intentToRepeat, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationHelper.scheduleRTCNotification(context, cal, pendingIntent);
        }
    }

    /**
     * Creates a notification with the title specified in the intent and the message
     * based on whether there are pre-planned meals or not.
     *
     * @param context context required to build the notification.
     * @param intent intent which holds the title.
     * @return the notification with the appropriate title and message body.
     */
    @NonNull
    private Notification buildNotification(Context context, Intent intent) {

        String contentTitle = intent.getStringExtra(TITLE) + " Reminder";

        String message = getMessageBasedOnPlannedMeals(context, intent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(contentTitle)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // open the application when the user clicks on the notification
        // reference: https://stackoverflow.com/questions/13716723/open-application-after-clicking-on-notification
        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                NOTIFICATION_ID,
                new Intent(context, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        builder.setContentIntent(contentIntent);
        return builder.build();
    }


    /**
     * Checks whether there are pre-planned recipes for the specified meal type
     * and if there are, it builds a message with their title. If there are no
     * pre-planned meals, it builds a message with a reminder.
     *
     * @param context context required to access the database to check for pre-planned meals.
     * @param intent intent required to get the specified meal type for this notification.
     * @return a message reminding the user about their pre-planned meals, or a generic
     * reminder if there are no pre-planned meals.
     */
    private String getMessageBasedOnPlannedMeals(Context context, Intent intent) {

        StringBuilder message = new StringBuilder();
        EnumMealType mealType = EnumMealType.getEnumMealTypeFromValue(intent.getStringExtra(TITLE));
        if (mealType != null) {

            AppDatabase db = AppDatabase.getDatabase(context);
            List<LocalRecipe> savedRecipes = db.recipeDao().getRecipesOfTypeForDate(mealType, LocalDate.now()).blockingFirst();

            if (savedRecipes.isEmpty()) {
                // no pre-planned meals
                message = new StringBuilder("Looks like you haven't planned your ").append(intent.getStringExtra(TITLE));
            } else {
                // there are pre-planned meals

                message.append("Time to prepare your planned recipes: ").append("\n");

                for (int i = 0 ; i < savedRecipes.size(); i++) {
                    message.append(savedRecipes.get(i).getTitle());

                    // this ensures that the & sign is not added at the last element in the list
                    if (i != savedRecipes.size() - 1) {
                        message.append(" &\n");
                    }
                }
            }
        }

        return message.toString();
    }
}
