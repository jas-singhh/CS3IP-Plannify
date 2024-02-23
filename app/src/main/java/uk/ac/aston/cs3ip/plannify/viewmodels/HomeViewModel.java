package uk.ac.aston.cs3ip.plannify.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.time.LocalDate;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.ac.aston.cs3ip.plannify.MainActivity;
import uk.ac.aston.cs3ip.plannify.enums.EnumMealType;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.RecipeResponseList;
import uk.ac.aston.cs3ip.plannify.models.local_recipe.LocalRecipe;
import uk.ac.aston.cs3ip.plannify.repos.RecipesRepository;

public class HomeViewModel extends ViewModel {
    // Google recommends using 1 ViewModel per View
    // reference: https://www.youtube.com/watch?v=Ts-uxYiBEQ8&t=520s

    private final RecipesRepository recipeRepository;
    private final MutableLiveData<RecipeResponseList> allRecipes;

    public HomeViewModel(Application application) {
        super();
        this.recipeRepository = new RecipesRepository(application);
        allRecipes = new MutableLiveData<>();
    }

    // reference: https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories#:~:text=If%20a%20ViewModel%20class%20receives,new%20instance%20of%20the%20ViewModel.&text=Provides%20access%20to%20the%20custom%20key%20you%20passed%20to%20ViewModelProvider.
    public static final ViewModelInitializer<HomeViewModel> initializer = new ViewModelInitializer<>(
            HomeViewModel.class,
            creationExtras -> {
                Application app = creationExtras.get(ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY	);
                return new HomeViewModel(app);
            }
    );

    /**
     * Returns the requested recipes.
     *
     * @return requested recipes.
     */
    public MutableLiveData<RecipeResponseList> getRequestedRecipes() {
        return allRecipes;
    }

    /**
     * Requests recipes by searching for the provided query.
     *
     * @param query query to search the recipes.
     */
    public void requestRecipesByQuery(String query) {
        Call<RecipeResponseList> request = recipeRepository.getRecipesByQuery(query);
        request.enqueue(new Callback<RecipeResponseList>() {
            @Override
            public void onResponse(@NonNull Call<RecipeResponseList> call, @NonNull Response<RecipeResponseList> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    storeData(response.body());
                } else {
                    assert response.errorBody() != null;
                    Log.i(MainActivity.TAG, response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecipeResponseList> call, @NonNull Throwable t) {
                Log.i(MainActivity.TAG, "Error in requestRecipesByQuery: " + t);
            }
        });
    }

//    /**
//     * Requests recipes by searching for the provided query.
//     *
//     * @param query query to search the recipes.
//     */
//    public void requestRecipesByQuery(String query) {
//        Call<RecipeResponse> request = recipeRepository.getRecipesByQuery(query);
//        request.enqueue(new Callback<RecipeResponse>() {
//            @Override
//            public void onResponse(@NonNull Call<RecipeResponse> call, @NonNull Response<RecipeResponse> response) {
//                if (response.isSuccessful()) {
//                    assert response.body() != null;
//                    storeData(response.body());
//                } else {
//                    assert response.errorBody() != null;
//                    Log.i(MainActivity.TAG, response.errorBody().toString());
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<RecipeResponse> call, @NonNull Throwable t) {
//                Log.i(MainActivity.TAG, "Error in requestRecipesByQuery: " + t);
//            }
//        });
//    }

//    /**
//     * Requests recipes according to the specified diet type.
//     *
//     * @param diet diet for which to fetch recipes.
//     */
//    public void requestRecipesByDiet(EnumDiet diet) {
//        Call<RecipeResponse> request = recipeRepository.getRecipesByDiet(diet);
//        request.enqueue(new Callback<RecipeResponse>() {
//            @Override
//            public void onResponse(@NonNull Call<RecipeResponse> call, @NonNull Response<RecipeResponse> response) {
//                if (response.isSuccessful()) {
//                    assert response.body() != null;
//                    storeData(response.body());
//                } else {
//                    assert response.errorBody() != null;
//                    Log.i(MainActivity.TAG, response.errorBody().toString());
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<RecipeResponse> call, @NonNull Throwable t) {
//                Log.i(MainActivity.TAG, "Error in requestRecipesByDiet: " + t);
//            }
//        });
//    }
//
    /**
     * Requests the recipes by the specified meal type.
     *
     * @param mealType meal type for which recipes are requested.
     */
    public void requestRecipesByMealType(EnumMealType mealType) {
        Call<RecipeResponseList> request = recipeRepository.getRecipesByMealType(mealType);
        request.enqueue(new Callback<RecipeResponseList>() {
            @Override
            public void onResponse(@NonNull Call<RecipeResponseList> call, @NonNull Response<RecipeResponseList> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    storeData(response.body());
                } else {
                    assert response.errorBody() != null;
                    Log.i(MainActivity.TAG, response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecipeResponseList> call, @NonNull Throwable t) {
                Log.e(MainActivity.TAG, "Error in requestRecipesByDiet: " + t);
            }
        });
    }

    /**
     * Stores the given response in the live data.
     *
     * @param recipeResponse response to store.
     */
    private void storeData(RecipeResponseList recipeResponse) {
        this.allRecipes.setValue(recipeResponse);
    }

    /* Local database related queries */


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
     * Deletes the given recipe from the database.
     *
     * @param recipe recipe to delete.
     * @return whether the operation succeeded or not.
     */
    public Completable delete(LocalRecipe recipe) {
        return recipeRepository.delete(recipe);
    }



    /**
     * Returns the local recipes present in the database.
     * @return local recipes in the database.
     */
    public Flowable<List<LocalRecipe>> getAllLocalRecipes() {
        return recipeRepository.getAllLocalRecipes();
    }

    /**
     * Checks whether the recipe already exists in the database and returns 1 if it exists
     * and 0 if it doesn't.
     *
     * @param id unique identifier of the recipe.
     * @return 1 if it exists - 0 if it doesn't.
     */
    public Single<Integer> existsById(int id) {
        return recipeRepository.existsById(id);
    }

    /**
     * Returns a list of local recipes saved for the specified date.
     *
     * @param date date for which to retrieve saved recipes.
     * @return a list of recipes for the given date.
     */
    public Flowable<List<LocalRecipe>> getRecipesForDate(LocalDate date) {
        return recipeRepository.getRecipesForDate(date);
    }

    /**
     * Returns a list of local recipes of the given meal type and for the specified date.
     *
     * @param mealTypeSavedFor meal type of which to retrieve the recipes.
     * @param dateSavedFor date for which to retrieve the recipes.
     * @return a list of local recipes of the given meal type and for the speicified date.
     */
    public Flowable<List<LocalRecipe>> getRecipesOfTypeForDate(EnumMealType mealTypeSavedFor, LocalDate dateSavedFor) {
        return recipeRepository.getRecipesOfTypeForDate(mealTypeSavedFor, dateSavedFor);
    }

    /**
     * Returns a list fo local recipes saved for the dates within the specified range.
     *
     * @param from date from which to fetch recipes.
     * @param to date to which to fetch recipes.
     * @return returns a list of local recipes within the specified dates.
     */
    public Flowable<List<LocalRecipe>> getRecipesWithinDates(LocalDate from, LocalDate to) {
        return recipeRepository.getRecipesWithinDates(from, to);
    }
}
