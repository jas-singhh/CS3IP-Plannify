package uk.ac.aston.cs3mdd.mealplanner.models.local_recipe;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

import uk.ac.aston.cs3mdd.mealplanner.data.recipe.enums.EnumMealType;
import uk.ac.aston.cs3mdd.mealplanner.models.api_recipe.Recipe;

@Entity(tableName = "recipes")
public class LocalRecipe extends Recipe {

    @PrimaryKey
    private int primaryId;
    private LocalDate dateSavedFor;
    private EnumMealType mealTypeSavedFor;

    public LocalRecipe() {
        this.primaryId = -1;
    }

    public LocalRecipe(Recipe recipe, LocalDate dateSavedFor, EnumMealType mealTypeSavedFor) {
        this.primaryId = recipe.getId();
        this.dateSavedFor = dateSavedFor;
        this.mealTypeSavedFor = mealTypeSavedFor;

        this.setVegetarian(recipe.isVegetarian());
        this.setVegan(recipe.isVegan());
        this.setGlutenFree(recipe.isGlutenFree());
        this.setDairyFree(recipe.isDairyFree());
        this.setVeryHealthy(recipe.isVeryHealthy());
        this.setCheap(recipe.isCheap());
        this.setVeryPopular(recipe.isVeryPopular());
        this.setSustainable(recipe.isSustainable());
        this.setLowFodmap(recipe.isLowFodmap());
        this.setWeightWatcherSmartPoints(recipe.getWeightWatcherSmartPoints());
        this.setGaps(recipe.getGaps());
        this.setPreparationMinutes(recipe.getPreparationMinutes());
        this.setCookingMinutes(recipe.getCookingMinutes());
        this.setAggregateLikes(recipe.getAggregateLikes());
        this.setHealthScore(recipe.getHealthScore());
        this.setCreditsText(recipe.getCreditsText());
        this.setLicense(recipe.getLicense());
        this.setSourceName(recipe.getSourceName());
        this.setPricePerServing(recipe.getPricePerServing());
        this.setExtendedIngredients(recipe.getExtendedIngredients());
        this.setTitle(recipe.getTitle());
        this.setReadyInMinutes(recipe.getReadyInMinutes());
        this.setServings(recipe.getServings());
        this.setSourceUrl(recipe.getSourceUrl());
        this.setImage(recipe.getImage());
        this.setImageType(recipe.getImageType());
        this.setSummary(recipe.getSummary());
        this.setCuisines(recipe.getCuisines());
        this.setDishTypes(recipe.getDishTypes());
        this.setDiets(recipe.getDiets());
        this.setOccasions(recipe.getOccasions());
        this.setAnalyzedInstructions(recipe.getAnalyzedInstructions());
        this.setNutrition(recipe.getNutrition());
        this.setSpoonacularScore(recipe.getSpoonacularScore());
        this.setSpoonacularSourceUrl(recipe.getSpoonacularSourceUrl());
        this.setUsedIngredientCount(recipe.getUsedIngredientCount());
        this.setMissedIngredientCount(recipe.getMissedIngredientCount());
        this.setLikes(recipe.getLikes());
    }

    public int getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(int primaryId) {
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
