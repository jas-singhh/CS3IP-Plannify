package uk.ac.aston.cs3mdd.mealplanner.data.recipe;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import uk.ac.aston.cs3mdd.mealplanner.data.recipe.enums.EnumImageType;

@Entity
public class Recipe implements Serializable {

    public Recipe() { uri = "null"; }

    @PrimaryKey @NonNull
    private String uri;
    private String label;
    private String image;

    // image is required to be stored as the api does not allow permanent
    // access to the images through the url
    @ColumnInfo(name = "localImage", typeAffinity = ColumnInfo.BLOB)
    private byte[] localImage;
    private String source;
    private String url;
    private String shareAs;
    private int yield;
    private List<String> dietLabels;
    private List<String> healthLabels;
    private List<String> cautions;
    private List<String> ingredientLines;
    private List<Ingredient> ingredients;
    private double calories;
    private double glycemicIndex;
    private double inflammatoryIndex;
    private String totalCO2Emissions;
    private String co2EmissionsClass;
    private double totalWeight;
    private List<String> cuisineType;
    private List<String> mealType;
    private List<String> dishType;
    private List<String> instructions;
    private List<String> tags;
    private String externalId;
    private Map<String, Nutrient> totalNutrients;
    private Map<String, Nutrient> totalDaily;

    private float totalTime;
    private List<Digest> digest;

    private Map<EnumImageType, Image> images;

    @NonNull
    public String getUri() {
        return uri;
    }

    public void setUri(@NonNull String uri) {
        this.uri = uri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShareAs() {
        return shareAs;
    }

    public void setShareAs(String shareAs) {
        this.shareAs = shareAs;
    }

    public int getYield() {
        return yield;
    }

    public void setYield(int yield) {
        this.yield = yield;
    }

    public List<String> getDietLabels() {
        return dietLabels;
    }

    public void setDietLabels(List<String> dietLabels) {
        this.dietLabels = dietLabels;
    }

    public List<String> getHealthLabels() {
        return healthLabels;
    }

    public void setHealthLabels(List<String> healthLabels) {
        this.healthLabels = healthLabels;
    }

    public List<String> getCautions() {
        return cautions;
    }

    public void setCautions(List<String> cautions) {
        this.cautions = cautions;
    }

    public List<String> getIngredientLines() {
        return ingredientLines;
    }

    public void setIngredientLines(List<String> ingredientLines) {
        this.ingredientLines = ingredientLines;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getGlycemicIndex() {
        return glycemicIndex;
    }

    public void setGlycemicIndex(double glycemicIndex) {
        this.glycemicIndex = glycemicIndex;
    }

    public double getInflammatoryIndex() {
        return inflammatoryIndex;
    }

    public void setInflammatoryIndex(double inflammatoryIndex) {
        this.inflammatoryIndex = inflammatoryIndex;
    }

    public String getTotalCO2Emissions() {
        return totalCO2Emissions;
    }

    public void setTotalCO2Emissions(String totalCO2Emissions) {
        this.totalCO2Emissions = totalCO2Emissions;
    }

    public String getCo2EmissionsClass() {
        return co2EmissionsClass;
    }

    public void setCo2EmissionsClass(String co2EmissionsClass) {
        this.co2EmissionsClass = co2EmissionsClass;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public List<String> getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(List<String> cuisineType) {
        this.cuisineType = cuisineType;
    }

    public List<String> getMealType() {
        return mealType;
    }

    public void setMealType(List<String> mealType) {
        this.mealType = mealType;
    }

    public List<String> getDishType() {
        return dishType;
    }

    public void setDishType(List<String> dishType) {
        this.dishType = dishType;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Map<String, Nutrient> getTotalNutrients() {
        return totalNutrients;
    }

    public void setTotalNutrients(Map<String, Nutrient> totalNutrients) {
        this.totalNutrients = totalNutrients;
    }

    public Map<String, Nutrient> getTotalDaily() {
        return totalDaily;
    }

    public void setTotalDaily(Map<String, Nutrient> totalDaily) {
        this.totalDaily = totalDaily;
    }

    public List<Digest> getDigest() {
        return digest;
    }

    public void setDigest(List<Digest> digest) {
        this.digest = digest;
    }

    public Map<EnumImageType, Image> getImages() {
        return images;
    }

    public void setImages(Map<EnumImageType, Image> images) {
        this.images = images;
    }

    public float getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(float totalTime) {
        this.totalTime = totalTime;
    }

    public byte[] getLocalImage() {
        return localImage;
    }

    public void setLocalImage(byte[] localImage) {
        this.localImage = localImage;
    }
}
