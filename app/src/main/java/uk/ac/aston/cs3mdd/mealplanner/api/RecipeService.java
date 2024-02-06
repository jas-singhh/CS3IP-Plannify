package uk.ac.aston.cs3mdd.mealplanner.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.RecipeResponse;
import uk.ac.aston.cs3mdd.mealplanner.enums.EnumCuisine;
import uk.ac.aston.cs3mdd.mealplanner.enums.EnumDiet;
import uk.ac.aston.cs3mdd.mealplanner.enums.EnumHealthLabel;

public interface RecipeService {

    // Reference: Mobile Design and Development Lab 4
    @GET("/api/recipes/v2")
    Call<RecipeResponse> getRecipesByQuery(@Query("type") String type,
                                                 @Query("app_id") String appId,
                                                 @Query("app_key") String appKey,
                                                 @Query("q") String query);

    @GET("/api/recipes/v2")
    Call<RecipeResponse> getRecipeByDiet(@Query("type") String type,
                                             @Query("app_id") String appId,
                                             @Query("app_key") String appKey,
                                             @Query("diet") EnumDiet diet);

    @GET("/api/recipes/v2")
    Call<RecipeResponse> getRecipeByHealthLabel(@Query("type") String type,
                                             @Query("app_id") String appId,
                                             @Query("app_key") String appKey,
                                             @Query("health") EnumHealthLabel healthLabel);

    @GET("/api/recipes/v2")
    Call<RecipeResponse> getRecipeByMealType(@Query("type") String type,
                                             @Query("app_id") String appId,
                                             @Query("app_key") String appKey,
                                             @Query("mealType")String mealType);

    @GET("/api/recipes/v2")
    Call<RecipeResponse> getRecipeByCuisineType(@Query("type") String type,
                                             @Query("app_id") String appId,
                                             @Query("app_key") String appKey,
                                             @Query("mealType") EnumCuisine cuisine);
}
