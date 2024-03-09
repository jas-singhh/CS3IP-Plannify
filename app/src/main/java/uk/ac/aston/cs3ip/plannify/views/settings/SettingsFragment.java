package uk.ac.aston.cs3ip.plannify.views.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import uk.ac.aston.cs3ip.plannify.databinding.FragmentSettingsBinding;
import uk.ac.aston.cs3ip.plannify.notifications.NotificationHelper;
import uk.ac.aston.cs3ip.plannify.shared_prefs.SharedPreferencesManager;


public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
            // update shared preferences
            SharedPreferencesManager.writeBoolean(
                    requireContext(),
                    SharedPreferencesManager.ARE_NOTIFICATIONS_ENABLED,
                    isChecked
            );

            // start or stop alarms based on the checked state
            if (isChecked) {
                NotificationHelper.startNotificationsIfNotAlreadySet(requireContext());
            } else {
                NotificationHelper.cancelAllNotifications(requireContext());
            }
        });


        // motivational quote switch
        binding.switchMotivationalQuote.setOnCheckedChangeListener((buttonView, isChecked) -> SharedPreferencesManager.writeBoolean(
                requireContext(),
                SharedPreferencesManager.IS_MOTIVATIONAL_QUOTE_DISABLED,
                !isChecked
        ));
    }

    private void initOnClickHelp() {
        binding.settingsHelpBtn.setOnClickListener(v -> {
            // reference: https://developer.android.com/guide/navigation/navcontroller
            NavHostFragment.findNavController(this).navigate(SettingsFragmentDirections.actionSettingsFragmentToHelpFragment());
        });
    }


}