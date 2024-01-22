package uk.ac.aston.cs3mdd.mealplanner.views.meal_details;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import uk.ac.aston.cs3mdd.mealplanner.R;
import uk.ac.aston.cs3mdd.mealplanner.adapters.MealDetailsIngredientsAdapter;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Ingredient;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Recipe;
import uk.ac.aston.cs3mdd.mealplanner.databinding.FragmentMealDetailsBinding;
import uk.ac.aston.cs3mdd.mealplanner.utils.Utilities;
import uk.ac.aston.cs3mdd.mealplanner.viewmodels.RecipeViewModel;

public class MealDetailsFragment extends Fragment {

    private FragmentMealDetailsBinding binding;
    private Recipe selectedRecipe;

    private ConstraintLayout[] tabLayouts;
    private RecipeViewModel recipeViewModel;
    private CompositeDisposable mDisposable;
    private boolean isRecipeAlreadySaved;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialisation
        recipeViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.Factory.from(RecipeViewModel.initializer)).get(RecipeViewModel.class);
        mDisposable = new CompositeDisposable();
        isRecipeAlreadySaved = false;

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
        onClickSaveOrDelete();

        // details setup
        displayMealMainAttributes();
        setupIngredients();
        setupNutrients();

        // store layouts' parents for easier switching in the order they appear
        tabLayouts = new ConstraintLayout[]{binding.ingredientsParent, binding.instructionsParent, binding.nutrientsParent};
        setupTabSwitching();

        checkIfRecipeIsAlreadySaved();

        return binding.getRoot();
    }

    /**
     * Checks whether this particular recipe is already saved in the database
     * and updates the layout accordingly.
     */
    private void checkIfRecipeIsAlreadySaved() {
        mDisposable.add(recipeViewModel.existsByUri(selectedRecipe.getUri())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result != 0) {
                        // recipe is already saved
                        isRecipeAlreadySaved = true;
                        binding.bntSaveImg.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_remove));
                }
                }));
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

//        Picasso.get().load(selectedRecipe.getImage()).into(binding.headerImage);
//        binding.detailsName.setText(Utilities.capitaliseString(selectedRecipe.getLabel()));
//        binding.detailsCuisine.setText(Utilities.capitaliseString(selectedRecipe.getCuisineType().get(0)));
//        binding.detailsHealthRating.setText(Utilities.capitaliseString(Utilities.getMealHealthRating(selectedRecipe)));

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
            // reference: https://stackoverflow.com/questions/9860456/search-a-specific-string-in-youtube-application-from-my-app
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
     * Saves or removes the workout in the Room Database depending on
     * whether it is already saved or not.
     */
    private void onClickSaveOrDelete() {
//        binding.btnSave.setOnClickListener(v -> {

//            if (!isRecipeAlreadySaved) {
//                // recipe is not already saved, therefore
//                // show the dialog box for the date and meal type
//                new DialogSaveRecipe(requireContext(), (date, mealType) -> {
//                    selectedRecipe.setDateSavedFor(date);
//                    selectedRecipe.setMealTypeSavedFor(mealType);

//                    mDisposable.add(recipeViewModel.insert(selectedRecipe)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(() -> {
//                                Toast.makeText(requireContext(), "Recipe Saved", Toast.LENGTH_LONG).show();
//                                binding.bntSaveImg.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_remove));
//                            }, throwable -> Utilities.showErrorToast(requireContext())));
//                });

//            } else {
//                // recipe is already saved - remove it
//                mDisposable.add(recipeViewModel.delete(selectedRecipe)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(() -> {
//                            Toast.makeText(requireContext(), "Recipe Removed", Toast.LENGTH_LONG).show();
//                            binding.bntSaveImg.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_add_filled));
//                        }, throwable -> Utilities.showErrorToast(requireContext())));
//            }
//        });
    }


    /**
     * Gets the list of nutrients for the given recipe and displays it in the layout.
     */
    private void setupNutrients() {
        binding.tvNutrients.setText(Utilities.getListOfNutrientsFromRecipe(selectedRecipe));
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