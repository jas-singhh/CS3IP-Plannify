package uk.ac.aston.cs3ip.plannify.views.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import uk.ac.aston.cs3ip.plannify.databinding.FragmentHelpBinding;

public class HelpFragment extends Fragment {

    private FragmentHelpBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHelpBinding.inflate(inflater, container, false);

        initOnClickBack();

        return binding.getRoot();
    }

    /**
     * Navigates the user back to the previous page depending on which source page
     * called it.
     */
    private void initOnClickBack() {
        binding.helpBackBtn.setOnClickListener(v -> {
            // reference: https://developer.android.com/guide/navigation/navcontroller
            // navigate back to the previous destination
            NavHostFragment.findNavController(this).popBackStack();
        });
    }
}