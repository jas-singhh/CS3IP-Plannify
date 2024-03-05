package uk.ac.aston.cs3ip.plannify.models.network_recipe;

import java.io.Serializable;

public class Metric implements Serializable {

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
