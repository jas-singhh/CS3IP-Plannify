package uk.ac.aston.cs3ip.plannify.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uk.ac.aston.cs3ip.plannify.R;
import uk.ac.aston.cs3ip.plannify.models.local_recipe.GroceryItem;
import uk.ac.aston.cs3ip.plannify.models.network_recipe.ExtendedIngredient;
import uk.ac.aston.cs3ip.plannify.utils.Utilities;

public class MyMealsGroceryListItemsAdapter extends RecyclerView.Adapter<MyMealsGroceryListItemsAdapter.MyViewHolder> {
    // reference: https://developer.android.com/develop/ui/views/layout/recyclerview

    // using array as the size of the ingredients will be known and
    // it can save some memory.
    private ArrayList<GroceryItem> localDataSet;
    private final MyMealsGroceryListOnClickInterface mInterface;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView groceryItemParent;
        private final CheckBox groceryItemCheckBox;
        private final AppCompatImageView groceryItemImage;
        private final TextView groceryItemQuantity;
        private final TextView groceryItemName;
        private final TextView groceryItemAisle;

        public MyViewHolder(View view, MyMealsGroceryListOnClickInterface myMealsGroceryListOnClickInterface, List<GroceryItem> localDataSet) {
            super(view);
            groceryItemParent = view.findViewById(R.id.grocery_item_parent);
            groceryItemCheckBox = view.findViewById(R.id.grocery_item_checkbox);
            groceryItemImage = view.findViewById(R.id.grocery_item_image);
            groceryItemQuantity = view.findViewById(R.id.grocery_item_quantity);
            groceryItemName = view.findViewById(R.id.grocery_item_name);
            groceryItemAisle = view.findViewById(R.id.grocery_item_aisle);

            // Define click listener for the ViewHolder's View
            groceryItemCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

                // during the initial set up, this function is called whilst setting up the checked
                // state depending on the data in the database. This causes issues as the function
                // is called multiple times, even when we do not need it, therefore we can check
                // if it is being called during the initial set up or during a button click using
                // isPressed().
                // reference: https://www.reddit.com/r/androiddev/comments/yvpvig/setoncheckedchangelistener_is_getting_called_when/
                if (buttonView.isPressed()) {
                    if (myMealsGroceryListOnClickInterface != null) {
                        int currentPos = getAbsoluteAdapterPosition();
                        if (currentPos != RecyclerView.NO_POSITION) {
                            myMealsGroceryListOnClickInterface.onClickGroceryItem(localDataSet.get(getAbsoluteAdapterPosition()), isChecked);
                        }
                    }
                }
            });
        }

        public MaterialCardView getGroceryItemParent() {
            return groceryItemParent;
        }

        public CheckBox getGroceryItemCheckBox() {
            return groceryItemCheckBox;
        }

        public AppCompatImageView getGroceryItemImage() {
            return groceryItemImage;
        }

        public TextView getGroceryItemQuantity() {
            return groceryItemQuantity;
        }

        public TextView getGroceryItemName() {
            return groceryItemName;
        }

        public TextView getGroceryItemAisle() {
            return groceryItemAisle;
        }

    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     *                by RecyclerView
     */
    public MyMealsGroceryListItemsAdapter(ArrayList<GroceryItem> dataSet, MyMealsGroceryListOnClickInterface myMealsGroceryListOnClickInterface) {
        localDataSet = dataSet;
        mInterface = myMealsGroceryListOnClickInterface;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_grocery_row, viewGroup, false);

        return new MyViewHolder(view, mInterface, localDataSet);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        // update the checkbox state
        viewHolder.getGroceryItemCheckBox().setChecked(localDataSet.get(position).getIngredient().isCheckedInGroceryList());

        // set the container's alpha based on the checkbox state
        float alpha = viewHolder.getGroceryItemCheckBox().isChecked() ? .8f : 1f;
        viewHolder.getGroceryItemParent().setAlpha(alpha);

        // get image using the URL provided by the API docs
        String url = "https://spoonacular.com/cdn/ingredients_100x100/" + localDataSet.get(position).getIngredient().getImage();
        Picasso.get().load(url)
                .error(R.drawable.img_image_not_available)
                .placeholder(R.drawable.img_image_not_available)
                .into(viewHolder.getGroceryItemImage());

        String name = "Name: " + Utilities.capitaliseString(localDataSet.get(position).getIngredient().getName());

        // aisle
        String aisle = "Aisle: ";
        if (localDataSet.get(position).getIngredient().getAisle() == null) aisle += "N/A";
        else aisle += Utilities.capitaliseString(localDataSet.get(position).getIngredient().getAisle());

        viewHolder.getGroceryItemQuantity().setText(getIngredientQuantityText(localDataSet.get(position).getIngredient()));
        viewHolder.getGroceryItemName().setText(name);
        viewHolder.getGroceryItemAisle().setText(aisle);

    }

    private String getIngredientQuantityText(ExtendedIngredient ingredient) {
        String quantity = "";// default

        // use short unit if available - if not use long unit
        String unit = ingredient.getMeasures().getMetric().getUnitShort() != null ?
                ingredient.getMeasures().getMetric().getUnitShort() : ingredient.getMeasures().getMetric().getUnitLong();

        if (ingredient.getMeasures() != null) {
            float amount = ingredient.getMeasures().getMetric().getAmount();
            if (ingredient.getMeasures().getMetric().getAmount() < 1) {
                quantity += getFractionFromDecimal(amount) + " " + unit;
            } else {
                // if quantity is a whole number - then do not display the decimals
                if (amount % 1 == 0) {
                    // it is a whole number
                    int amountWholeNumber = (int) amount;
                    quantity += amountWholeNumber + " " + unit;
                } else {
                    // number contains decimals
                    quantity += Math.round(amount) + " " + unit;
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
     * Updates the current data with the given one.
     *
     * @param dataSet list containing the data to update the current one with.
     */
    public void updateData(ArrayList<GroceryItem> dataSet) {
        if (dataSet != null) {
            localDataSet = dataSet;
            notifyDataSetChanged();
        }
    }

    /**
     * Clears the local data set.
     */
    public void clearDataSet() {
        if (localDataSet != null && !localDataSet.isEmpty()) localDataSet.clear();
    }

}
