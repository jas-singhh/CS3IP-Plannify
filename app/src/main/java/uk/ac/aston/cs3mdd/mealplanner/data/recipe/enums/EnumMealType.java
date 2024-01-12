package uk.ac.aston.cs3mdd.mealplanner.data.recipe.enums;

public enum EnumMealType {

    BREAKFAST("Breakfast"),
    BRUNCH("Brunch"),
    LUNCH("Lunch"),
    SNACK("Snack"),
    TEA_TIME("Teatime"),
    DINNER("Dinner");


    private final String mealType;

    EnumMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getMealType() {
        return mealType;
    }
}
