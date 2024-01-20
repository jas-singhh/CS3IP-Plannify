package uk.ac.aston.cs3mdd.mealplanner.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.aston.cs3mdd.mealplanner.MainActivity;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Recipe;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.RecipeResponse;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.enums.EnumDiet;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.enums.EnumMealType;
import uk.ac.aston.cs3mdd.mealplanner.repos.RecipesRepository;

public class RecipeViewModel extends ViewModel {

    private final RecipesRepository recipeRepository;
    private final MutableLiveData<RecipeResponse> allRecipes;


    public RecipeViewModel(Application application) {
        super();
        this.recipeRepository = new RecipesRepository(application);
        allRecipes = new MutableLiveData<>();
    }

    // reference: https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories#:~:text=If%20a%20ViewModel%20class%20receives,new%20instance%20of%20the%20ViewModel.&text=Provides%20access%20to%20the%20custom%20key%20you%20passed%20to%20ViewModelProvider.
    public static final ViewModelInitializer<RecipeViewModel> initializer = new ViewModelInitializer<>(
            RecipeViewModel.class,
            creationExtras -> {
                Application app = creationExtras.get(ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY	);
                return new RecipeViewModel(app);
            }
    );

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

    /* Local database related queries */


    /**
     * Inserts the given recipe into the database.
     *
     * @param recipe recipe to insert.
     * @return whether the operation succeeded or not.
     */
    public Completable insert(Recipe recipe) {
        return recipeRepository.insert(recipe);
    }

    /**
     * Deletes the given recipe from the database.
     *
     * @param recipe recipe to delete.
     * @return whether the operation succeeded or not.
     */
    public Completable delete(Recipe recipe) {
        return recipeRepository.delete(recipe);
    }



    /**
     * Returns the local recipes present in the database.
     * @return local recipes in the database.
     */
    public Flowable<List<Recipe>> getAllLocalRecipes() {
        return recipeRepository.getAllLocalRecipes();
    }

    /**
     * Checks whether the recipe already exists in the database and returns 1 if it exists
     * and 0 if it doesn't.
     *
     * @param uri unique identifier of the recipe.
     * @return 1 if it exists - 0 if it doesn't.
     */
    public Single<Integer> existsByUri(String uri) {
        return recipeRepository.existsByUri(uri);
    }
}
