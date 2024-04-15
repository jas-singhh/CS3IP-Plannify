package uk.ac.aston.cs3ip.plannify.views.meal_details;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import uk.ac.aston.cs3ip.plannify.R;
import uk.ac.aston.cs3ip.plannify.adapters.MealDetailsIngredientsAdapter;
import uk.ac.aston.cs3ip.plannify.adapters.MealDetailsNutrientsAdapter;
import uk.ac.aston.cs3ip.plannify.databinding.FragmentMealDetailsBinding;
import uk.ac.aston.cs3ip.plannify.models.local_recipe.LocalRecipe;
import uk.ac.aston.cs3ip.plannify.models.network_recipe.ExtendedIngredient;
import uk.ac.aston.cs3ip.plannify.models.network_recipe.NetworkRecipe;
import uk.ac.aston.cs3ip.plannify.models.network_recipe.Nutrient;
import uk.ac.aston.cs3ip.plannify.models.network_recipe.Step;
import uk.ac.aston.cs3ip.plannify.utils.SnackBarUtils;
import uk.ac.aston.cs3ip.plannify.utils.Utilities;
import uk.ac.aston.cs3ip.plannify.viewmodels.HomeViewModel;
import uk.ac.aston.cs3ip.plannify.views.dialogs.DialogSaveRecipe;

public class MealDetailsFragment extends Fragment {

    private FragmentMealDetailsBinding binding;
    private NetworkRecipe selectedNetworkRecipe;
    private ConstraintLayout[] tabLayouts;
    private HomeViewModel homeViewModel;
    private CompositeDisposable mDisposable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialisation
        homeViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.Factory.from(HomeViewModel.initializer)).get(HomeViewModel.class);
        mDisposable = new CompositeDisposable();

        // safe args
        selectedNetworkRecipe = MealDetailsFragmentArgs.fromBundle(getArguments()).getMyRecipe();
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
        initSteps();
        initNutrients();

        // store layouts' parents for easier switching in the order they appear
        tabLayouts = new ConstraintLayout[]{binding.ingredientsParent, binding.stepsParent, binding.nutrientsParent};
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
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }


    /**
     * Displays the main three attributes of the meal, i.e. the meal cuisine,
     * the meal's health rating, and the meal's serving size.
     */
    private void displayMealMainAttributes() {
        if (selectedNetworkRecipe == null) return;

        // image
        Picasso.get().load(selectedNetworkRecipe.getImage())
                .placeholder(R.drawable.img_image_not_available)
                .into(binding.headerImage);

        // name
        binding.detailsName.setText(Utilities.capitaliseString(selectedNetworkRecipe.getTitle()));

        // time
        String time = "N/A";
        if (selectedNetworkRecipe.getReadyInMinutes() > 0) time = selectedNetworkRecipe.getReadyInMinutes() + "m";
        binding.detailsTime.setText(time);

        // health rating - returns N/A if it is 0
        binding.detailsHealthRating.setText(Utilities.getMealHealthRating(selectedNetworkRecipe));

        // servings
        String servings = "N/A";
        if (selectedNetworkRecipe.getServings() > 0) servings = selectedNetworkRecipe.getServings() + " servings";
        binding.detailsServings.setText(servings);
    }


    /**
     * Sets up the recycler view to show the ingredients required for the meal.
     */
    private void initIngredients() {
        binding.detailsIngredientsRv.setHasFixedSize(true);
        LinearLayoutManager rvLayout = new LinearLayoutManager(requireContext());
        rvLayout.setOrientation(LinearLayoutManager.VERTICAL);
        binding.detailsIngredientsRv.setLayoutManager(rvLayout);

        if (selectedNetworkRecipe.getExtendedIngredients() != null) {
            // initialise adapter
            binding.detailsIngredientsRv.setAdapter(
                    new MealDetailsIngredientsAdapter(selectedNetworkRecipe.
                            getExtendedIngredients().
                            toArray(new ExtendedIngredient[0])));

            // show no results status message if there are no ingredients
        }
    }

    /**
     * Sets up the steps for the specific recipe.
     */
    private void initSteps() {
        if (selectedNetworkRecipe.getAnalyzedInstructions() == null) return;
        if (selectedNetworkRecipe.getAnalyzedInstructions().isEmpty() ||
                selectedNetworkRecipe.getAnalyzedInstructions().get(0).getSteps() == null) return;

        List<Step> steps = selectedNetworkRecipe.getAnalyzedInstructions().get(0).getSteps();

        // html formatting for readability
        StringBuilder mealSteps = new StringBuilder();
        for (Step step : steps) {
            mealSteps.append("<h5>Step ").append(step.getNumber()).append("</h5>");
            mealSteps.append(step.getStep()).append("<br>");
        }

        binding.mealDetailsSteps.setText(Html.fromHtml(mealSteps.toString(), Html.FROM_HTML_MODE_LEGACY));
    }


    /**
     * Gets the list of nutrients for the given recipe and displays it in the layout.
     */
    private void initNutrients() {
        // nutrients to limit
        binding.detailsNutrientsLimitRv.setHasFixedSize(true);
        binding.detailsNutrientsLimitRv.setLayoutManager(new LinearLayoutManager(requireContext()));

        // nutrients to get enough
        binding.detailsNutrientsGetEnoughRv.setHasFixedSize(true);
        binding.detailsNutrientsGetEnoughRv.setLayoutManager(new LinearLayoutManager(requireContext()));

        if (selectedNetworkRecipe.getNutrition() != null) {
            binding.detailsNutrientsLimitRv.setAdapter(new MealDetailsNutrientsAdapter(getNutrientsToLimit()));
            binding.detailsNutrientsGetEnoughRv.setAdapter(new MealDetailsNutrientsAdapter(getNutrientsToGetEnough()));
        }
    }

    /**
     * Filters out the nutrients to limit for this recipe and returns them as an array.
     *
     * @return an array of nutrients to limit, if this recipe has any, null otherwise.
     */
    private Nutrient[] getNutrientsToLimit() {

        if (selectedNetworkRecipe.getNutrition() != null) {
            List<Nutrient> nutrientList = selectedNetworkRecipe.getNutrition().getNutrients();
            if (nutrientList != null && !nutrientList.isEmpty()) {
                List<String> nutrientsToLimitNames = Arrays.asList(Utilities.getNutrientsToLimitNames());

                // filter the nutrients to limit associated with this recipe
                return nutrientList.stream().filter(nutrient -> nutrient.getName() != null &&
                        nutrientsToLimitNames.contains(nutrient.getName())).toArray(Nutrient[]::new);
            }
        }

        return null;
    }

    /**
     * Filters out the nutrients to get enough for this recipe and returns them as an array.
     *
     * @return an array of nutrients to get enough, if the recipe has any, null otherwise.
     */
    private Nutrient[] getNutrientsToGetEnough() {

        if (selectedNetworkRecipe.getNutrition() != null) {
            List<Nutrient> nutrientList = selectedNetworkRecipe.getNutrition().getNutrients();
            if (nutrientList != null && !nutrientList.isEmpty()) {
                List<String> nutrientsToGetEnoughNames = Arrays.asList(Utilities.getNutrientsToGetEnoughNames());

                return nutrientList.stream()
                        .filter(nutrient -> nutrient.getName() != null &&
                                nutrientsToGetEnoughNames.contains(nutrient.getName())).toArray(Nutrient[]::new);
            }
        }

        return null;
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
            intent.putExtra("query", selectedNetworkRecipe.getTitle());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            requireActivity().startActivity(intent);
        });
    }

    /**
     * Navigates the user back to the previous page depending on which source page
     * called it.
     */
    private void onClickBack() {
        binding.btnBack.setOnClickListener(v -> {

            // reference: https://developer.android.com/guide/navigation/navcontroller
            // navigate back to the previous destination
            NavHostFragment.findNavController(this).popBackStack();

        });
    }

    /**
     * Saves or removes the workout in the Room Database depending on
     * whether it is already saved or not.
     */
    private void onClickSave() {
        binding.btnSave.setOnClickListener(v -> {

            // show the dialog box for the date and meal type
            new DialogSaveRecipe(requireContext(), selectedNetworkRecipe, (date, mealType) -> {
                LocalRecipe recipeToSave = new LocalRecipe(selectedNetworkRecipe, date, mealType);

                mDisposable.add(homeViewModel.insert(recipeToSave)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(primaryId -> {
                            // check if the returned primary id is -1 - meaning that the recipe was
                            // not inserted as it already exists. If so, display an informational SnackBar.
                            if (primaryId == -1) {
                                SnackBarUtils.showInformationalSnackBarIfRecipeAlreadySaved(requireView());
                            } else {
                                SnackBarUtils.showInformationalSnackBarOnRecipeSaved(requireView());
                            }
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