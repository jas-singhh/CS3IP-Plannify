package uk.ac.aston.cs3ip.plannify.test_utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import uk.ac.aston.cs3ip.plannify.enums.EnumMealType;
import uk.ac.aston.cs3ip.plannify.models.local_recipe.LocalRecipe;
import uk.ac.aston.cs3ip.plannify.models.network_recipe.AnalyzedInstruction;
import uk.ac.aston.cs3ip.plannify.models.network_recipe.ExtendedIngredient;
import uk.ac.aston.cs3ip.plannify.models.network_recipe.Measures;
import uk.ac.aston.cs3ip.plannify.models.network_recipe.Metric;
import uk.ac.aston.cs3ip.plannify.models.network_recipe.Nutrient;
import uk.ac.aston.cs3ip.plannify.models.network_recipe.Nutrition;

public class TestUtils {

    /**
     * Creates a sample local recipe with placeholder data.
     *
     * @return a sample local recipe with placeholder data.
     */
    public static LocalRecipe createSampleLocalRecipe() {
        LocalRecipe localRecipe = new LocalRecipe();
        localRecipe.setId(1);
        localRecipe.setServings(1);
        localRecipe.setTitle("Sample Recipe");
        localRecipe.setCreditsText("Chef Name");
        localRecipe.setReadyInMinutes(25);
        localRecipe.setPreparationMinutes(25);
        localRecipe.setCookingMinutes(25);
        localRecipe.setAggregateLikes(1);
        localRecipe.setUsedIngredientCount(10);
        localRecipe.setWeightWatcherSmartPoints(10);
        localRecipe.setSpoonacularScore(20d);
        localRecipe.setPricePerServing(2.50);
        localRecipe.setLikes(1);
        localRecipe.setHealthScore(100);
        localRecipe.setMissedIngredientCount(1);
        localRecipe.setCheap(false);
        localRecipe.setVeryHealthy(false);
        localRecipe.setVeryPopular(false);
        localRecipe.setVeryHealthy(false);
        localRecipe.setVegetarian(true);
        localRecipe.setVegan(false);
        localRecipe.setSustainable(true);
        localRecipe.setLowFodmap(false);
        localRecipe.setDairyFree(false);
        localRecipe.setSummary("Sample local recipe summary");

        // nutrition
        Nutrition nutrition = new Nutrition();
        ArrayList<Nutrient> nutrients = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            Nutrient nutrient = new Nutrient();
            nutrient.setName("Nutrient" + i);
            nutrient.setAmount(i);
            nutrient.setUnit("Sample Unit");
        }
        nutrition.setNutrients(nutrients);
        localRecipe.setNutrition(nutrition);

        // ingredients
        ArrayList<ExtendedIngredient> ingredients = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            ExtendedIngredient ingredient = new ExtendedIngredient();
            ingredient.setId(i);
            ingredient.setName("Ingredient" + i);
            ingredient.setAmount(i);
            Measures measures = new Measures();
            Metric metric = new Metric();
            metric.setAmount(i);
            metric.setUnitShort("Unit Short" + i);
            metric.setUnitLong("Unit Long" + i);
            measures.setMetric(metric);
            ingredient.setMeasures(measures);
            ingredients.add(ingredient);
        }
        localRecipe.setExtendedIngredients(ingredients);

        // instructions
        ArrayList<AnalyzedInstruction> instructions = new ArrayList<>(2);
        AnalyzedInstruction instruction1 = new AnalyzedInstruction();
        instruction1.setName("Step 1");
        instruction1.setSteps(new ArrayList<>());
        AnalyzedInstruction instruction2 = new AnalyzedInstruction();
        instruction2.setName("Step 2");
        instruction2.setSteps(new ArrayList<>());
        instructions.add(instruction1);
        instructions.add(instruction2);
        localRecipe.setAnalyzedInstructions(instructions);
        localRecipe.setGaps("gaps");
        localRecipe.setSourceUrl("Source url");
        localRecipe.setSourceName("Source name");

        localRecipe.setCuisines(new ArrayList<>(Arrays.asList("Italian", "European")));
        localRecipe.setDiets(new ArrayList<>());
        localRecipe.setDishTypes(new ArrayList<>(Arrays.asList("Breakfast", "Brunch")));
        localRecipe.setMealTypeSavedFor(EnumMealType.BREAKFAST);
        localRecipe.setDateSavedFor(LocalDate.now());

        return localRecipe;
    }
}
