package uk.ac.aston.cs3mdd.mealplanner.data.recipe.enums;

public enum EnumDiet {

    // Reference: https://stackoverflow.com/questions/7191634/set-value-to-enum-java
    BALANCED("balanced"),
    HIGH_FIBER("high-fiber"),
    HIGH_PROTEIN("high-protein"),
    LOW_CARB("low-card"),
    LOW_FAT("low-fat"),
    LOW_SODIUM("low-sodium");

    private final String diet;
    EnumDiet(String diet) {
        this.diet = diet;
    }

    public String getDiet() {
        return diet;
    }
}
