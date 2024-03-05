package uk.ac.aston.cs3ip.plannify.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import uk.ac.aston.cs3ip.plannify.R;
import uk.ac.aston.cs3ip.plannify.models.network_recipe.ExtendedIngredient;

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
        private final TextView ingredientAisle;

        public MyViewHolder(View view) {
            super(view);
            ingredientImageParent = view.findViewById(R.id.ingredient_image_container);
            ingredientText = view.findViewById(R.id.ingredient_name);
            ingredientsQuantity = view.findViewById(R.id.ingredient_quantity);
            ingredientAisle = view.findViewById(R.id.ingredient_aisle);
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

        public TextView getIngredientAisle() {
            return ingredientAisle;
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

        // hide aisle as it is not displayed here
        holder.getIngredientAisle().setVisibility(View.GONE);


        String quantityAndUnit = getIngredientQuantityText(localDataSet.get(position)) +
                " " + localDataSet.get(position).getMeasures().getMetric().getUnitLong();

        holder.getIngredientText().setText(localDataSet.get(position).getName());
        holder.getIngredientsQuantity().setText(quantityAndUnit);
    }

    private String getIngredientQuantityText(ExtendedIngredient ingredient) {
        String quantity = "";// default

        if (ingredient.getMeasures() != null) {
            float amount = ingredient.getMeasures().getMetric().getAmount();
            if (ingredient.getMeasures().getMetric().getAmount() < 1) {
                quantity += getFractionFromDecimal(amount);
            } else {
                // if quantity is a whole number - then do not display the decimals
                if (amount % 1 == 0) {
                    // it is a whole number
                    int amountWholeNumber = (int) amount;
                    quantity += amountWholeNumber;
                } else {
                    // number contains decimals
                    quantity += amount;
                }
            }
        } else {
            quantity += "N/A";
        }

        return quantity;
    }

    /**
     * Returns a String including a fraction from the specified number if it is
     * less than 1.
     *
     * @param number number of which the fraction is returned.
     * @return a String containing the fraction for the given number if it is less than 1.
     */
    private String getFractionFromDecimal(double number) {
        int[] fraction = new int[2];

        // fraction is returned only if number is less than 1
        if (number < 1) {
            fraction[0] = 1;
            double denominator = 1 / number;
            fraction[1] = (int) Math.round(denominator);
        }

        return fraction[0] + "/" + fraction[1];
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

    public void clearDataSet() {
        if (localDataSet != null && !localDataSet.isEmpty()) {
            localDataSet.clear();
            notifyDataSetChanged();
        }
    }

}
