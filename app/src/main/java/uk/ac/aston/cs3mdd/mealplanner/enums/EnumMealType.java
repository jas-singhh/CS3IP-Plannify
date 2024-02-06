package uk.ac.aston.cs3mdd.mealplanner.enums;

import androidx.annotation.NonNull;

public enum EnumMealType {

    BREAKFAST("Breakfast"),
    BRUNCH("Brunch"),
    LUNCH("Lunch"),
    SNACK("Snack"),
    TEA_TIME("Beverage"),
    DINNER("Dinner");


    private final String mealType;

    EnumMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getMealType() {
        return mealType;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getMealType();
    }
}
