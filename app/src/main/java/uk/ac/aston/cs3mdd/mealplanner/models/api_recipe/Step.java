package uk.ac.aston.cs3mdd.mealplanner.models.api_recipe;

import java.io.Serializable;
import java.util.ArrayList;

public class Step implements Serializable {

    private int number;
    private String step;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Equipment> equipment;

    public int getNumber() {
        return number;
    }

    public String getStep() {
        return step;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ArrayList<Equipment> getEquipment() {
        return equipment;
    }
}
