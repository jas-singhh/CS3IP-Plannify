package uk.ac.aston.cs3mdd.mealplanner;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.ac.aston.cs3mdd.mealplanner.databinding.FragmentHomeBinding;
import uk.ac.aston.cs3mdd.mealplanner.databinding.FragmentMyMealsBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyMealsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyMealsFragment extends Fragment {

    private FragmentMyMealsBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyMealsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}