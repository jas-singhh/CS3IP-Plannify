package uk.ac.aston.cs3mdd.mealplanner.views.find_meals;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import uk.ac.aston.cs3mdd.mealplanner.MainActivity;
import uk.ac.aston.cs3mdd.mealplanner.adapters.HomeMealsAdapter;
import uk.ac.aston.cs3mdd.mealplanner.adapters.HomeMealsOnClickInterface;
import uk.ac.aston.cs3mdd.mealplanner.databinding.FragmentFindMealsBinding;
import uk.ac.aston.cs3mdd.mealplanner.models.api_recipe.Recipe;
import uk.ac.aston.cs3mdd.mealplanner.viewmodels.FindMealsViewModel;
import uk.ac.aston.cs3mdd.mealplanner.views.dialogs.DialogFindMealsFilters;
import uk.ac.aston.cs3mdd.mealplanner.views.dialogs.DialogLottie;

public class FindMealsFragment extends Fragment implements HomeMealsOnClickInterface{

    private FragmentFindMealsBinding binding;
    private FindMealsViewModel mViewModel;
    private HomeMealsAdapter mAdapter;
    private DialogLottie animatedLoading;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.Factory.from(FindMealsViewModel.initializer)).get(FindMealsViewModel.class);
        mAdapter = new HomeMealsAdapter(this, new ArrayList<>());
        animatedLoading = new DialogLottie(requireActivity());
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFindMealsBinding.inflate(inflater, container, false);

        initRecyclerView();

        subscribeToViewModel();

        // on click listeners
        onClickFiltersButton();
        onRecipeSearch();

        return binding.getRoot();
    }


    private void setupAutoComplete() {
        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line);

        binding.customMealAutoCompleteSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                fetchAutoCompleteData(s.toString());
            }
        });
    }

    private void fetchAutoCompleteData(String value) {
        if (value.isEmpty()) return;


    }

    private void requestMeals(String query) {
        // request the meals with the specified params
        mViewModel.requestRecipesByQueryAndFilters(query);
    }

    /**
     * Sets up the recycler view with the appropriate layout manger
     * and sets its adapter.
     */
    private void initRecyclerView() {
        binding.rvFindMeals.setLayoutManager(new LinearLayoutManager(requireContext()));
        if (mAdapter != null) binding.rvFindMeals.setAdapter(mAdapter);
    }

    /**
     * Subscribes to changes in the requested recipes from the corresponding view model.
     */
    private void subscribeToViewModel() {
        mViewModel.getRequestedRecipes().observe(getViewLifecycleOwner(), recipeResponseList -> {
            if(animatedLoading != null) animatedLoading.dismiss();
            if(mAdapter != null) mAdapter.updateData((ArrayList<? extends Recipe>) recipeResponseList.getResults());
        });
    }

    /**
     * Sets up the on click listener for the filters button to open a new filters
     * dialog.
     */
    private void onClickFiltersButton() {
        binding.findMealsFilters.setOnClickListener(v -> new DialogFindMealsFilters(requireContext(), (MainActivity) requireActivity()));
    }

    private void onRecipeSearch() {

        // reference: https://stackoverflow.com/questions/3205339/android-how-to-make-keyboard-enter-button-say-search-and-handle-its-click
        binding.customMealAutoCompleteSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                animatedLoading.show();
                // user clicked search
                requestMeals(v.getText().toString());
                return true;
            }
            return false;
        });
    }

    @Override
    public void onClickMeal(Recipe recipe) {

    }
}