package uk.ac.aston.cs3mdd.mealplanner.views.my_meals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayoutMediator;

import uk.ac.aston.cs3mdd.mealplanner.adapters.MyMealsViewPagerAdapter;
import uk.ac.aston.cs3mdd.mealplanner.databinding.FragmentMyMealsBinding;

public class MyMealsFragment extends Fragment {

    private FragmentMyMealsBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyMealsBinding.inflate(inflater, container, false);

        binding.viewPager.setAdapter(new MyMealsViewPagerAdapter(this));

        // reference: https://developer.android.com/guide/navigation/navigation-swipe-view-2
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                ((tab, position) -> {
                    if (position == 0) tab.setText("Saved Meals");
                    if (position == 1) tab.setText("Grocery List");
                })).attach();

        return binding.getRoot();
    }
}