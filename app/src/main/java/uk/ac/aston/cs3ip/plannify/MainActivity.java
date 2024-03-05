package uk.ac.aston.cs3ip.plannify;

import static uk.ac.aston.cs3ip.plannify.notifications.NotificationPublisher.CHANNEL_ID;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import uk.ac.aston.cs3ip.plannify.databinding.ActivityMainBinding;
import uk.ac.aston.cs3ip.plannify.notifications.NotificationHelper;
import uk.ac.aston.cs3ip.plannify.shared_prefs.SharedPreferencesManager;
import uk.ac.aston.cs3ip.plannify.views.dialogs.DialogGetNotified;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static final String TAG = "WMK";
    public static int INTRO_DELAY_MS = 1000;
    public static final int RC_NOTIFICATIONS = 14;

    private CompositeDisposable mDisposable;
    private DialogGetNotified notificationsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // handle splash screen transitions
        // reference: https://www.youtube.com/watch?v=dsstwSpF_ik&ab_channel=TechPoty
        SplashScreen.installSplashScreen(this);


        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // initialisation
        initVariables();
        initNavigation();
        initFab();

        // set up the notifications if they are enabled but not set
        if (areNotificationsEnabled()) {
            startNotificationsIfNotAlreadySet();
        }

        // show notification dialog only on first access
        showNotificationsDialogOnFirstAccess();

        // Recreating an existing notification channel with its original values performs no operation,
        // so it's safe to call this code when starting an app.
        // reference: https://developer.android.com/develop/ui/views/notifications/channels
        createNotificationChannel();
    }

    private void initVariables() {
        mDisposable = new CompositeDisposable();
    }

    /**
     * Displays the notifications dialog if the user is accessing the application
     * for the first time.
     */
    private void showNotificationsDialogOnFirstAccess() {
        if (!SharedPreferencesManager.readBoolean(MainActivity.this,
                SharedPreferencesManager.IS_NOT_FIRST_ACCESS)) {
            // it is first time access - update value
            SharedPreferencesManager.writeBoolean(MainActivity.this,
                    SharedPreferencesManager.IS_NOT_FIRST_ACCESS, true);

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
        return SharedPreferencesManager.readBoolean(
                MainActivity.this,
                SharedPreferencesManager.ARE_NOTIFICATIONS_ENABLED
        );
    }

    /**
     * Checks if the notifications have already been set up.
     *
     * @return true if they are set up, false otherwise.
     */
    private boolean areNotificationsSetup() {
        return SharedPreferencesManager.readBoolean(
                MainActivity.this,
                SharedPreferencesManager.ARE_NOTIFICATIONS_SETUP
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RC_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "permission granted");

                // notifications permission granted
                // so update the shared preferences value
                SharedPreferencesManager.writeBoolean(MainActivity.this,
                        SharedPreferencesManager.ARE_NOTIFICATIONS_ENABLED, true);

                // setup the notifications as the user granted the permission
                startNotificationsIfNotAlreadySet();
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

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

            // reference 1: https://stackoverflow.com/questions/74721348/problem-to-navigate-between-fragments-in-navbar
            // reference 2: https://stackoverflow.com/questions/71089052/android-navigation-component-bottomnavigationviews-selected-tab-icon-is-not-u
            // this prevents an issue where when the user is viewing a nested fragment inside another
            // fragment, and navigates to another fragment through the nav menu and comes back to the same fragment
            // with the nested fragment opened in it, then the menu selection does not update.

            // issue explanation example
            // user opens the home fragment -> clicks on a meal and the meal details fragment is opened
            // user navigates to a different fragment e.g. settings.
            // user comes back to the home fragment which had meal details fragment opened.
            // the meal details fragment is displayed, however the menu still indicates that the active fragment is settings when it should indicate home
            binding.bottomNavigation.setOnItemSelectedListener(item -> {
                NavigationUI.onNavDestinationSelected(item, navController);
                return true;
            });
        }
    }

    /**
     * Sets up the on click listener for the floating action button to open a dialog
     * box to add a custom meal.
     */
    private void initFab() {
//        findViewById(R.id.fab).setOnClickListener(v -> new DialogCustomMeal(MainActivity.this, localRecipe -> {
//            // save recipe once the user clicks on save
//            mDisposable.add(homeViewModel.insert(localRecipe)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(() -> Toast.makeText(this, "Recipe Saved", Toast.LENGTH_LONG).show()
//                            , throwable -> Utilities.showErrorToast(this)));
//        }));

        findViewById(R.id.fab).setOnClickListener(v -> {
            // global action - reference: https://developer.android.com/guide/navigation/design/global-action
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if (navHostFragment != null) {
                navHostFragment.getNavController().navigate(NavGraphDirections.actionGlobalCreateRecipeFragment());

            }

        });
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

    private void startNotificationsIfNotAlreadySet() {
        NotificationHelper.startNotificationsIfNotAlreadySet(MainActivity.this);
        // update the shared preferences to indicate that the notifications have been set
        SharedPreferencesManager.writeBoolean(MainActivity.this,
                SharedPreferencesManager.ARE_NOTIFICATIONS_SETUP, true);
    }

//    private void setupNotificationsIfNotAlreadySet() {
//
//        // check if notifications have already been set
//        // return if they have been already set up as we do not want to set them multiple times
//        if (SharedPreferencesManager.readBoolean(MainActivity.this, SharedPreferencesManager.ARE_NOTIFICATIONS_SETUP)) return;
//
//        for (EnumMealType mealType: EnumMealType.values()) {
//            // set up the intent for the specific meal type
//            Intent intent = new Intent(MainActivity.this, NotificationPublisher.class);
//            intent.putExtra(TITLE, mealType.getMealType());
//            intent.setAction("uk.ac.aston.cs3mdd.mealplanner.START_ALARM");
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                    MainActivity.this,
//                    mealType.hashCode(),
//                    intent,
//                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
//            );
//
//            // set up the appropriate time for each meal notification
//            int mealHour = Utilities.getTimeHourFromMealType(mealType);
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(System.currentTimeMillis());
//            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
//
//            // check if meal time has already passed
//            // if meal time has passed - schedule it for the next day
//            if (currentHour >= mealHour) {
//                calendar.add(Calendar.DAY_OF_MONTH, 1);// schedule starting from tomorrow
//            }
//            calendar.set(Calendar.HOUR_OF_DAY, mealHour);
//            calendar.set(Calendar.MINUTE, 0);
//            calendar.set(Calendar.SECOND, 0);
//
//            // schedule the notification
//            NotificationHelper.scheduleRTCNotification(MainActivity.this, calendar, pendingIntent);
//        }
//
//        // update the shared preferences to indicate that the notifications have been set
//        SharedPreferencesManager.writeBoolean(MainActivity.this,
//                SharedPreferencesManager.ARE_NOTIFICATIONS_SETUP, true);
//}

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