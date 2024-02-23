package uk.ac.aston.cs3ip.plannify.views.find_meals;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import uk.ac.aston.cs3ip.plannify.MainActivity;
import uk.ac.aston.cs3ip.plannify.adapters.HomeMealsAdapter;
import uk.ac.aston.cs3ip.plannify.adapters.HomeMealsOnClickInterface;
import uk.ac.aston.cs3ip.plannify.databinding.FragmentFindMealsBinding;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.AutoCompleteResult;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.Recipe;
import uk.ac.aston.cs3ip.plannify.viewmodels.FindMealsViewModel;
import uk.ac.aston.cs3ip.plannify.views.dialogs.DialogFindMealsFilters;
import uk.ac.aston.cs3ip.plannify.views.dialogs.DialogLottie;

public class FindMealsFragment extends Fragment implements HomeMealsOnClickInterface {

    private FragmentFindMealsBinding binding;
    private FindMealsViewModel mViewModel;
    private HomeMealsAdapter mAdapter;
    private DialogLottie animatedLoading;
    private ArrayAdapter<String> autoCompleteAdapter;

    private int autoCompleteTextCounter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.Factory.from(FindMealsViewModel.initializer)).get(FindMealsViewModel.class);
        mAdapter = new HomeMealsAdapter(this, new ArrayList<>());
        animatedLoading = new DialogLottie(requireActivity());
        autoCompleteTextCounter = 0;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFindMealsBinding.inflate(inflater, container, false);

        initRecyclerView();
        initAutoComplete();

        requestRandomHealthyMeals();

        subscribeToSearchedRecipes();
        subscribeToRandomHealthyRecipes();
        subscribeToRequestedAutoCompleteResults();

        // on click listeners
        onClickFiltersButton();
        onRecipeSearch();
        onClickClearResults();

        return binding.getRoot();
    }

    /**
     * Requests random healthy meals only if the user has not yet searched
     * for a meal.
     */
    private void requestRandomHealthyMeals() {
        if (mViewModel.getRequestedRecipes().getValue() == null) {
            String title = "Top Suggested";
            binding.tvFindMealsResults.setText(title);

            // request the data only if we do not already have it stored in the view model
            if (mViewModel.getRandomHealthyRecipes().getValue() == null)
                mViewModel.requestRandomHealthyRecipes();
            else {
                if (mAdapter != null)
                    mAdapter.updateData((ArrayList<? extends Recipe>) mViewModel.getRandomHealthyRecipes().getValue().getResults());
            }
        }
    }

    private void requestSearchedRecipes(String query) {
        // request the meals with the specified params
        String resultsTitle = "Results";
        binding.tvFindMealsResults.setText(resultsTitle);

        // display clear button
        binding.textBtnClearResults.setVisibility(View.VISIBLE);

        mViewModel.requestRecipesByQueryAndFilters(query);
    }

    /**
     * Sets up the recycler view with the appropriate layout manger
     * and the corresponding adapter.
     */
    private void initRecyclerView() {
        // search results recycler view
        binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        if (mAdapter != null) binding.rvSearchResults.setAdapter(mAdapter);
    }


    /**
     * Sets up the auto complete functionality by initialising the adapter and
     * fetching results once the user types something in the edit box, only if
     * the length of the types word is longer than 2.
     */
    private void initAutoComplete() {
        autoCompleteAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        binding.customMealAutoCompleteSearch.setAdapter(autoCompleteAdapter);


        binding.customMealAutoCompleteSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // waits for user to type 3 letters after the length is higher than 3
                // to save api calls.

                autoCompleteTextCounter++;

                if (autoCompleteTextCounter % 3 == 0) {
                    autoCompleteTextCounter = 0;
                    fetchAutoCompleteData(s.toString());
                }
            }
        });
    }

    /**
     * Requests the auto complete data with the given query.
     *
     * @param query query for which to fetch auto complete data.
     */
    private void fetchAutoCompleteData(String query) {
        if (query.isEmpty()) return;

        mViewModel.requestAutoCompleteForQuery(query);
    }

    /**
     * Subscribes to changes in the requested recipes live data.
     */
    private void subscribeToSearchedRecipes() {
        mViewModel.getRequestedRecipes().observe(getViewLifecycleOwner(), recipeResponseList -> {
            if (mAdapter != null && recipeResponseList != null) {

                if (recipeResponseList.getResults().isEmpty()) {
                    // show no results message
                    binding.noResultsMessageParent.setVisibility(View.VISIBLE);

                    mAdapter.clearData();
                } else {
                    // hide no results message in case it is visible
                    binding.noResultsMessageParent.setVisibility(View.GONE);

                    mAdapter.updateData((ArrayList<? extends Recipe>) recipeResponseList.getResults());
                }

            }

            if (animatedLoading != null) animatedLoading.dismiss();
        });
    }

    /**
     * Subscribes to changes in the requested auto complete results live data for the specific query
     * and updates the edit box's adapter to display the fetched values.
     */
    private void subscribeToRequestedAutoCompleteResults() {
        mViewModel.getAutoCompleteResult().observe(getViewLifecycleOwner(), autoCompleteResult -> {
            if (autoCompleteAdapter != null) {
                for (AutoCompleteResult autoCompleteItem : autoCompleteResult) {
                    autoCompleteAdapter.add(autoCompleteItem.getTitle());
                }
                autoCompleteAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Subscribes to changes in the random healthy meals live data.
     */
    private void subscribeToRandomHealthyRecipes() {
        mViewModel.getRandomHealthyRecipes().observe(getViewLifecycleOwner(), recipeResponseList -> {
            if (mAdapter != null && mViewModel.getRequestedRecipes().getValue() == null) {
                mAdapter.updateData((ArrayList<? extends Recipe>) recipeResponseList.getResults());
            }
        });
    }

    /**
     * Sets up the on click listener for the filters button to open a new filters
     * dialog.
     */
    private void onClickFiltersButton() {
        binding.findMealsFilters.setOnClickListener(v -> new DialogFindMealsFilters(requireContext(), (MainActivity) requireActivity()));
    }

    /**
     * Requests recipes for the specified query by the user once the search button
     * in the keyboard is pressed.
     */
    private void onRecipeSearch() {

        // reference: https://stackoverflow.com/questions/3205339/android-how-to-make-keyboard-enter-button-say-search-and-handle-its-click
        binding.customMealAutoCompleteSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                animatedLoading.show();
                // user clicked search
                requestSearchedRecipes(v.getText().toString());
                return true;
            }
            return false;
        });
    }

    /**
     * Sets up the on click listener for the clear text button to clear the searched
     * results and display the suggested meals again.
     */
    private void onClickClearResults() {
        binding.textBtnClearResults.setOnClickListener(v -> {

            // clear search results
            if (mViewModel.getRequestedRecipes().getValue() != null) {
                mViewModel.setRequestedRecipes(null);
            }

            // change title heading
            String suggestedTitle = "Top Suggested";
            binding.tvFindMealsResults.setText(suggestedTitle);

            // clear the edit text
            binding.customMealAutoCompleteSearch.setText("");

            // display top suggested data again
            if (mViewModel.getRandomHealthyRecipes().getValue() != null) {
                // we already have the data
                if (mAdapter != null) {
                    mAdapter.updateData((ArrayList<? extends Recipe>)
                            mViewModel.getRandomHealthyRecipes().getValue().getResults());
                    // hide the no results parent if it is visible
                    if (binding.noResultsMessageParent.getVisibility() == View.VISIBLE)
                        binding.noResultsMessageParent.setVisibility(View.GONE);
                }
            } else {
                // we do not already have the data, so request new one
                requestRandomHealthyMeals();
            }

            // hide button itself
            binding.textBtnClearResults.setVisibility(View.GONE);

        });
    }

    @Override
    public void onClickMeal(Recipe recipe) {
        FindMealsFragmentDirections.ActionFindMealsFragmentToMealDetailsFragment action =
                FindMealsFragmentDirections.actionFindMealsFragmentToMealDetailsFragment(recipe);

        // reference: https://developer.android.com/guide/navigation/navcontroller
        NavHostFragment.findNavController(this).navigate(action);

    }
}