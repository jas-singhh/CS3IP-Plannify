package uk.ac.aston.cs3ip.plannify.views.settings;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import uk.ac.aston.cs3ip.plannify.databinding.FragmentSettingsBinding;
import uk.ac.aston.cs3ip.plannify.notifications.NotificationHelper;
import uk.ac.aston.cs3ip.plannify.shared_prefs.SharedPreferencesManager;


public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    // reference: https://developer.android.com/training/permissions/requesting#java
    private ActivityResultLauncher<String> requestPermissionLauncher;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog. Save the return value, an instance of
        // ActivityResultLauncher, as an instance variable.
        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // Permission is granted. Continue the action or workflow in your
                        // app.
                        setNotificationsOnOrOff(true);
                    } else {
                        setNotificationsOnOrOff(false);
                        // do we need to display a rationale again?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.POST_NOTIFICATIONS))
                            displayNotificationsPermissionRationale();
                        else {
                            setNotificationsOnOrOff(false);
                            // functionality not available due to permissions
                            Snackbar.make(requireView(), "Please enable notification permissions from the settings", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);


        setupSwitches();

        initSwitchesListeners();
        initOnClickHelp();

        return binding.getRoot();
    }

    /**
     * Sets up the switches based on the saved settings stored in the shared
     * preferences.
     */
    private void setupSwitches() {
        // notifications toggle setup
        binding.switchNotifications.setChecked(SharedPreferencesManager.readBoolean(
                requireContext(), SharedPreferencesManager.ARE_NOTIFICATIONS_ENABLED
        ));

        // motivational quote toggle setup
        binding.switchMotivationalQuote.setChecked(!SharedPreferencesManager.readBoolean(
                requireContext(), SharedPreferencesManager.IS_MOTIVATIONAL_QUOTE_DISABLED
        ));

    }

    /**
     * Initialises the listeners for the switches to update the values stored
     * in the persistent storage through the shared preferences
     */
    private void initSwitchesListeners() {
        // notifications switch
        binding.switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // check for notifications permissions first
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {

                setNotificationsOnOrOff(isChecked);

            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.POST_NOTIFICATIONS)) {
                setNotificationsOnOrOff(false);

                displayNotificationsPermissionRationale();
            } else {
                setNotificationsOnOrOff(false);
                // The registered ActivityResultCallback gets the result of this request.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestNotificationsPermission();
                }
            }
        });


        // motivational quote switch
        binding.switchMotivationalQuote.setOnCheckedChangeListener((buttonView, isChecked) -> SharedPreferencesManager.writeBoolean(
                requireContext(),
                SharedPreferencesManager.IS_MOTIVATIONAL_QUOTE_DISABLED,
                !isChecked
        ));
    }

    /**
     * Sets up the notifications on or off depending on the provided value.
     *
     * @param on value based on which the notifications are turned on or off.
     */
    private void setNotificationsOnOrOff(boolean on) {
        binding.switchNotifications.setChecked(on);
        // update shared preferences
            SharedPreferencesManager.writeBoolean(
                    requireContext(),
                    SharedPreferencesManager.ARE_NOTIFICATIONS_ENABLED,
                    on
            );

            // start or stop alarms based on the checked state
            if (on) {
                NotificationHelper.startNotificationsIfNotAlreadySet(requireContext());
            } else {
                NotificationHelper.cancelAllNotifications(requireContext());
            }
    }

    /**
     * Displays a rationale explaining why the app requires notification permissions.
     * Asks for notification permissions again when the user selects OK.
     */
    private void displayNotificationsPermissionRationale() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("This app requires your notifications permissions to send you meal reminders");
        builder.setTitle("Notifications permission");
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            // request permission again
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationsPermission();
            }
        });

        builder.show();
    }

    /**
     * Requests the notifications permissions.
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestNotificationsPermission() {
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
    }

    private void initOnClickHelp() {
        binding.settingsHelpBtn.setOnClickListener(v -> {
            // reference: https://developer.android.com/guide/navigation/navcontroller
            NavHostFragment.findNavController(this).navigate(SettingsFragmentDirections.actionSettingsFragmentToHelpFragment());
        });
    }


}