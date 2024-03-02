package uk.ac.aston.cs3ip.plannify.utils;

import android.content.Context;
import android.widget.Toast;

import java.util.HashMap;

import uk.ac.aston.cs3ip.plannify.R;
import uk.ac.aston.cs3ip.plannify.enums.EnumMealType;

public class Utilities {

//    Meal Types
    private static final String POPULAR = "popular";
    private static final String BREAKFAST = "breakfast";
    private static final String BRUNCH = "brunch";
    private static final String LUNCH = "lunch";
    private static final String SNACK = "snack";
    private static final String TEA_TIME = "tea_time";
    private static final String DINNER = "dinner";

//    Nutrients
    private static final String FAT = "FAT";
    private static final String SUGAR = "SUGAR";

//    Nutrients recommended quantity per 100g by NHS
//    Reference: https://www.nhs.uk/live-well/eat-well/food-guidelines-and-food-labels/how-to-read-food-labels/
    private static final float MAX_TOTAL_FAT_PER_100G = 17.5f;
    private static final int MAX_SATURATED_FAT_PER_100G = 5;
    private static final float MAX_SUGARS_PER_100G = 22.5f;
    private static final float MAX_SALT_PER_100G = 1.5f;

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
     * Returns the appropriate icon based on the meal type requested.
     *
     * @param mealType meal type requested.
     * @return the icon associated with the specified meal type.
     */
    public static int getIconBasedOnMealType(String mealType) {
        String meal = mealType.toLowerCase();

        switch (meal) {
            case POPULAR:
                return R.drawable.ic_popular;
            case BREAKFAST:
                return R.drawable.ic_breakfast;
            case BRUNCH:
                return R.drawable.ic_brunch;
            case LUNCH:
                return R.drawable.ic_lunch;
            case SNACK:
                return R.drawable.ic_snack;
            case TEA_TIME:
                return R.drawable.ic_tea_time;
            case DINNER:
                return R.drawable.ic_dinner;
            default:
                return R.drawable.ic_breakfast;
        }
    }

    /**
     * Analyses the given recipe and calculates its health rating
     * based on the guidelines set by NHS.
     * Guidelines can be found at: <a href="https://www.nhs.uk/live-well/eat-well/food-guidelines-and-food-labels/how-to-read-food-labels/#:~:text=If%20you">...</a>'re%20buying%20pre,saturated%20fat%2C%20sugars%20and%20salt.
     *
     * @return the health rating for the given meal.
     */
//    public static String getMealHealthRating(Recipe recipe) {
//        // fat content is the first element in the array
//        // according to the API documentation https://developer.edamam.com/edamam-docs-recipe-api#/
//
//        int rating = 0;
//
//        // get the nutrients required for comparison
//        float totalFat = 0;
//        float sugar = 0;
//        double saturatedFat = recipe.getDigest().get(0).getSub().get(0).getTotal();
//        double weight = recipe.getTotalWeight();
//
//        Nutrient fatNutrient = recipe.getTotalNutrients().get(FAT);
//        Nutrient sugarNutrient = recipe.getTotalNutrients().get(SUGAR);
//
//        if (fatNutrient != null)
//             totalFat = fatNutrient.getQuantity();
//
//        if (sugarNutrient != null)
//            sugar = sugarNutrient.getQuantity();
//
//
//        // calculate the nutrients for 100g portions, as NHS guidelines reference 100g portions
//        // Reference: https://www.ifsqn.com/forum/index.php/topic/42843-how-to-convert-per-serving-nutritionals-to-100-gram-nutritionals/#:~:text=Say%2C%20you%20have%20175%20g,100%20g%20is%201.15%20g.
//        double totalFatPer100g = (totalFat/weight) * 100;
//        double sugarPer100g = (sugar/weight) * 100;
//        double saturatedFatPer100g = (saturatedFat/weight) * 100;
//
//        // NHS Guidelines
//        double maxTotalFatPer100g = 17.5;
//        double maxSaturatedFatPer100g = 5;
//        double maxSugarPer100g = 22.5;
//
//        // compare values
//        if (totalFatPer100g < maxTotalFatPer100g) rating++;
//        if (sugarPer100g < maxSugarPer100g) rating++;
//        if (saturatedFatPer100g < maxSaturatedFatPer100g) rating++;
//
//        // calculate rating
//        switch (rating) {
//            case 0:
//                return "Very Unhealthy";
//            case 1:
//                return "Unhealthy";
//            case 2:
//                return "Healthy";
//            case 3:
//                return "Very Healthy";
//            default:
//                return "n/a";
//        }
//    }

    public static float sodiumToSaltInGrams(float sodium) {
        // conversion formula is sodium / 400 to get salt in grams
        return sodium / 400;
    }

    public static float saltToSodiumInMilligrams(float salt) {
        return salt * 400;
    }

    public static String getMealHealthRating(uk.ac.aston.cs3ip.plannify.models.api_recipe.Recipe recipe) {
        String rating = "N/A";
        int healthScore = recipe.getHealthScore();

        if (healthScore > 0 && healthScore <= 20) rating = "Very Unhealthy";
        else if (healthScore > 20 && healthScore <= 40) rating = "Unhealthy";
        else if (healthScore > 40 && healthScore <= 60) rating = "A Little Healthy";
        else if (healthScore > 60 && healthScore <= 80) rating = "Healthy";
        else if (healthScore > 80 && healthScore <= 100) rating = "Very Healthy";

        return rating;
    }

    public static String capitaliseString(String value) {
        if (value != null && !value.isEmpty()) {
            return value.substring(0,1).toUpperCase() + value.substring(1).toLowerCase();
        }

        return null;
    }

    public static String getHoursFromMinutes(int minutes) {
        if (minutes >= 60) {
            return minutes/60 + "h";
        }

        return minutes + "m";
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

    public static EnumMealType getMealTypeFromTime(int hour) {
        if (hour > 6 && hour < 10) {
            return EnumMealType.BREAKFAST;
        } else if (hour == 10 || hour == 11) {
            return EnumMealType.BRUNCH;
        } else if (hour == 12 || hour == 13) {
            return  EnumMealType.LUNCH;
        } else if (hour == 15 || hour == 16) {
            return EnumMealType.SNACK;
        } else if (hour > 16 && hour < 19) {
            return EnumMealType.DINNER;
        }

        return EnumMealType.LUNCH;//default
    }

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
