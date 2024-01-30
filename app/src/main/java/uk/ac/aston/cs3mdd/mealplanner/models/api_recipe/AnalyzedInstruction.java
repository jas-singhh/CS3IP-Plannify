package uk.ac.aston.cs3mdd.mealplanner.models.api_recipe;

import java.io.Serializable;
import java.util.ArrayList;

public class AnalyzedInstruction implements Serializable {

    private String name;
    private ArrayList<Step> steps;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }
}
