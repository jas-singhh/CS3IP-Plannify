package uk.ac.aston.cs3mdd.mealplanner.repos;

import android.app.Application;

import java.time.LocalDate;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import uk.ac.aston.cs3mdd.mealplanner.BuildConfig;
import uk.ac.aston.cs3mdd.mealplanner.api.RecipeService;
import uk.ac.aston.cs3mdd.mealplanner.api.RetrofitClient;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.LocalRecipe;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.RecipeResponse;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.enums.EnumDiet;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.enums.EnumMealType;
import uk.ac.aston.cs3mdd.mealplanner.room.AppDatabase;

public class RecipesRepository {
    // MVVM architecture
    private static final String TYPE = "public";
    private final RecipeService request;
    private final AppDatabase database;

    public RecipesRepository(Application application) {
        this.request = RetrofitClient.getInstance(BuildConfig.EDAMAM_BASE_URL).create(RecipeService.class);
        this.database = AppDatabase.getDatabase(application);
    }

    /**
     * Network related queries
     */

    public Call<RecipeResponse> getRecipesByQuery(String query) {
        return request.getRecipesByQuery(TYPE, BuildConfig.EDAMAM_APP_ID,
                BuildConfig.EDAMAM_API_KEY, query);
    }


    public Call<RecipeResponse> getRecipesByDiet(EnumDiet diet) {
        return request.getRecipeByDiet(TYPE, BuildConfig.EDAMAM_APP_ID,
                BuildConfig.EDAMAM_API_KEY, diet);
    }

    public Call<RecipeResponse> getRecipesByMealType(EnumMealType mealType) {
        return request.getRecipeByMealType(TYPE, BuildConfig.EDAMAM_APP_ID,
                BuildConfig.EDAMAM_API_KEY, mealType.getMealType());
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

    public Single<Integer> existsById(String uri) {
        return database.recipeDao().existsByUri(uri);
    }

    public Flowable<List<LocalRecipe>> getRecipesForDate(LocalDate date) {
        return database.recipeDao().getRecipesForDate(date);
    }

    public Flowable<List<LocalRecipe>> getRecipesOfTypeForDate(EnumMealType mealTypeSavedFor, LocalDate dateSavedFor) {
        return database.recipeDao().getRecipesOfTypeForDate(mealTypeSavedFor, dateSavedFor);
    }


}
