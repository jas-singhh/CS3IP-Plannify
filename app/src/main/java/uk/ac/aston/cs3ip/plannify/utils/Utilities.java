package uk.ac.aston.cs3ip.plannify.utils;

import android.content.Context;
import android.widget.Toast;

import java.util.HashMap;

import uk.ac.aston.cs3ip.plannify.enums.EnumMealType;
import uk.ac.aston.cs3ip.plannify.models.network_recipe.NetworkRecipe;

public class Utilities {

//    Nutrients recommended quantity per 100g by NHS
//    Reference: https://www.nhs.uk/live-well/eat-well/food-guidelines-and-food-labels/how-to-read-food-labels/
    private static final float MAX_TOTAL_FAT_PER_100G = 17.5f;
    private static final int MAX_SATURATED_FAT_PER_100G = 5;
    private static final float MAX_SUGARS_PER_100G = 22.5f;
    private static final float MAX_SALT_PER_100G = 1.5f;

    /**
     * Returns a map of recommended nutrients and their recommended intake amount for
     * fat, saturated fat, sugar,and sodium.
     *
     * @return a map of recommended nutrients, along with their recommended amount.
     */
    public static HashMap<String, Float> getRecommendedNutrientsMap() {
        // these values are taken from the API docs to match the endpoint parameters
        HashMap<String, Float> result = new HashMap<>(4);
        result.put("maxFat", MAX_TOTAL_FAT_PER_100G);
        result.put("maxSaturatedFat", (float) MAX_SATURATED_FAT_PER_100G);
        result.put("maxSugar", MAX_SUGARS_PER_100G);

        float sodium = saltToSodiumInMilligrams(MAX_SALT_PER_100G);
        result.put("maxSodium", sodium);

        return result;
    }

    /**
     * Calculates and returns the sodium from salt in milligrams.
     *
     * @param salt salt amount from which to calculate sodium amount.
     * @return sodium amount in milligrams from the given salt amount.
     */
    public static float saltToSodiumInMilligrams(float salt) {
        return salt * 400;
    }

    /**
     * Returns the meal health rating based on its health score.
     *
     * @param networkRecipe recipe, for which to return the meal rating.
     * @return meal rating for the given recipe.
     */
    public static String getMealHealthRating(NetworkRecipe networkRecipe) {
        String rating = "N/A";
        int healthScore = networkRecipe.getHealthScore();

        if (healthScore > 0 && healthScore <= 20) rating = "Very Unhealthy";
        else if (healthScore > 20 && healthScore <= 40) rating = "Unhealthy";
        else if (healthScore > 40 && healthScore <= 60) rating = "A Little Healthy";
        else if (healthScore > 60 && healthScore <= 80) rating = "Healthy";
        else if (healthScore > 80 && healthScore <= 100) rating = "Very Healthy";

        return rating;
    }

    /**
     * Calculates the rating in stars depending on the specified health score.
     *
     * @param healthScore health score based on which the rating is calculated.
     * @return rating for the specified health score ranging from 0 to 5.
     */
    public static int getStarRatingFromHealthScore(int healthScore) {
        int result = 0;
        if (healthScore >= 0 && healthScore <= 20) result = 1;
        else if (healthScore > 20 && healthScore <= 40) result = 2;
        else if (healthScore > 40 && healthScore <= 60) result = 3;
        else if (healthScore > 60 && healthScore <= 80) result = 4;
        else if (healthScore > 80 && healthScore <= 100) result = 5;
        return result;
    }

    /**
     * Capitalises the given string.
     *
     * @param value value to capitalise.
     * @return capitalised version of the given value, if not null or empty.
     */
    public static String capitaliseString(String value) {
        if (value != null && !value.isEmpty()) {
            return value.substring(0,1).toUpperCase() + value.substring(1).toLowerCase();
        }

        return null;
    }


    /**
     * Displays an error toast message indicating that an error occurred.
     *
     * @param context context required to display the toast.
     */
    public static void showErrorToast(Context context) {
        if (context != null) {
            Toast.makeText(context, "An Error Occurred", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Returns the meal hour based on the given meal type.
     *
     * @param mealType meal type, based on which to return the meal hour.
     * @return the meal hour based on the given meal type.
     */
    public static int getTimeHourFromMealType(EnumMealType mealType) {
        if (mealType == null) return -1;

        switch (mealType) {
            case BREAKFAST:
                return 8;
            case BRUNCH:
                return 10;
            case LUNCH:
                return 12;
            case SNACK:
            case TEA_TIME:
                return 15;
            case DINNER:
                return 18;
        }

        return -1;//default
    }


    /* Nutrients */

    /**
     * Returns an array of nutrients to limit.
     *
     * @return an array of nutrients to limit.
     */
    public static String[] getNutrientsToLimitNames() {
        String[] result = new String[8];
        result[0] = "Calories";
        result[1] = "Fat";
        result[2] = "Saturated Fat";
        result[3] = "Carbohydrates";
        result[4] = "Net Carbohydrates";
        result[5] = "Sugar";
        result[6] = "Cholesterol";
        result[7] = "Sodium";

        return result;
    }

    /**
     * Returns an array of nutrients to get enough.
     *
     * @return an array of nutrients to get enough.
     */
    public static String[] getNutrientsToGetEnoughNames() {
        String[] result = new String[21];
        result[0] = "Protein";
        result[1] = "Vitamin C";
        result[2] = "Vitamin A";
        result[3] = "Manganese";
        result[4] = "Folate";
        result[5] = "Vitamin E";
        result[6] = "Vitamin B6";
        result[7] = "Fiber";
        result[8] = "Magnesium";
        result[9] = "Phosphorus";
        result[10] = "Potassium";
        result[11] = "Copper";
        result[12] = "Vitamin K";
        result[13] = "Vitamin B2";
        result[14] = "Vitamin B1";
        result[15] = "Iron";
        result[16] = "Vitamin B3";
        result[17] = "Zinc";
        result[18] = "Vitamin B5";
        result[19] = "Calcium";
        result[20] = "Selenium";

        return result;
    }
}
