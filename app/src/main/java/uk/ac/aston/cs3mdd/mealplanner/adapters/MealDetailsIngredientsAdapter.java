package uk.ac.aston.cs3mdd.mealplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import uk.ac.aston.cs3mdd.mealplanner.R;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Ingredient;
import uk.ac.aston.cs3mdd.mealplanner.utils.Utilities;

public class MealDetailsIngredientsAdapter extends RecyclerView.Adapter<MealDetailsIngredientsAdapter.MyViewHolder> {

    // using array as the size of the ingredients will be known and
    // it can save some memory.
    private final Ingredient[] localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatImageView ingredientImage;
        private final TextView ingredientQuantity;
        private final TextView ingredientName;

        public MyViewHolder(View view) {
            super(view);
            ingredientImage = view.findViewById(R.id.ingredient_image);
            ingredientQuantity = view.findViewById(R.id.ingredient_quantity);
            ingredientName = view.findViewById(R.id.ingredient_name);

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
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public MealDetailsIngredientsAdapter(Ingredient[] dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_ingredient_column, viewGroup, false);

        return new MyViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Picasso.get().load(localDataSet[position].getImage()).into(viewHolder.getIngredientImage());
        String quantity = localDataSet[position].getQuantity() + " " + localDataSet[position].getMeasure();
        viewHolder.getIngredientQuantity().setText(quantity);
        viewHolder.getIngredientName().setText(Utilities.capitaliseString(localDataSet[position].getFood()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }

}
