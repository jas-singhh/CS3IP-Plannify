package uk.ac.aston.cs3ip.plannify.models.api_recipe;

import java.io.Serializable;

public class Nutrient implements Serializable {

    private String name;
    private float amount;
    private String unit;
    private float percentOfDailyNeeds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getPercentOfDailyNeeds() {
        return percentOfDailyNeeds;
    }

    public void setPercentOfDailyNeeds(float percentOfDailyNeeds) {
        this.percentOfDailyNeeds = percentOfDailyNeeds;
    }
}
