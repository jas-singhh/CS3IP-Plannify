package uk.ac.aston.cs3mdd.mealplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import uk.ac.aston.cs3mdd.mealplanner.R;
import uk.ac.aston.cs3mdd.mealplanner.models.api_recipe.ExtendedIngredient;

public class CustomMealIngredientsAdapter extends RecyclerView.Adapter<CustomMealIngredientsAdapter.MyViewHolder> {
    // reference: https://developer.android.com/develop/ui/views/layout/recyclerview

    private final ArrayList<ExtendedIngredient> localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView ingredientImageParent;
        private final TextView ingredientText;
        private final TextView ingredientsQuantity;

        public MyViewHolder(View view) {
            super(view);
            ingredientImageParent = view.findViewById(R.id.ingredient_image_container);
            ingredientText = view.findViewById(R.id.ingredient_name);
            ingredientsQuantity = view.findViewById(R.id.ingredient_quantity);
        }

        public MaterialCardView getIngredientImageParent() {
            return ingredientImageParent;
        }

        public TextView getIngredientText() {
            return ingredientText;
        }

        public TextView getIngredientsQuantity() {
            return ingredientsQuantity;
        }
    }

    public CustomMealIngredientsAdapter(ArrayList<ExtendedIngredient> dataSet) {
        localDataSet = dataSet;
    }

    @NonNull
    @Override
    public CustomMealIngredientsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient_row, parent, false);

        return new CustomMealIngredientsAdapter.MyViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull CustomMealIngredientsAdapter.MyViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        // hide the image as it is not displayed here
        holder.getIngredientImageParent().setVisibility(View.GONE);

        String quantityAndUnits = localDataSet.get(position).getAmount() +
                localDataSet.get(position).getMeasures().getMetric().getUnitShort();

        holder.getIngredientText().setText(localDataSet.get(position).getName());
        holder.getIngredientsQuantity().setText(quantityAndUnits);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    /**
     * Adds the given ingredient and updates the list.
     *
     * @param ingredient ingredient to be added to the list.
     */
    public void addSingleItem(ExtendedIngredient ingredient) {
        if (ingredient != null) {
            localDataSet.add(ingredient);
            // no need to check if the list is empty as we just added an item
            notifyItemInserted(localDataSet.size() - 1);
        }
    }


    /**
     * Returns the list containing the ingredients.
     *
     * @return the list of ingredients.
     */
    public ArrayList<ExtendedIngredient> getIngredients() {
        return localDataSet;
    }

}
