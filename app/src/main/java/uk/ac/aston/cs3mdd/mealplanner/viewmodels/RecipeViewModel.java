package uk.ac.aston.cs3mdd.mealplanner.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.aston.cs3mdd.mealplanner.MainActivity;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.enums.EnumMealType;
import uk.ac.aston.cs3mdd.mealplanner.repos.RecipesRepository;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.RecipeResponse;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.enums.EnumDiet;

public class RecipeViewModel extends ViewModel {

    private RecipesRepository recipeRepository;
    private MutableLiveData<RecipeResponse> allRecipes;


    public RecipeViewModel() {
        super();
        this.recipeRepository = new RecipesRepository();
        allRecipes = new MutableLiveData<>();
    }

    /**
     * Returns the requested recipes.
     *
     * @return requested recipes.
     */
    public MutableLiveData<RecipeResponse> getRequestedRecipes() {
        return allRecipes;
    }


    /**
     * Requests recipes by searching for the provided query.
     *
     * @param query query to search the recipes.
     */
    public void requestRecipesByQuery(String query) {
        Call<RecipeResponse> request = recipeRepository.getRecipesByQuery(query);
        request.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecipeResponse> call, @NonNull Response<RecipeResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    storeData(response.body());
                } else {
                    assert response.errorBody() != null;
                    Log.i(MainActivity.TAG, response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecipeResponse> call, @NonNull Throwable t) {
                Log.i(MainActivity.TAG, "Error in requestRecipesByQuery: " + t);
            }
        });
    }

    /**
     * Requests recipes according to the specified diet type.
     *
     * @param diet diet for which to fetch recipes.
     */
    public void requestRecipesByDiet(EnumDiet diet) {
        Call<RecipeResponse> request = recipeRepository.getRecipesByDiet(diet);
        request.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecipeResponse> call, @NonNull Response<RecipeResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    storeData(response.body());
                } else {
                    assert response.errorBody() != null;
                    Log.i(MainActivity.TAG, response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecipeResponse> call, @NonNull Throwable t) {
                Log.i(MainActivity.TAG, "Error in requestRecipesByDiet: " + t);
            }
        });
    }

    /**
     * Requests the recipes by the specified meal type.
     *
     * @param mealType meal type for which recipes are requested.
     */
    public void requestRecipesByMealType(EnumMealType mealType) {
        Call<RecipeResponse> request = recipeRepository.getRecipesByMealType(mealType);
        request.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecipeResponse> call, @NonNull Response<RecipeResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    storeData(response.body());
                } else {
                    assert response.errorBody() != null;
                    Log.i(MainActivity.TAG, response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecipeResponse> call, @NonNull Throwable t) {
                Log.i(MainActivity.TAG, "Error in requestRecipesByDiet: " + t);
            }
        });
    }

    /**
     * Stores the given response in the live data.
     *
     * @param recipeResponse response to store.
     */
    private void storeData(RecipeResponse recipeResponse) {
        this.allRecipes.setValue(recipeResponse);
    }
}
