package uk.ac.aston.cs3mdd.mealplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import uk.ac.aston.cs3mdd.mealplanner.R;
import uk.ac.aston.cs3mdd.mealplanner.models.api_recipe.ExtendedIngredient;
public class GroceryListAdapter extends RecyclerView.Adapter<GroceryListAdapter.MyViewHolder> {

    // reference: https://developer.android.com/develop/ui/views/layout/recyclerview

    private ArrayList<ExtendedIngredient> localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final CheckBox itemCheckbox;
        private final TextView itemName;
        private final TextView itemQuantity;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            itemCheckbox = itemView.findViewById(R.id.grocery_checkbox);
            itemName = itemView.findViewById(R.id.tv_grocery_item_name);
            itemQuantity = itemView.findViewById(R.id.tv_grocery_item_quantity);
        }

        public CheckBox getItemCheckbox() {
            return itemCheckbox;
        }

        public TextView getItemName() {
            return itemName;
        }

        public TextView getItemQuantity() {
            return itemQuantity;
        }
    }

    public GroceryListAdapter(ArrayList<ExtendedIngredient> dataset) {
        localDataSet = dataset;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public GroceryListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grocery_list,
                parent, false);

        return new MyViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull GroceryListAdapter.MyViewHolder holder, int position) {
        // round the quantity amount
        String quantity = Math.round(localDataSet.get(position).getAmount()) + localDataSet.get(position).getMeasures().getMetric().getUnitShort();

        holder.itemName.setText(localDataSet.get(position).getName());
        holder.itemQuantity.setText(quantity);
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
    public void updateData(ArrayList<ExtendedIngredient> dataSet) {
        if (dataSet != null) {
            localDataSet = dataSet;
            notifyDataSetChanged();
        }
    }

}
