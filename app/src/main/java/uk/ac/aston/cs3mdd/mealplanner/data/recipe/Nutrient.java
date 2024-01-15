package uk.ac.aston.cs3mdd.mealplanner.data.recipe;

import java.io.Serializable;

public class Nutrient implements Serializable {
    private String label;
    private float quantity;
    private String unit;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
