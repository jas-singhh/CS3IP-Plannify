package uk.ac.aston.cs3mdd.mealplanner.enums;

import androidx.annotation.NonNull;

import java.util.Random;

public enum EnumCuisine {
    // These cuisine types supported by the API, as mentioned in the docs
    // reference: https://spoonacular.com/food-api/docs#Cuisines

    AFRICAN("African"),
    ASIAN("Asian"),
    AMERICAN("American"),
    BRITISH("British"),
    CAJUN("Cajun"),
    CARIBBEAN("Caribbean"),
    CHINESE("Chinese"),
    EASTERN_EUROPE("Eastern Europe"),
    EUROPEAN("European"),
    FRENCH("French"),
    GERMAN("German"),
    GREEK("Greek"),
    INDIAN("Indian"),
    IRISH("Irish"),
    ITALIAN("Italian"),
    JAPANESE("Japanese"),
    JEWISH("Jewish"),
    KOREAN("Korean"),
    LATIN_AMERICAN("Latin American"),
    MEDITERRANEAN("Mediterranean"),
    MEXICAN("Mexican"),
    MIDDLE_EASTERN("Middle Eastern"),
    NORDIC("Nordic"),
    SOUTHERN("Southern"),
    SPANISH("Spanish"),
    THAI("Thai"),
    VIETNAMESE("Vietnamese");


    private final String cuisine;

    EnumCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getCuisine() {
        return cuisine;
    }
    public static EnumCuisine getRandomCuisine() {
        EnumCuisine[] cuisines = EnumCuisine.values();
        Random random = new Random();
        return cuisines[random.nextInt(cuisines.length - 1)];
    }

    @NonNull
    @Override
    public String toString() {
        return this.getCuisine();
    }
}
