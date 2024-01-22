package uk.ac.aston.cs3mdd.mealplanner.data.recipe;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

import uk.ac.aston.cs3mdd.mealplanner.data.recipe.enums.EnumMealType;

@Entity(tableName = "recipes")
public class LocalRecipe extends Recipe {

    private LocalDate dateSavedFor;
    private EnumMealType mealTypeSavedFor;

    public LocalRecipe() {
        primaryId = "na";
    }

    public LocalRecipe(Recipe recipe, LocalDate dateSavedFor, EnumMealType mealTypeSavedFor) {
        this.setDateSavedFor(dateSavedFor);
        this.setMealTypeSavedFor(mealTypeSavedFor);

        this.setUri(recipe.getUri());
        this.setLabel(recipe.getLabel());
        this.setImage(recipe.getImage());
        this.setLocalImage(recipe.getLocalImage());
        this.setSource(recipe.getSource());
        this.setUrl(recipe.getUrl());
        this.setYield(recipe.getYield());
        this.setDietLabels(recipe.getDietLabels());
        this.setHealthLabels(recipe.getHealthLabels());
        this.setCautions(recipe.getCautions());
        this.setIngredientLines(recipe.getIngredientLines());
        this.setIngredients(recipe.getIngredients());
        this.setCalories(recipe.getCalories());
        this.setGlycemicIndex(recipe.getGlycemicIndex());
        this.setInflammatoryIndex(recipe.getInflammatoryIndex());
        this.setTotalCO2Emissions(recipe.getTotalCO2Emissions());
        this.setCo2EmissionsClass(recipe.getCo2EmissionsClass());
        this.setTotalWeight(recipe.getTotalWeight());
        this.setCuisineType(recipe.getCuisineType());
        this.setMealType(recipe.getMealType());
        this.setDishType(recipe.getDishType());
        this.setInstructions(recipe.getInstructions());
        this.setTags(recipe.getTags());
        this.setExternalId(recipe.getExternalId());
        this.setTotalNutrients(recipe.getTotalNutrients());
        this.setTotalDaily(recipe.getTotalDaily());
        this.setTotalTime(recipe.getTotalTime());
        this.setDigest(recipe.getDigest());
        this.setImages(recipe.getImages());

        primaryId = recipe.getUri() + dateSavedFor + mealTypeSavedFor.getMealType();
    }

    @PrimaryKey @NonNull
    String primaryId;

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

    @NonNull
    public String getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(@NonNull String primaryId) {
        this.primaryId = primaryId;
    }
}
