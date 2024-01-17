package uk.ac.aston.cs3mdd.mealplanner.views.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import uk.ac.aston.cs3mdd.mealplanner.R;
import uk.ac.aston.cs3mdd.mealplanner.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Reference: https://stackoverflow.com/questions/22713128/how-can-i-switch-between-two-fragments-without-recreating-the-fragments-each-ti
        if (getChildFragmentManager().findFragmentById(R.id.fragment_container_view) == null) {
            // add the fragment only if the fragment container is empty
            ContentMainHomeFragment homeContentFragment = new ContentMainHomeFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_view, homeContentFragment)
                    .commit();
        }

        return binding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}