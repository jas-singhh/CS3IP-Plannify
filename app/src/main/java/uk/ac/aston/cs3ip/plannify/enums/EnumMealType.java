package uk.ac.aston.cs3ip.plannify.enums;

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

    public static EnumMealType getEnumMealTypeFromValue(String value) {
        value = value.toLowerCase();
        for (EnumMealType enumValue: EnumMealType.values()) {
            if(enumValue.toString().toLowerCase().equals(value)) {
                return enumValue;
            }
        }
        return null;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getMealType();
    }
}
