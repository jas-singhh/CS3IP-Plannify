package uk.ac.aston.cs3mdd.mealplanner.repos;

import retrofit2.Call;
import uk.ac.aston.cs3mdd.mealplanner.BuildConfig;
import uk.ac.aston.cs3mdd.mealplanner.api.RecipeService;
import uk.ac.aston.cs3mdd.mealplanner.api.RetrofitClient;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.RecipeResponse;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.enums.EnumDiet;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.enums.EnumMealType;

public class RecipesRepository {
    // MVVM architecture
    private static final String TYPE = "public";
    private final RecipeService request;

    public RecipesRepository() {
        this.request = RetrofitClient.getInstance(BuildConfig.EDAMAM_BASE_URL).create(RecipeService.class);
    }

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
}
