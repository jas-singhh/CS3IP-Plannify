package uk.ac.aston.cs3mdd.mealplanner.api;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import uk.ac.aston.cs3mdd.mealplanner.BuildConfig;
import uk.ac.aston.cs3mdd.mealplanner.models.api_recipe.AutoCompleteResult;
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

    @Headers("x-api-key: " + BuildConfig.SPOONACULAR_API_KEY)
    @GET("recipes/autocomplete?number=10")
    Call<List<AutoCompleteResult>> getAutoCompleteForQuery(@Query("query") String query);


    @Headers("x-api-key: " + BuildConfig.SPOONACULAR_API_KEY)
    @GET("recipes/complexSearch?addRecipeInformation=true&addRecipeNutrition=true&number=10")
    Call<RecipeResponseList> getRandomHealthyRecipes(@QueryMap HashMap<String, Float> nutrientsMap);
}
