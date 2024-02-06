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
    @GET("recipes/complexSearch?addRecipeInformation=true&addRecipeNutrition=true")
    Call<RecipeResponseList> getRecipesBySearch(@Query("query") String query);

    @Headers("x-api-key: " + BuildConfig.SPOONACULAR_API_KEY)
    @GET("recipes/complexSearch?addRecipeInformation=true&addRecipeNutrition=true")
    Call<RecipeResponseList> getRecipesByMealType(@Query("type") String type);

    @Headers("x-api-key: " + BuildConfig.SPOONACULAR_API_KEY)
    @GET("recipes/complexSearch?addRecipeInformation=true&addRecipeNutrition=true")
    Call<RecipeResponseList> getRecipesByQueryAndFilters(@Query("query") String query,
                                                         @Query("type") String mealTypes,
                                                         @Query("cuisine") String cuisines,
                                                         @Query("diet") String diets,
                                                         @Query("maxReadyTime")int maxReadyTime);

}
