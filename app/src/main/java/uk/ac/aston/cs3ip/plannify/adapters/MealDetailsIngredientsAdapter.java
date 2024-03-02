package uk.ac.aston.cs3ip.plannify.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import uk.ac.aston.cs3ip.plannify.R;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.ExtendedIngredient;
import uk.ac.aston.cs3ip.plannify.utils.Utilities;

public class MealDetailsIngredientsAdapter extends RecyclerView.Adapter<MealDetailsIngredientsAdapter.MyViewHolder> {
    // reference: https://developer.android.com/develop/ui/views/layout/recyclerview

    // using array as the size of the ingredients will be known and
    // it can save some memory.
    private final ExtendedIngredient[] localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatImageView ingredientImage;
        private final TextView ingredientQuantity;
        private final TextView ingredientName;
        private final TextView ingredientAisle;

        public MyViewHolder(View view) {
            super(view);
            ingredientImage = view.findViewById(R.id.ingredient_image);
            ingredientQuantity = view.findViewById(R.id.ingredient_quantity);
            ingredientName = view.findViewById(R.id.ingredient_name);
            ingredientAisle = view.findViewById(R.id.ingredient_aisle);

            // Define click listener for the ViewHolder's View
        }

        public AppCompatImageView getIngredientImage() {
            return ingredientImage;
        }

        public TextView getIngredientQuantity() {
            return ingredientQuantity;
        }

        public TextView getIngredientName() {
            return ingredientName;
        }

        public TextView getIngredientAisle() {
            return ingredientAisle;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     *                by RecyclerView
     */
    public MealDetailsIngredientsAdapter(ExtendedIngredient[] dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_ingredient_row, viewGroup, false);

        return new MyViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        // get image using the URL provided by the API docs
        String url = "https://spoonacular.com/cdn/ingredients_100x100/" + localDataSet[position].getImage();
        Picasso.get().load(url)
                .placeholder(R.drawable.img_image_not_available)
                .into(viewHolder.getIngredientImage());

        String name = "Name: " + Utilities.capitaliseString(localDataSet[position].getName());

        viewHolder.getIngredientQuantity().setText(getIngredientQuantityText(localDataSet[position]));
        viewHolder.getIngredientName().setText(name);
        viewHolder.getIngredientAisle().setText(getIngredientAisleText(localDataSet[position]));
    }

    private String getIngredientQuantityText(ExtendedIngredient ingredient) {
        String quantity = "Quantity: ";

        if (ingredient.getMeasures() != null) {
            float amount = ingredient.getMeasures().getMetric().getAmount();
            if (ingredient.getMeasures().getMetric().getAmount() < 1) {
                quantity += getFractionFromDecimal(amount) +
                        " " + ingredient.getMeasures().getMetric().getUnitLong().toLowerCase();
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
                quantity += " " + ingredient.getMeasures().getMetric().getUnitLong().toLowerCase();
            }
        } else {
            quantity += "N/A";
        }

        return quantity;
    }

    private String getIngredientAisleText(ExtendedIngredient ingredient) {
        String aisle = "Aisle: ";

        if (ingredient.getAisle() != null) {
            aisle += Utilities.capitaliseString(ingredient.getAisle());
        } else {
            aisle += "N/A";
        }

        return aisle;
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
        return localDataSet.length;
    }

}
