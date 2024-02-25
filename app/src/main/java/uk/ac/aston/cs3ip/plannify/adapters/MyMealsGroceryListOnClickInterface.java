package uk.ac.aston.cs3ip.plannify.adapters;

import uk.ac.aston.cs3ip.plannify.models.api_recipe.ExtendedIngredient;

public interface MyMealsGroceryListOnClickInterface {

    void onClickGroceryItem(ExtendedIngredient groceryItem, boolean isChecked);

}
