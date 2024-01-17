package uk.ac.aston.cs3mdd.mealplanner.views.find_meals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import uk.ac.aston.cs3mdd.mealplanner.databinding.FragmentFindMealsBinding;

public class FindMealsFragment extends Fragment {

    private FragmentFindMealsBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFindMealsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}