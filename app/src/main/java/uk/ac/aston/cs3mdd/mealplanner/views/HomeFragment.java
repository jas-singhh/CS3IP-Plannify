package uk.ac.aston.cs3mdd.mealplanner.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

import uk.ac.aston.cs3mdd.mealplanner.R;
import uk.ac.aston.cs3mdd.mealplanner.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    ContentMainHomeFragment contentMainHomeFragment = new ContentMainHomeFragment();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        // display the main home content page on launch
//        requireActivity().getSupportFragmentManager()
//                .beginTransaction().setReorderingAllowed(true)
//                .add(R.id.fragment_container_view, contentMainHomeFragment, "main")
//                .addToBackStack(null)
//                .commit();
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