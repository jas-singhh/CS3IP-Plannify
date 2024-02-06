package uk.ac.aston.cs3mdd.mealplanner.repos;

import android.app.Application;
import android.util.Log;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import uk.ac.aston.cs3mdd.mealplanner.BuildConfig;
import uk.ac.aston.cs3mdd.mealplanner.MainActivity;
import uk.ac.aston.cs3mdd.mealplanner.api.MyRecipeService;
import uk.ac.aston.cs3mdd.mealplanner.api.RetrofitClient;
import uk.ac.aston.cs3mdd.mealplanner.enums.EnumMaxReadyTime;
import uk.ac.aston.cs3mdd.mealplanner.enums.EnumMealType;
import uk.ac.aston.cs3mdd.mealplanner.models.api_recipe.RecipeResponseList;
import uk.ac.aston.cs3mdd.mealplanner.models.local_recipe.LocalRecipe;
import uk.ac.aston.cs3mdd.mealplanner.room.AppDatabase;

public class RecipesRepository {
    // MVVM architecture
    private static final String TYPE = "public";
    private final MyRecipeService request;
    private final AppDatabase database;

    public RecipesRepository(Application application) {
        this.request = RetrofitClient.getInstance(BuildConfig.SPOONACULAR_BASE_URL).create(MyRecipeService.class);
        this.database = AppDatabase.getDatabase(application);
    }

    /**
     * Network related queries
     */

    public Call<RecipeResponseList> getRecipesByQuery(String query) {
        return request.getRecipesBySearch(query);
    }

//    public Call<RecipeResponse> getRecipesByQuery(String query) {
//        return request.getRecipesByQuery(TYPE, BuildConfig.EDAMAM_APP_ID,
//                BuildConfig.EDAMAM_API_KEY, query);
//    }


    //    public Call<RecipeResponse> getRecipesByDiet(EnumDiet diet) {
//        return request.getRecipeByDiet(TYPE, BuildConfig.EDAMAM_APP_ID,
//                BuildConfig.EDAMAM_API_KEY, diet);
//    }
//
    public Call<RecipeResponseList> getRecipesByMealType(EnumMealType mealType) {
        return request.getRecipesByMealType(mealType.getMealType());
    }

    public Call<RecipeResponseList> getRecipesByQueryAndFilters(String query, List<String> mealTypes, List<String> cuisines, List<String> diets, List<String> maxReadyTime) {
        // api call requires comma separated values
        // reference: https://stackoverflow.com/questions/57602096/convert-a-list-of-integers-into-a-comma-separated-string
        String _mealTypes = null;
        String _cuisines = null;
        String _diets = null;
        int _maxReadyTime = 300;// default 5h

        if (mealTypes != null)
            _mealTypes = mealTypes.stream().map(String::valueOf).collect(Collectors.joining(", "));
        if (cuisines != null)
            _cuisines = cuisines.stream().map(String::valueOf).collect(Collectors.joining(", "));
        if (diets != null)
            _diets = diets.stream().map(String::valueOf).collect(Collectors.joining(", "));

        // transform the max ready time list into an integer
        if (maxReadyTime != null && !maxReadyTime.isEmpty()) {
                if (maxReadyTime.get(0) != null) {
                    EnumMaxReadyTime maxReadyTimeEnum = EnumMaxReadyTime.fromValue(maxReadyTime.get(0));
                    _maxReadyTime = maxReadyTimeEnum.getMaxReadyTimeInMinutesFromEnum();
            }
        }

        Log.d(MainActivity.TAG, "meal types: " + _mealTypes);
        Log.d(MainActivity.TAG, "cuisines: " + _cuisines);
        Log.d(MainActivity.TAG, "diets: " + _diets);
        Log.d(MainActivity.TAG, "max ready time: " + _maxReadyTime);

        return request.getRecipesByQueryAndFilters(query, _mealTypes, _cuisines, _diets, _maxReadyTime);
    }

    /**
     * Local database related queries
     */

    public Completable insert(LocalRecipe recipe) {
        if (recipe != null) {
            return database.recipeDao().insert(recipe);
        }

        return null;
    }

    public Completable delete(LocalRecipe recipe) {
        if (recipe != null) {
            return database.recipeDao().delete(recipe);
        }

        return null;
    }

    public Completable update(LocalRecipe recipe) {
        if (recipe != null) {
            return database.recipeDao().update(recipe);
        }

        return null;
    }

    public Flowable<List<LocalRecipe>> getAllLocalRecipes() {
        return database.recipeDao().getAll();
    }

    public Single<Integer> existsById(int id) {
        return database.recipeDao().existsById(id);
    }

    public Flowable<List<LocalRecipe>> getRecipesForDate(LocalDate date) {
        return database.recipeDao().getRecipesForDate(date);
    }

    public Flowable<List<LocalRecipe>> getRecipesOfTypeForDate(EnumMealType mealTypeSavedFor, LocalDate dateSavedFor) {
        return database.recipeDao().getRecipesOfTypeForDate(mealTypeSavedFor, dateSavedFor);
    }

    public Flowable<List<LocalRecipe>> getRecipesWithinDates(LocalDate from, LocalDate to) {
        return database.recipeDao().getRecipesWithinDates(from, to);
    }

}
