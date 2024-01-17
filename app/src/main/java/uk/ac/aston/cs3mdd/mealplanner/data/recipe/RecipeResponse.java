package uk.ac.aston.cs3mdd.mealplanner.data.recipe;

import java.util.List;

public class RecipeResponse {

    private List<Hit> hits;

    public List<Hit> getHits() {
        return hits;
    }

    public void setHits(List<Hit> hits) {
        this.hits = hits;
    }
}
