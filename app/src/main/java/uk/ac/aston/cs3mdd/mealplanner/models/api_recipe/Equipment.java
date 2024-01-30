package uk.ac.aston.cs3mdd.mealplanner.models.api_recipe;

import java.io.Serializable;

public class Equipment implements Serializable {

    private long id;
    private String name;
    private String localizedName;
    private String image;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public String getImage() {
        return image;
    }
}
