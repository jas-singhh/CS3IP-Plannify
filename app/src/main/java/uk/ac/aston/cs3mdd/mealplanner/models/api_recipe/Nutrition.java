package uk.ac.aston.cs3mdd.mealplanner.models.api_recipe;

import java.io.Serializable;
import java.util.List;

public class Nutrition implements Serializable {

    private List<Nutrient> nutrients;

    public List<Nutrient> getNutrients() {
        return nutrients;
    }

    public void setNutrients(List<Nutrient> nutrients) {
        this.nutrients = nutrients;
    }
}
