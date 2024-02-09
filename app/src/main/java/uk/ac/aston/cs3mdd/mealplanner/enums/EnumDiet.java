package uk.ac.aston.cs3mdd.mealplanner.enums;

import androidx.annotation.NonNull;

import java.util.Random;

public enum EnumDiet {

    // Reference: https://stackoverflow.com/questions/7191634/set-value-to-enum-java
    GLUTEN_FREE("Gluten Free"),
    KETOGENIC("Ketogenic"),
    VEGETARIAN("Vegetarian"),
    LACTO_VEGETARIAN("Lacto-Vegeterian"),
    OVO_VEGETARIAN("Ovo-Vegetarian"),
    VEGAN("Vegan"),
    PESCETARIAN("Pescetarian"),
    PALEO("Paleo"),
    PRIMAL("Primal"),
    LOW_FODMAP("Low FODMAP"),
    WHOLE_30("Whole30");

    private final String diet;
    EnumDiet(String diet) {
        this.diet = diet;
    }

    public String getDiet() {
        return diet;
    }

    public static EnumDiet getRandomDiet() {
        EnumDiet[] diets = EnumDiet.values();
        Random random = new Random();
        return diets[random.nextInt(diets.length - 1)];
    }

    @NonNull
    @Override
    public String toString() {
        return this.getDiet();
    }
}
