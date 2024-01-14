package uk.ac.aston.cs3mdd.mealplanner.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.zip.Inflater;

import uk.ac.aston.cs3mdd.mealplanner.R;
import uk.ac.aston.cs3mdd.mealplanner.adapters.MealDetailsRecyclerViewAdapter;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Ingredient;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Recipe;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.enums.EnumImageType;
import uk.ac.aston.cs3mdd.mealplanner.databinding.FragmentFindMealsBinding;
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

        displayMealDetails();

        binding.detailsIngredientsRv.setHasFixedSize(true);
        LinearLayoutManager rvLayout = new LinearLayoutManager(requireContext());
        rvLayout.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.detailsIngredientsRv.setLayoutManager(rvLayout);
        binding.detailsIngredientsRv.setAdapter(new MealDetailsRecyclerViewAdapter(selectedRecipe.getIngredients().toArray(new Ingredient[0])));

        return binding.getRoot();
    }

    private void displayMealDetails() {
        if (selectedRecipe == null) return;

        Picasso.get().load(selectedRecipe.getImage()).into(binding.headerImage);
        binding.detailsName.setText(Utilities.capitaliseString(selectedRecipe.getLabel()));
        binding.detailsCuisine.setText(Utilities.capitaliseString(selectedRecipe.getCuisineType().get(0)));
        binding.detailsDishType.setText(Utilities.capitaliseString(selectedRecipe.getDishType().get(0)));

        String servings = selectedRecipe.getYield() + " servings";
        binding.detailsServings.setText(servings);
    }

    private void displayIngredients() {

    }
}