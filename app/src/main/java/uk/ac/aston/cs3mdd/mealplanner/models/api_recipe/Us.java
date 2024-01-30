package uk.ac.aston.cs3mdd.mealplanner.models.api_recipe;

import java.io.Serializable;

public class Us implements Serializable {

    private float amount;
    private String unitShort;
    private String unitLong;

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getUnitShort() {
        return unitShort;
    }

    public void setUnitShort(String unitShort) {
        this.unitShort = unitShort;
    }

    public String getUnitLong() {
        return unitLong;
    }

    public void setUnitLong(String unitLong) {
        this.unitLong = unitLong;
    }
}
