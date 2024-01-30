package uk.ac.aston.cs3mdd.mealplanner.views.meal_details;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import uk.ac.aston.cs3mdd.mealplanner.R;
import uk.ac.aston.cs3mdd.mealplanner.adapters.MealDetailsIngredientsAdapter;
import uk.ac.aston.cs3mdd.mealplanner.databinding.FragmentMealDetailsBinding;
import uk.ac.aston.cs3mdd.mealplanner.models.api_recipe.ExtendedIngredient;
import uk.ac.aston.cs3mdd.mealplanner.models.api_recipe.Recipe;
import uk.ac.aston.cs3mdd.mealplanner.models.api_recipe.Step;
import uk.ac.aston.cs3mdd.mealplanner.models.local_recipe.LocalRecipe;
import uk.ac.aston.cs3mdd.mealplanner.utils.Utilities;
import uk.ac.aston.cs3mdd.mealplanner.viewmodels.RecipeViewModel;
import uk.ac.aston.cs3mdd.mealplanner.views.dialogs.DialogSaveRecipe;

public class MealDetailsFragment extends Fragment {


    private FragmentMealDetailsBinding binding;
    private Recipe selectedRecipe;
    private String source;

    private ConstraintLayout[] tabLayouts;
    private RecipeViewModel recipeViewModel;
    private CompositeDisposable mDisposable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialisation
        recipeViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.Factory.from(RecipeViewModel.initializer)).get(RecipeViewModel.class);
        mDisposable = new CompositeDisposable();

        // retrieve the received arguments
        assert getArguments() != null;
        selectedRecipe = (uk.ac.aston.cs3mdd.mealplanner.models.api_recipe.Recipe) getArguments().getSerializable("Recipe");
        source = getArguments().getString("Source");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMealDetailsBinding.inflate(inflater, container, false);

        // on clicks
        onClickTutorials();
        onClickBack();
        onClickSave();

        // details setup
        displayMealMainAttributes();
        initIngredients();
        initInstructions();
        initNutrients();

        // store layouts' parents for easier switching in the order they appear
        tabLayouts = new ConstraintLayout[]{binding.ingredientsParent, binding.instructionsParent, binding.nutrientsParent};
        initTabSwitching();

        return binding.getRoot();
    }


    /**
     * Handles the tab switches.
     */
    private void initTabSwitching() {
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

        String time = selectedRecipe.getReadyInMinutes() + "m";

        Picasso.get().load(selectedRecipe.getImage()).into(binding.headerImage);
        binding.detailsName.setText(Utilities.capitaliseString(selectedRecipe.getTitle()));
        binding.detailsTime.setText(time);
        binding.detailsHealthRating.setText(Utilities.capitaliseString(Utilities.getMealHealthRating(selectedRecipe)));
        String servings = selectedRecipe.getServings() + " servings";
        binding.detailsServings.setText(servings);
    }


    /**
     * Sets up the recycler view to show the ingredients required for the meal.
     */
    private void initIngredients() {
        binding.detailsIngredientsRv.setHasFixedSize(true);
        LinearLayoutManager rvLayout = new LinearLayoutManager(requireContext());
        rvLayout.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.detailsIngredientsRv.setLayoutManager(rvLayout);
        if (selectedRecipe.getExtendedIngredients() != null)
            binding.detailsIngredientsRv.setAdapter(new MealDetailsIngredientsAdapter(selectedRecipe.getExtendedIngredients().toArray(new ExtendedIngredient[0])));
    }

    /**
     * Sets up the instructions for the specific recipe.
     */
    private void initInstructions() {
        if (selectedRecipe.getAnalyzedInstructions() == null) return;
        if (selectedRecipe.getAnalyzedInstructions().isEmpty() ||
                selectedRecipe.getAnalyzedInstructions().get(0).getSteps() == null) return;

        List<Step> steps = selectedRecipe.getAnalyzedInstructions().get(0).getSteps();
        StringBuilder mealInstructions = new StringBuilder();
        for (Step step : steps) {
            mealInstructions.append(step.getNumber()).append(". ").append(step.getStep()).append("\n");
        }
        binding.mealDetailsInstructions.setText(mealInstructions.toString());
    }


    /**
     * Gets the list of nutrients for the given recipe and displays it in the layout.
     */
    private void initNutrients() {
//        binding.tvNutrients.setText(Utilities.getListOfNutrientsFromRecipe(selectedRecipe));

    }


    /**
     * Sets up the on click listener for the tutorials button and navigates
     * the user to YouTube (if installed) or to the web browser to find
     * tutorials for the selected recipe.
     */
    private void onClickTutorials() {
        binding.btnDetailsTutorials.setOnClickListener(v -> {
            // reference: https://stackoverflow.com/questions/9860456/search-a-specific-string-in-youtube-application-from-my-app
            Intent intent = new Intent(Intent.ACTION_SEARCH);
            intent.setPackage("com.google.android.youtube");
            intent.putExtra("query", selectedRecipe.getTitle());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            requireActivity().startActivity(intent);
        });
    }

    /**
     * Navigates the user back to the previous page depending on which source
     * called it in the first place.
     */
    private void onClickBack() {
        binding.btnBack.setOnClickListener(v -> {
            // reference: https://stackoverflow.com/questions/10863572/programmatically-go-back-to-the-previous-fragment-in-the-backstack
            if (source.equals("my_meals_saved_fragment"))
                requireActivity().getSupportFragmentManager().popBackStack("my_meals_saved_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            if (source.equals("content_main_home_fragment"))
                requireActivity().getSupportFragmentManager().popBackStack("content_main_home_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });
    }

    /**
     * Saves or removes the workout in the Room Database depending on
     * whether it is already saved or not.
     */
    private void onClickSave() {
        binding.btnSave.setOnClickListener(v -> {

            // show the dialog box for the date and meal type
            new DialogSaveRecipe(requireContext(), (date, mealType) -> {
                LocalRecipe recipeToSave = new LocalRecipe(selectedRecipe, date, mealType);

                mDisposable.add(recipeViewModel.insert(recipeToSave)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            Toast.makeText(requireContext(), "Recipe Saved", Toast.LENGTH_LONG).show();
                            binding.bntSaveImg.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_remove));
                        }, throwable -> Utilities.showErrorToast(requireContext())));
            });
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mDisposable.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
    }
}