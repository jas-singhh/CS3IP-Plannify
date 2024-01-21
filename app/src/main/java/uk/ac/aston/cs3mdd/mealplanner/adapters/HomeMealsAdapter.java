package uk.ac.aston.cs3mdd.mealplanner.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import uk.ac.aston.cs3mdd.mealplanner.MainActivity;
import uk.ac.aston.cs3mdd.mealplanner.R;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Recipe;
import uk.ac.aston.cs3mdd.mealplanner.utils.Utilities;

public class HomeMealsAdapter extends RecyclerView.Adapter<HomeMealsAdapter.MyViewHolder> {

//    Reference: https://developer.android.com/develop/ui/views/layout/recyclerview

    private ArrayList<Recipe> localDataSet;
    private final HomeMealsOnClickInterface mInterface;


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mealImage;
        private final TextView mealName;
        private final TextView mealHealthRating;
        private final TextView mealCalories;
        private final TextView mealTime;

        private final HomeMealsOnClickInterface homeInterface;

        public MyViewHolder(@NonNull View itemView, HomeMealsOnClickInterface homeInterface) {
            super(itemView);
            this.homeInterface = homeInterface;

            mealImage = itemView.findViewById(R.id.meal_image);
            mealName = itemView.findViewById(R.id.meal_name);
            mealHealthRating = itemView.findViewById(R.id.meal_health_rating);
            mealCalories = itemView.findViewById(R.id.meal_calories);
            mealTime = itemView.findViewById(R.id.meal_time);
        }

        public ImageView getMealImage() {
            return mealImage;
        }

        public TextView getMealName() {
            return mealName;
        }

        public TextView getMealHealthRating() {
            return mealHealthRating;
        }

        public TextView getMealCalories() {
            return mealCalories;
        }

        public TextView getMealTime() {
            return mealTime;
        }

        public void setOnClickListener(Recipe recipe) {
            if (homeInterface != null) {
                itemView.setOnClickListener(v -> {
                    int currentPos = getAbsoluteAdapterPosition();
                    if (currentPos != RecyclerView.NO_POSITION) {
                        homeInterface.onClickMeal(recipe);
                    }
                });
            }
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet List of recipes containing the data to populate views to be used
     *                by RecyclerView
     */
    public HomeMealsAdapter(HomeMealsOnClickInterface homeInterface, ArrayList<Recipe> dataSet) {
        mInterface = homeInterface;
        localDataSet = dataSet;
    }

    @NonNull
    @Override
    public HomeMealsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal_row,
                parent, false);

        return new MyViewHolder(view, mInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeMealsAdapter.MyViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        Recipe currentItem = localDataSet.get(position);
        Log.d(MainActivity.TAG, currentItem.getImage());
        Picasso.get().load(currentItem.getImage())
                .error(R.drawable.loading_img)
                .placeholder(R.drawable.loading_img)
                .into(holder.getMealImage());
        holder.getMealName().setText(currentItem.getLabel());
        holder.getMealHealthRating().setText(Utilities.getMealHealthRating(currentItem));

        String calories = Math.round(currentItem.getCalories()) + " Calories";
        String time = Utilities.getHoursFromMinutes(Math.round(currentItem.getTotalTime()));
        holder.getMealCalories().setText(calories);
        holder.getMealTime().setText(time);

        // set on click listener for the meal
        holder.setOnClickListener(currentItem);
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void updateData(ArrayList<Recipe> recipes) {
        if (recipes != null) {
            localDataSet = recipes;
            notifyDataSetChanged();
        }
    }

    /**
     * Returns the recipe at the given position (if present).
     *
     * @param pos index where to find the recipe.
     * @return the recipe at the given index.
     */
    public Recipe getRecipeAt(int pos) {
        if (pos == RecyclerView.NO_POSITION || localDataSet.size() == 0) return null;

        return localDataSet.get(pos);
    }


}
