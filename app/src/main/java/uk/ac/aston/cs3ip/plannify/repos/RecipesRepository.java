package uk.ac.aston.cs3ip.plannify.repos;

import android.app.Application;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import uk.ac.aston.cs3ip.plannify.api.MyRecipeService;
import uk.ac.aston.cs3ip.plannify.api.RetrofitClient;
import uk.ac.aston.cs3ip.plannify.enums.EnumMaxReadyTime;
import uk.ac.aston.cs3ip.plannify.enums.EnumMealType;
import uk.ac.aston.cs3ip.plannify.models.local_recipe.LocalRecipe;
import uk.ac.aston.cs3ip.plannify.models.network_recipe.AutoCompleteResult;
import uk.ac.aston.cs3ip.plannify.models.network_recipe.ExtendedIngredient;
import uk.ac.aston.cs3ip.plannify.models.network_recipe.RecipeResponseList;
import uk.ac.aston.cs3ip.plannify.room.AppDatabase;
import uk.ac.aston.cs3ip.plannify.utils.Utilities;

public class RecipesRepository {
    // MVVM architecture
    private final MyRecipeService request;
    private final AppDatabase database;

    public RecipesRepository(Application application) {
        this.request = RetrofitClient.getRecipeRetrofitInstance().create(MyRecipeService.class);
        this.database = AppDatabase.getDatabase(application);
    }

    /**
     * Returns the recipe response list for the specified meal type.
     *
     * @param mealType meal type, for which to return the recipe list.
     * @return recipe list according to the specified meal type.
     */
    public Call<RecipeResponseList> getRecipesByMealType(EnumMealType mealType) {
        return request.getRecipesByMealType(mealType.getMealType());
    }

    /**
     * Returns the recipe response list based on the specified query and filters.
     *
     * @param query query for which to fetch the recipes.
     * @param mealTypes meal types for which to fetch the recipes.
     * @param cuisines cuisines for which to fetch the recipes.
     * @param diets diets for which to fetch the recipes.
     * @param maxReadyTime max ready time for which to fetch the recipes.
     * @return recipe response list according to the specified query and filters.
     */
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
        return request.getRecipesByQueryAndFilters(query, _mealTypes, _cuisines, _diets, _maxReadyTime);
    }

    /**
     * Returns a list of auto complete data for the given query.
     *
     * @param query query for which to return the list of auto complete data.
     * @return a list of auto complete data based on the given query.
     */
    public Call<List<AutoCompleteResult>> getAutoCompleteForQuery(String query) {
        return request.getAutoCompleteForQuery(query);
    }

    /**
     * Returns a recipe response list containing random healthy recipes.
     *
     * @return recipe response list with random healthy recipes.
     */
    public Call<RecipeResponseList> getRandomHealthyRecipes() {
        return request.getRandomHealthyRecipes(Utilities.getRecommendedNutrientsMap());
    }

    /**
     * Local database related queries
     */

    /**
     * Inserts the given recipe into the database.
     *
     * @param recipe recipe to insert into the database.
     * @return the ID of the recipe inserted into the database.
     */
    public Single<Long> insert(LocalRecipe recipe) {
        if (recipe != null) {
            return database.recipeDao().insert(recipe);
        }

        return null;
    }

    /**
     * Deletes the given recipe from the database, if present.
     *
     * @param recipe recipe to delete.
     * @return a value indicating whether the query succeeded or failed.
     */
    public Completable delete(LocalRecipe recipe) {
        if (recipe != null) {
            return database.recipeDao().delete(recipe);
        }

        return null;
    }

    /**
     * Updates the given recipe in the database, if present.
     *
     * @param recipe recipe to update.
     * @return a value indicating whether the query succeeded or failed.
     */
    public Completable update(LocalRecipe recipe) {
        if (recipe != null) {
            return database.recipeDao().update(recipe);
        }

        return null;
    }

    /**
     * Returns a list of recipes from the database for the specified meal type saved for the
     * specified date.
     *
     * @param mealTypeSavedFor meal type for which to retrieve recipes from the database.
     * @param dateSavedFor date for which to retrieve recipes from the database.
     * @return an observable list of recipes from the database for the specified meal type
     * and saved for the specified date.
     */
    public Flowable<List<LocalRecipe>> getRecipesOfTypeForDate(EnumMealType mealTypeSavedFor, LocalDate dateSavedFor) {
        return database.recipeDao().getRecipesOfTypeForDate(mealTypeSavedFor, dateSavedFor);
    }

    /**
     * Returns a list of recipes from the database within the specified range of dates.
     *
     * @param from date from which to retrieve recipes from the database.
     * @param to date to which to retrieve recipes from the database.
     * @return an observable list of recipes from the database within the specified range of dates.
     */
    public Flowable<List<LocalRecipe>> getRecipesWithinDates(LocalDate from, LocalDate to) {
        return database.recipeDao().getRecipesWithinDates(from, to);
    }

    /**
     * Returns the recipe with the specified primary ID if it exists.
     *
     * @param primaryId primary ID for which to retrieve the recipe from the database.
     * @return the recipe with the specified primary ID if it exists, or an error otherwise.
     */
    public Single<LocalRecipe> getRecipeByPrimaryId(long primaryId) {
        return database.recipeDao().getRecipeByPrimaryId(primaryId);
    }

    /**
     * Updates the list of ingredients for the recipe with the specified primary ID.
     *
     * @param extendedIngredients list of ingredients to update.
     * @param primaryId primary ID of the recipe for which to update the ingredients.
     * @return a value indicating whether the query succeeded or failed.
     */
    public Completable updateIngredientsForRecipeWithPrimaryId(List<ExtendedIngredient> extendedIngredients, long primaryId) {
        return database.recipeDao().updateIngredientsForRecipeWithPrimaryId(extendedIngredients, primaryId);
    }

}
