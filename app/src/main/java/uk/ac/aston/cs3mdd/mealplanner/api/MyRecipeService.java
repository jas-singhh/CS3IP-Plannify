package uk.ac.aston.cs3mdd.mealplanner.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import uk.ac.aston.cs3mdd.mealplanner.BuildConfig;
import uk.ac.aston.cs3mdd.mealplanner.models.api_recipe.RecipeResponseList;

public interface MyRecipeService {

    // Reference: Mobile Design and Development Lab 4
    @Headers("x-api-key: " + BuildConfig.SPOONACULAR_API_KEY)
    @GET("recipes/complexSearch")
    Call<RecipeResponseList> getRecipesBySearch(@Query("query") String query,
                                                @Query("addRecipeInformation") boolean addRecipeInformation,
                                                @Query("addRecipeNutrition") boolean addRecipeNutrition,
                                                @Query("fillIngredients") boolean fillIngredients);

    @Headers("x-api-key: " + BuildConfig.SPOONACULAR_API_KEY)
    @GET("recipes/complexSearch")
    Call<RecipeResponseList> getRecipesByMealType(@Query("type") String type,
                                                  @Query("addRecipeInformation") boolean addRecipeInformation,
                                                  @Query("addRecipeNutrition") boolean addRecipeNutrition,
                                                  @Query("fillIngredients") boolean fillIngredients);
}
