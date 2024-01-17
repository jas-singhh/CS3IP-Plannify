package uk.ac.aston.cs3mdd.mealplanner.views.my_meals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import uk.ac.aston.cs3mdd.mealplanner.databinding.FragmentMyMealsGroceryListBinding;


public class MyMealsGroceryListFragment extends Fragment {

    private FragmentMyMealsGroceryListBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyMealsGroceryListBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }
}