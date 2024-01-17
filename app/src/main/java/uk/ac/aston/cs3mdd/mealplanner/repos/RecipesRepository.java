package uk.ac.aston.cs3mdd.mealplanner.repos;

import android.app.Application;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import uk.ac.aston.cs3mdd.mealplanner.BuildConfig;
import uk.ac.aston.cs3mdd.mealplanner.api.RecipeService;
import uk.ac.aston.cs3mdd.mealplanner.api.RetrofitClient;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Recipe;
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

    /** Network related queries */

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

    /** Local database related queries */

    public Completable insert(Recipe recipe) {
        if (recipe != null) {
            return database.recipeDao().insert(recipe);
        }

        return null;
    }

    public Completable delete(Recipe recipe) {
        if (recipe != null) {
            return database.recipeDao().delete(recipe);
        }

        return null;
    }

    public Completable update(Recipe recipe) {
        if (recipe != null) {
            return database.recipeDao().update(recipe);
        }

        return null;
    }

    public Flowable<List<Recipe>> getAllLocalRecipes() {
        return database.recipeDao().getAll();
    }

    public Single<Integer> existsByUri(String uri) { return database.recipeDao().existsByUri(uri);}
}
