package uk.ac.aston.cs3ip.plannify.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import uk.ac.aston.cs3ip.plannify.R;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.Nutrient;

public class MealDetailsNutrientsAdapter extends RecyclerView.Adapter<MealDetailsNutrientsAdapter.MyViewHolder> {
    // reference: https://developer.android.com/develop/ui/views/layout/recyclerview

    // using array as the size of the ingredients will be known and
    // it can save some memory.
    private final Nutrient[] localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView nutrientName;
        private final TextView nutrientAmount;
        private final ProgressBar nutrientDailyNeedsBar;
        private final TextView nutrientDailyNeedsPercentage;

        public MyViewHolder(View view) {
            super(view);

            nutrientName = view.findViewById(R.id.nutrient_name);
            nutrientAmount = view.findViewById(R.id.nutrient_amount);
            nutrientDailyNeedsBar = view.findViewById(R.id.nutrient_daily_needs_percentage_bar);
            nutrientDailyNeedsPercentage = view.findViewById(R.id.nutrient_daily_need_percentage);

            // Define click listener for the ViewHolder's View
        }

        public TextView getNutrientName() {
            return nutrientName;
        }

        public TextView getNutrientAmount() {
            return nutrientAmount;
        }

        public ProgressBar getNutrientDailyNeedsBar() {
            return nutrientDailyNeedsBar;
        }

        public TextView getNutrientDailyNeedsPercentage() {
            return nutrientDailyNeedsPercentage;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public MealDetailsNutrientsAdapter(Nutrient[] dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_nutrient_row, viewGroup, false);

        return new MyViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        String nutrientAmount = localDataSet[position].getAmount() + " " +
                localDataSet[position].getUnit();
        int progress = Math.round(localDataSet[position].getPercentOfDailyNeeds());
        String percentage = progress + "%";

        viewHolder.getNutrientName().setText(localDataSet[position].getName());
        viewHolder.getNutrientAmount().setText(nutrientAmount);
        viewHolder.getNutrientDailyNeedsBar().setProgress(progress);
        viewHolder.getNutrientDailyNeedsPercentage().setText(percentage);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }

}
