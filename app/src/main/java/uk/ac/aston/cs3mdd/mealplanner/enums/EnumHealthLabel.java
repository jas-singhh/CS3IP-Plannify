package uk.ac.aston.cs3mdd.mealplanner.enums;

public enum EnumHealthLabel {

    // Reference: https://stackoverflow.com/questions/7191634/set-value-to-enum-java
    ALCOHOL_COCKTAIL("alcohol-cocktail"),
    ALCOHOL_FREE("alcohol-free"),
    CELERY_FREE("celery-free"),
    DAIRY_FREE("dairy-free"),
    EGG_FREE("egg-free"),
    FISH_FREE("fish-free"),
    GLUTEN_FREE("gluten-free"),
    KETO_FRIENDLY("keto-friendly"),
    LOW_POTASSIUM("low-potassium"),
    LOW_SUGAR("low-sugar"),
    PEANUT_FREE("peanut-free"),
    RED_MEAT_FREE("red-meat-free"),
    VEGAN("vegan"),
    VEGETARIAN("vegetarian"),
    WHEAT_FREE("wheat-free");

    private final String healthLabel;
    EnumHealthLabel(String healthLabel) {
        this.healthLabel = healthLabel;
    }

    public String getHealthLabel() {
        return healthLabel;
    }
}
