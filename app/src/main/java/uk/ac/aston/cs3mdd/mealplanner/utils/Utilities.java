package uk.ac.aston.cs3mdd.mealplanner.utils;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.ac.aston.cs3mdd.mealplanner.R;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Digest;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Hit;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Nutrient;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Recipe;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Sub;

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
    private static final double MAX_TOTAL_FAT_PER_100G = 17.5;
    private static final int MAX_SATURATED_FAT_PER_100G = 5;
    private static final double MAX_SUGARS_PER_100G = 22.5;


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
     * @param recipe recipe to analyse.
     * @return the health rating for the given meal.
     */
    public static String getMealHealthRating(Recipe recipe) {
        // fat content is the first element in the array
        // according to the API documentation https://developer.edamam.com/edamam-docs-recipe-api#/

        int rating = 0;

        // get the nutrients required for comparison
        float totalFat = 0;
        float sugar = 0;
        double saturatedFat = recipe.getDigest().get(0).getSub().get(0).getTotal();
        double weight = recipe.getTotalWeight();

        Nutrient fatNutrient = recipe.getTotalNutrients().get(FAT);
        Nutrient sugarNutrient = recipe.getTotalNutrients().get(SUGAR);

        if (fatNutrient != null)
             totalFat = fatNutrient.getQuantity();

        if (sugarNutrient != null)
            sugar = sugarNutrient.getQuantity();


        // calculate the nutrients for 100g portions, as NHS guidelines reference 100g portions
        // Reference: https://www.ifsqn.com/forum/index.php/topic/42843-how-to-convert-per-serving-nutritionals-to-100-gram-nutritionals/#:~:text=Say%2C%20you%20have%20175%20g,100%20g%20is%201.15%20g.
        double totalFatPer100g = (totalFat/weight) * 100;
        double sugarPer100g = (sugar/weight) * 100;
        double saturatedFatPer100g = (saturatedFat/weight) * 100;

        // NHS Guidelines
        double maxTotalFatPer100g = 17.5;
        double maxSaturatedFatPer100g = 5;
        double maxSugarPer100g = 22.5;

        // compare values
        if (totalFatPer100g < maxTotalFatPer100g) rating++;
        if (sugarPer100g < maxSugarPer100g) rating++;
        if (saturatedFatPer100g < maxSaturatedFatPer100g) rating++;

        // calculate rating
        switch (rating) {
            case 0:
                return "Very Unhealthy";
            case 1:
                return "Unhealthy";
            case 2:
                return "Healthy";
            case 3:
                return "Very Healthy";
            default:
                return "n/a";
        }
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

    public static String getListOfNutrientsFromRecipe(Recipe recipe) {
        if (recipe==null) return "";

        StringBuilder result = new StringBuilder();

        StringBuilder fatSubs = new StringBuilder();

        for (Sub fatSub: recipe.getDigest().get(0).getSub()) {
            String formattedQuantity = String.format(Locale.getDefault(),"%.2f", fatSub.getTotal());
            fatSubs.append(fatSub.getLabel()).append(": ").append(formattedQuantity).append("g\n");
        }

        StringBuilder carbsSubs = new StringBuilder();

        for (Sub carbsSub: recipe.getDigest().get(1).getSub()) {
            String formattedQuantity = String.format(Locale.getDefault(),"%.2f", carbsSub.getTotal());
            carbsSubs.append(carbsSub.getLabel()).append(": ").append(formattedQuantity).append("g\n");
        }

        for (Digest nutrient: recipe.getDigest()) {
            result.append(nutrient.getLabel()).append(": ").append(String.format(Locale.getDefault(),
                    "%.2f", nutrient.getTotal())).append("g\n");
        }

        result = new StringBuilder(result + fatSubs.toString() + carbsSubs);

        return result.toString();
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

    public static ArrayList<Recipe> fromHitsToRecipes(List<Hit> hits) {
        if (hits == null || hits.isEmpty()) return null;

        ArrayList<Recipe> result = new ArrayList<>(hits.size());
        for (int i = 0; i < hits.size(); i++) {
            result.add(hits.get(i).getRecipe());
        }

        return result;
    }
}
