package uk.ac.aston.cs3mdd.mealplanner.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

import com.squareup.picasso.Picasso;

import uk.ac.aston.cs3mdd.mealplanner.adapters.MealDetailsRecyclerViewAdapter;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Ingredient;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Recipe;
import uk.ac.aston.cs3mdd.mealplanner.databinding.FragmentMealDetailsBinding;
import uk.ac.aston.cs3mdd.mealplanner.utils.Utilities;

public class MealDetailsFragment extends Fragment {

    private FragmentMealDetailsBinding binding;
    private Recipe selectedRecipe;

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

        onClickInstructions();

        displayMealMainAttributes();
        displayIngredients();

        return binding.getRoot();
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
    private void displayIngredients() {
        binding.detailsIngredientsRv.setHasFixedSize(true);
        LinearLayoutManager rvLayout = new LinearLayoutManager(requireContext());
        rvLayout.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.detailsIngredientsRv.setLayoutManager(rvLayout);
        binding.detailsIngredientsRv.setAdapter(new MealDetailsRecyclerViewAdapter(selectedRecipe.getIngredients().toArray(new Ingredient[0])));
    }

    private void onClickInstructions() {
        binding.btnDetailsInstructions.setOnClickListener(v -> {
            // open browser with the link of the recipe
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedRecipe.getUrl()));
            requireActivity().startActivity(browserIntent);
        });
    }



}