package uk.ac.aston.cs3mdd.mealplanner.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.aston.cs3mdd.mealplanner.MainActivity;
import uk.ac.aston.cs3mdd.mealplanner.enums.EnumFilterId;
import uk.ac.aston.cs3mdd.mealplanner.models.api_recipe.RecipeResponseList;
import uk.ac.aston.cs3mdd.mealplanner.models.local_recipe.LocalRecipe;
import uk.ac.aston.cs3mdd.mealplanner.repos.RecipesRepository;

public class FindMealsViewModel extends ViewModel {

    private final RecipesRepository recipeRepository;
    private final MutableLiveData<RecipeResponseList> requestedRecipes;
    private final MutableLiveData<HashMap<EnumFilterId, List<String>>> filtersMap;

    public FindMealsViewModel(Application application) {
        super();
        this.recipeRepository = new RecipesRepository(application);
        requestedRecipes = new MutableLiveData<>();
        filtersMap = new MutableLiveData<>();
    }

    // reference: https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories#:~:text=If%20a%20ViewModel%20class%20receives,new%20instance%20of%20the%20ViewModel.&text=Provides%20access%20to%20the%20custom%20key%20you%20passed%20to%20ViewModelProvider.
    public static final ViewModelInitializer<FindMealsViewModel> initializer = new ViewModelInitializer<>(
            FindMealsViewModel.class,
            creationExtras -> {
                Application app = creationExtras.get(ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY);
                return new FindMealsViewModel(app);
            }
    );

    /**
     * Requests the recipes for the specified query and filters.
     *
     * @param query query specified for the recipes.
     */
    public void requestRecipesByQueryAndFilters(String query) {

        // null by default indicates that there are no filters applied
        List<String> mealTypes = null;
        List<String> cuisines = null;
        List<String> diets = null;
        List<String> maxReadyTimeList = null;

        // check if filters have been applied
        if (filtersMap != null && filtersMap.getValue() != null) {
            if (filtersMap.getValue().get(EnumFilterId.MEAL_TYPE) != null)
                mealTypes = filtersMap.getValue().get(EnumFilterId.MEAL_TYPE);
            if (filtersMap.getValue().get(EnumFilterId.CUISINES) != null)
                cuisines = filtersMap.getValue().get(EnumFilterId.CUISINES);
            if (filtersMap.getValue().get(EnumFilterId.DIETS) != null)
                diets = filtersMap.getValue().get(EnumFilterId.DIETS);
            if (filtersMap.getValue().get(EnumFilterId.MAX_READY_TIME) != null)
                maxReadyTimeList = filtersMap.getValue().get(EnumFilterId.MAX_READY_TIME);
        }

        Call<RecipeResponseList> request = recipeRepository.getRecipesByQueryAndFilters(query, mealTypes, cuisines, diets, maxReadyTimeList);

        request.enqueue(new Callback<RecipeResponseList>() {
            @Override
            public void onResponse(@NonNull Call<RecipeResponseList> call, @NonNull Response<RecipeResponseList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getResults().isEmpty()) {
                        Log.d(MainActivity.TAG, "response empty");
                    }

                    storeData(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecipeResponseList> call, @NonNull Throwable t) {
                Log.e(MainActivity.TAG, "Error in requestRecipesByDiet: " + t);
            }
        });
    }

    /**
     * Inserts the given recipe into the database.
     *
     * @param recipe recipe to insert.
     * @return whether the operation succeeded or not.
     */
    public Completable insert(LocalRecipe recipe) {
        return recipeRepository.insert(recipe);
    }


    /**
     * Stores the given response in the live data.
     *
     * @param recipeResponse response to store.
     */
    private void storeData(RecipeResponseList recipeResponse) {
        this.requestedRecipes.setValue(recipeResponse);
    }


    /**
     * Returns the requested recipes.
     *
     * @return requested recipes.
     */
    public MutableLiveData<RecipeResponseList> getRequestedRecipes() {
        return requestedRecipes;
    }

    /**
     * Returns the currently selected filter options.
     *
     * @return selected filter options.
     */
    public MutableLiveData<HashMap<EnumFilterId, List<String>>> getFiltersMap() {
        return filtersMap;
    }

    /**
     * Sets the given value to the filters map.
     *
     * @param filtersMap map to set as the value for the filters map.
     */
    public void setFiltersMap(HashMap<EnumFilterId, List<String>> filtersMap) {
        this.filtersMap.setValue(filtersMap);
    }
}
