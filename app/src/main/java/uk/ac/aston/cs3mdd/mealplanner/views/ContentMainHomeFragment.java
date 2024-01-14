package uk.ac.aston.cs3mdd.mealplanner.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.ac.aston.cs3mdd.mealplanner.BuildConfig;
import uk.ac.aston.cs3mdd.mealplanner.MainActivity;
import uk.ac.aston.cs3mdd.mealplanner.R;
import uk.ac.aston.cs3mdd.mealplanner.adapters.HomeRecyclerViewAdapter;
import uk.ac.aston.cs3mdd.mealplanner.adapters.HomeRecyclerViewInterface;
import uk.ac.aston.cs3mdd.mealplanner.api.QuoteService;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Recipe;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.enums.EnumMealType;
import uk.ac.aston.cs3mdd.mealplanner.databinding.FragmentContentMainHomeBinding;
import uk.ac.aston.cs3mdd.mealplanner.repos.QuotesRepository;
import uk.ac.aston.cs3mdd.mealplanner.viewmodels.QuoteViewModel;
import uk.ac.aston.cs3mdd.mealplanner.viewmodels.RecipeViewModel;

public class ContentMainHomeFragment extends Fragment implements HomeRecyclerViewInterface {

    // Reference: CS3MDD Lab 2
    private QuoteViewModel quoteViewModel;
    private RecipeViewModel recipeViewModel;

    private DialogLottie animatedLoading;
    private HomeRecyclerViewAdapter mAdapter;

    private FragmentContentMainHomeBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialise variables
        animatedLoading = new DialogLottie(requireContext());
        mAdapter = new HomeRecyclerViewAdapter(this, new ArrayList<>());
        quoteViewModel = new ViewModelProvider(requireActivity()).get(QuoteViewModel.class);
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);

        // Perform Retrofit call to request the quote from the API
            requestQuote();
            requestBreakfastMeals();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentContentMainHomeBinding.inflate(inflater, container, false);

        animatedLoading.show();

        // setup UI elements
        setupChipGroupOnClickListeners();
        setupRecyclerView();
        setupOnClickForExpandMinimiseMotivationalQuote();

        // Observe the quote data requested from the API
        quoteViewModel.getQuote().observe(getViewLifecycleOwner(), quote -> binding.tvMotivationalQuote.setText(quote.getQuote()));

        recipeViewModel.getRequestedRecipes().observe(getViewLifecycleOwner(), recipeResponse -> {
            mAdapter.updateData(recipeResponse);
            animatedLoading.dismiss();
        });

        return binding.getRoot();
    }

    /**
     * Sets up the on click listener for the expand/minimise button in the
     * motivational quote container.
     */
    private void setupOnClickForExpandMinimiseMotivationalQuote() {
//        binding.motivationalQuoteExpandMinimise.setOnClickListener(click -> {
//            expandOrMinimiseMotivationalQuote();
//        });
    }

    /**
     * Sets up the recycler view with the appropriate layout manger
     * and sets its adapter.
     */
    private void setupRecyclerView() {
        binding.rvHomeMeals.setLayoutManager(new LinearLayoutManager(requireContext()));
        if (mAdapter != null) binding.rvHomeMeals.setAdapter(mAdapter);
    }

    /**
     * Sets up on click listeners for each chip in the chip group.
     */
    private void setupChipGroupOnClickListeners() {
        binding.chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            // show loading screen
            animatedLoading.show();

            // find the selected chip
            Chip selectedChip = null;
            for (Integer id : checkedIds) {
                selectedChip = group.findViewById(id);
            }

            assert selectedChip != null;
            requestMealsBasedOnSelectedChip(selectedChip);
        });
    }

    /**
     * Requests meals based on the selected chip.
     *
     * @param selectedChip selected chip for which meals are fetched.
     */
    private void requestMealsBasedOnSelectedChip(Chip selectedChip) {
        try {
            assert selectedChip != null;
            // get Enum value from the selected option
            String value = selectedChip.getText().toString().toUpperCase();
            value = value.replace("-", "_");
            EnumMealType mealType = EnumMealType.valueOf(value);

            // request the meals for the selected meal type
            recipeViewModel.requestRecipesByMealType(mealType);

        } catch (IllegalArgumentException e) {
            Log.e(MainActivity.TAG, e.toString());
        }
    }

    /**
     * Requests the motivational quote to display at launch.
     */
    private void requestQuote() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.NINJA_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        QuoteService request = retrofit.create(QuoteService.class);
        quoteViewModel.requestQuote(new QuotesRepository(request));
    }

    /**
     * Request breakfast meals to show at launch as breakfast
     * is the default choice in the chip group.
     */
    private void requestBreakfastMeals() {
        recipeViewModel.requestRecipesByMealType(EnumMealType.BREAKFAST);
        Log.d(MainActivity.TAG, "breakfast meals requested");
    }


    /**
     * Expands or minimises the motivational quote depending on whether
     * it is already visible or not.
     */
    private void expandOrMinimiseMotivationalQuote() {
//        if (binding.tvMotivationalQuote.getVisibility() == View.VISIBLE) {
//            TransitionManager.beginDelayedTransition(binding.cardContentContainer, new AutoTransition());
//            binding.tvMotivationalQuote.setVisibility(View.GONE);
//            binding.motivationalQuoteExpandMinimise.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_expand_more));
//        } else {
//            TransitionManager.beginDelayedTransition(binding.motivationalQuoteContainer, new AutoTransition());
//            binding.tvMotivationalQuote.setVisibility(View.VISIBLE);
//            binding.motivationalQuoteExpandMinimise.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_expand_less));
//        }
    }

    @Override
    public void onClickMeal(Recipe recipe) {
        // set arguments
        Bundle args = new Bundle();
        args.putSerializable("Recipe", recipe);
        MealDetailsFragment destinationFragment = new MealDetailsFragment();
        destinationFragment.setArguments(args);

        // navigate to the destination fragment
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view, destinationFragment, null)
                .setReorderingAllowed(true)
                .addToBackStack(null).
                commit();

    }
}