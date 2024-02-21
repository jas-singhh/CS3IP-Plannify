package uk.ac.aston.cs3mdd.mealplanner;

import static uk.ac.aston.cs3mdd.mealplanner.notifications.NotificationPublisher.CHANNEL_ID;
import static uk.ac.aston.cs3mdd.mealplanner.notifications.NotificationPublisher.TITLE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import java.util.Calendar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import uk.ac.aston.cs3mdd.mealplanner.databinding.ActivityMainBinding;
import uk.ac.aston.cs3mdd.mealplanner.enums.EnumMealType;
import uk.ac.aston.cs3mdd.mealplanner.notifications.NotificationHelper;
import uk.ac.aston.cs3mdd.mealplanner.notifications.NotificationPublisher;
import uk.ac.aston.cs3mdd.mealplanner.utils.Utilities;
import uk.ac.aston.cs3mdd.mealplanner.viewmodels.RecipeViewModel;
import uk.ac.aston.cs3mdd.mealplanner.views.dialogs.DialogCustomMeal;
import uk.ac.aston.cs3mdd.mealplanner.views.dialogs.DialogGetNotified;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static final String TAG = "WMK";
    public static final int RC_NOTIFICATIONS = 14;

    private RecipeViewModel recipeViewModel;
    private CompositeDisposable mDisposable;
    private SharedPreferences mPreferences;
    private DialogGetNotified notificationsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // initialisation
        initVariables();
        initNavigation();
        initFab();

        // show notification dialog only on first access
        showNotificationsDialogOnFirstAccess();

        // Recreating an existing notification channel with its original values performs no operation,
        // so it's safe to call this code when starting an app.
        // reference: https://developer.android.com/develop/ui/views/notifications/channels
        createNotificationChannel();
    }

    private void initVariables() {
        mPreferences = getPreferences(Context.MODE_PRIVATE);
        recipeViewModel = new ViewModelProvider(this,
                ViewModelProvider.Factory.from(RecipeViewModel.initializer)).get(RecipeViewModel.class);
        mDisposable = new CompositeDisposable();
    }

    /**
     * Displays the notifications dialog if the user is accessing the application
     * for the first time.
     */
    private void showNotificationsDialogOnFirstAccess() {
        if (mPreferences.getBoolean("firstAccess", true)) {
            // it is first time access - update value
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean("firstAccess", false);
            editor.apply();

            // display notifications dialog
            notificationsDialog = new DialogGetNotified(MainActivity.this);
        }
    }

    /**
     * Checks if the notifications have been enabled by the user.
     *
     * @return true if the notifications are enabled, false otherwise.
     */
    private boolean areNotificationsEnabled() {
        return mPreferences.getBoolean("notificationsEnabled", false);
    }

    /**
     * Checks if the notifications have already been set up.
     *
     * @return true if they are set up, false otherwise.
     */
    private boolean areNotificationsSetup() {
        return mPreferences.getBoolean("areNotificationsSetup", false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RC_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "permission granted");

                // notifications permission granted
                // so update the shared preferences value
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("notificationsEnabled", true);
                editor.apply();

                // setup the notifications as the user granted the permission
                setupNotificationsIfNotAlreadySet();
            } else {
                Log.d(TAG, "permission not granted");

                // notifications permission not granted
                // notificationsEnabled shared preference is already false by default
                // so there is no need to update it here
            }

            if (notificationsDialog != null) notificationsDialog.dismiss();
        }
    }

    /**
     * Sets up the navigation bar.
     */
    private void initNavigation() {
        // Reference: https://stackoverflow.com/questions/65170700/activity-does-not-have-a-navcontroller-set-on
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }

    /**
     * Sets up the on click listener for the floating action button to open a dialog
     * box to add a custom meal.
     */
    private void initFab() {
        findViewById(R.id.fab).setOnClickListener(v -> new DialogCustomMeal(MainActivity.this, localRecipe -> {
            // save recipe once the user clicks on save
            mDisposable.add(recipeViewModel.insert(localRecipe)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> Toast.makeText(this, "Recipe Saved", Toast.LENGTH_LONG).show()
                            , throwable -> Utilities.showErrorToast(this)));
        }));
    }

    /**
     * Create a notification channel to send all notifications on.
     * Reference: <a href="https://developer.android.com/develop/ui/views/notifications/channels">...</a>
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel
        CharSequence name = getString(R.string.channel_name);
        String description = "This channel provides reminders for each meal";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void setupNotificationsIfNotAlreadySet() {

        // check if notifications have already been set
        // return if they have been already set up as we do not want to set them multiple times
        if (mPreferences.getBoolean("areNotificationsSetup", false)) return;


        for (EnumMealType mealType: EnumMealType.values()) {
            // set up the intent for the specific meal type
            Intent intent = new Intent(MainActivity.this, NotificationPublisher.class);
            intent.putExtra(TITLE, mealType.getMealType());
            intent.setAction("uk.ac.aston.cs3mdd.mealplanner.START_ALARM");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    MainActivity.this,
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
            NotificationHelper.scheduleRTCNotification(MainActivity.this, calendar, pendingIntent);
        }

        // update the shared preferences to indicate that the notifications have been set
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean("areNotificationsSetup", true);
        editor.apply();
}

    @Override
    protected void onStop() {
        super.onStop();
        mDisposable.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
    }
}