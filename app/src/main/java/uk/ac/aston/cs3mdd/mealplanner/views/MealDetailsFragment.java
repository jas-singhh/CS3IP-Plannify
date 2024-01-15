package uk.ac.aston.cs3mdd.mealplanner.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import uk.ac.aston.cs3mdd.mealplanner.adapters.MealDetailsIngredientsAdapter;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Ingredient;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Recipe;
import uk.ac.aston.cs3mdd.mealplanner.databinding.FragmentMealDetailsBinding;
import uk.ac.aston.cs3mdd.mealplanner.utils.Utilities;

public class MealDetailsFragment extends Fragment {

    private FragmentMealDetailsBinding binding;
    private Recipe selectedRecipe;

    private ConstraintLayout[] tabLayouts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // retrieve the received arguments
        assert getArguments() != null;
        selectedRecipe = (Recipe) getArguments().getSerializable("Recipe");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMealDetailsBinding.inflate(inflater, container, false);

        // on clicks
        onClickInstructions();
        onClickTutorials();
        onClickBack();

        // details setup
        displayMealMainAttributes();
        setupIngredients();
        setupNutrients();

        // store layouts' parents for easier switching in the order they appear
        tabLayouts = new ConstraintLayout[] {binding.ingredientsParent, binding.instructionsParent, binding.nutrientsParent};

        setupTabSwitching();

        return binding.getRoot();
    }


    /**
     * Handles the tab switches.
     */
    private void setupTabSwitching() {
        binding.detailsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                // show active tab and hide the rest
                for (int i = 0; i < tabLayouts.length; i++) {
                    if (i == tab.getPosition()) {
                        tabLayouts[i].setVisibility(View.VISIBLE);
                    } else {
                        tabLayouts[i].setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    /**
     * Displays the main three attributes of the meal, i.e. the meal cuisine,
     * the meal's health rating, and the meal's serving size.
     */
    private void displayMealMainAttributes() {
        if (selectedRecipe == null) return;

        Picasso.get().load(selectedRecipe.getImage()).into(binding.headerImage);
        binding.detailsName.setText(Utilities.capitaliseString(selectedRecipe.getLabel()));
        binding.detailsCuisine.setText(Utilities.capitaliseString(selectedRecipe.getCuisineType().get(0)));
        binding.detailsHealthRating.setText(Utilities.capitaliseString(Utilities.getMealHealthRating(selectedRecipe)));

        String servings = selectedRecipe.getYield() + " servings";
        binding.detailsServings.setText(servings);
    }


    /**
     * Sets up the recycler view to show the ingredients required for the meal.
     */
    private void setupIngredients() {
        binding.detailsIngredientsRv.setHasFixedSize(true);
        LinearLayoutManager rvLayout = new LinearLayoutManager(requireContext());
        rvLayout.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.detailsIngredientsRv.setLayoutManager(rvLayout);
        binding.detailsIngredientsRv.setAdapter(new MealDetailsIngredientsAdapter(selectedRecipe.getIngredients().toArray(new Ingredient[0])));
    }

    /**
     * Sets up the on click listener for the instructions button and navigates
     * the user to the meal's instructions' list in the device's browser.
     */
    private void onClickInstructions() {
        binding.btnDetailsInstructions.setOnClickListener(v -> {
            // open browser with the link of the recipe
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedRecipe.getUrl()));
            requireActivity().startActivity(browserIntent);
        });
    }


    /**
     * Sets up the on click listener for the tutorials button and navigates
     * the user to YouTube (if installed) or to the web browser to find
     * tutorials for the selected recipe.
     */
    private void onClickTutorials() {
        binding.btnDetailsTutorials.setOnClickListener(v -> {
//            Reference: https://stackoverflow.com/questions/9860456/search-a-specific-string-in-youtube-application-from-my-app
            Intent intent = new Intent(Intent.ACTION_SEARCH);
            intent.setPackage("com.google.android.youtube");
            intent.putExtra("query", selectedRecipe.getLabel());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            requireActivity().startActivity(intent);
        });
    }

    /**
     * Navigates the user back to the previous page in the stack.
     */
    private void onClickBack() {
        binding.btnBack.setOnClickListener(v -> {
            // reference: https://stackoverflow.com/questions/10863572/programmatically-go-back-to-the-previous-fragment-in-the-backstack
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    /**
     * Gets the list of nutrients for the given recipe and displays it in the layout.
     */
    private void setupNutrients() {
        binding.tvNutrients.setText(Utilities.getListOfNutrientsFromRecipe(selectedRecipe));
    }

}