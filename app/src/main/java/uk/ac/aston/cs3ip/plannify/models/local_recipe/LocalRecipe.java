package uk.ac.aston.cs3ip.plannify.models.local_recipe;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

import uk.ac.aston.cs3ip.plannify.enums.EnumMealType;
import uk.ac.aston.cs3ip.plannify.models.network_recipe.NetworkRecipe;

@Entity(tableName = "recipes", indices = {@Index(value={"id", "title", "dateSavedFor", "mealTypeSavedFor"}, unique = true)})
public class LocalRecipe extends NetworkRecipe {

    // reference: https://stackoverflow.com/questions/59159978/android-room-insert-how-do-i-verify-by-comparing-object-strings-that-i-wont
    // indices ensure data integrity by preventing duplicate insertion in the database
    // for remote recipe - we need to check the id, date saved for, and meal type saved for
    // however, for custom recipes - we need to check the title as well because the id is always -1

    @PrimaryKey(autoGenerate = true)
    private long primaryId;
    private LocalDate dateSavedFor;
    private EnumMealType mealTypeSavedFor;

    /**
     * Room expects an empty public constructor for its entities.
     */
    public LocalRecipe() {
        this.setId(-1);// id will be -1 for custom recipes
    }

    /**
     * This constructor creates a local recipe from a normal recipe, along with
     * more details such as date saved for and meal type saved for.
     *
     * @param networkRecipe recipe from which to create this local recipe.
     * @param dateSavedFor date for which to save the local recipe.
     * @param mealTypeSavedFor meal type for which to save the local recipe.
     */
    public LocalRecipe(NetworkRecipe networkRecipe, LocalDate dateSavedFor, EnumMealType mealTypeSavedFor) {
        this.dateSavedFor = dateSavedFor;
        this.mealTypeSavedFor = mealTypeSavedFor;

        this.setId(networkRecipe.getId());
        this.setVegetarian(networkRecipe.isVegetarian());
        this.setVegan(networkRecipe.isVegan());
        this.setGlutenFree(networkRecipe.isGlutenFree());
        this.setDairyFree(networkRecipe.isDairyFree());
        this.setVeryHealthy(networkRecipe.isVeryHealthy());
        this.setCheap(networkRecipe.isCheap());
        this.setVeryPopular(networkRecipe.isVeryPopular());
        this.setSustainable(networkRecipe.isSustainable());
        this.setLowFodmap(networkRecipe.isLowFodmap());
        this.setWeightWatcherSmartPoints(networkRecipe.getWeightWatcherSmartPoints());
        this.setGaps(networkRecipe.getGaps());
        this.setPreparationMinutes(networkRecipe.getPreparationMinutes());
        this.setCookingMinutes(networkRecipe.getCookingMinutes());
        this.setAggregateLikes(networkRecipe.getAggregateLikes());
        this.setHealthScore(networkRecipe.getHealthScore());
        this.setCreditsText(networkRecipe.getCreditsText());
        this.setLicense(networkRecipe.getLicense());
        this.setSourceName(networkRecipe.getSourceName());
        this.setPricePerServing(networkRecipe.getPricePerServing());
        this.setExtendedIngredients(networkRecipe.getExtendedIngredients());
        this.setTitle(networkRecipe.getTitle());
        this.setReadyInMinutes(networkRecipe.getReadyInMinutes());
        this.setServings(networkRecipe.getServings());
        this.setSourceUrl(networkRecipe.getSourceUrl());
        this.setImage(networkRecipe.getImage());
        this.setImageType(networkRecipe.getImageType());
        this.setSummary(networkRecipe.getSummary());
        this.setCuisines(networkRecipe.getCuisines());
        this.setDishTypes(networkRecipe.getDishTypes());
        this.setDiets(networkRecipe.getDiets());
        this.setOccasions(networkRecipe.getOccasions());
        this.setAnalyzedInstructions(networkRecipe.getAnalyzedInstructions());
        this.setNutrition(networkRecipe.getNutrition());
        this.setSpoonacularScore(networkRecipe.getSpoonacularScore());
        this.setSpoonacularSourceUrl(networkRecipe.getSpoonacularSourceUrl());
        this.setUsedIngredientCount(networkRecipe.getUsedIngredientCount());
        this.setMissedIngredientCount(networkRecipe.getMissedIngredientCount());
        this.setLikes(networkRecipe.getLikes());
    }

    public long getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(long primaryId) {
        this.primaryId = primaryId;
    }

    public LocalDate getDateSavedFor() {
        return dateSavedFor;
    }

    public void setDateSavedFor(LocalDate dateSavedFor) {
        this.dateSavedFor = dateSavedFor;
    }

    public EnumMealType getMealTypeSavedFor() {
        return mealTypeSavedFor;
    }

    public void setMealTypeSavedFor(EnumMealType mealTypeSavedFor) {
        this.mealTypeSavedFor = mealTypeSavedFor;
    }
}
