package uk.ac.aston.cs3ip.plannify.models.local_recipe;

import androidx.annotation.Nullable;

import java.util.Objects;

import uk.ac.aston.cs3ip.plannify.models.api_recipe.ExtendedIngredient;

/**
 * Grocery Item is the main entity representing each grocery item containing
 * the parent recipe id which it is associated with.
 *
 * @author Jaskaran Singh
 */
public class GroceryItem {
    private final long recipeId;
    private final ExtendedIngredient ingredient;

    public GroceryItem(long recipeId, ExtendedIngredient extendedIngredient) {
        this.recipeId = recipeId;
        this.ingredient = extendedIngredient;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public ExtendedIngredient getIngredient() {
        return ingredient;
    }

    // reference: https://stackoverflow.com/questions/15885991/override-equals-method-only-of-a-java-object
    // compare id if it is a remote recipe grocery item or the name if it is a custom recipe grocery item
    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass())
            return false;

        GroceryItem otherItem = (GroceryItem) obj;


        if (this.getIngredient().getId() != 0 && otherItem.getIngredient().getId() != 0)
            return this.getIngredient().getId() == otherItem.getIngredient().getId();
        else
            return this.getIngredient().getName().equals(otherItem.getIngredient().getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getIngredient().getId(), this.getIngredient().getName());
    }
}
