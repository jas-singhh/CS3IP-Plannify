package uk.ac.aston.cs3ip.plannify.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import uk.ac.aston.cs3ip.plannify.R;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.Recipe;

public class HomeMealsAdapter extends RecyclerView.Adapter<HomeMealsAdapter.MyViewHolder> {

//    reference: https://developer.android.com/develop/ui/views/layout/recyclerview

    private ArrayList<? extends uk.ac.aston.cs3ip.plannify.models.api_recipe.Recipe> localDataSet;
    private final HomeMealsOnClickInterface mInterface;


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mealImage;
        private final TextView mealName;
        private final LinearLayout mealHealthRatingParent;
        private final TextView mealCalories;
        private final TextView mealTime;

        private final HomeMealsOnClickInterface homeInterface;

        public MyViewHolder(@NonNull View itemView, HomeMealsOnClickInterface homeInterface) {
            super(itemView);
            this.homeInterface = homeInterface;
            mealImage = itemView.findViewById(R.id.meal_image);
            mealName = itemView.findViewById(R.id.meal_name);
            mealCalories = itemView.findViewById(R.id.meal_calories);
            mealTime = itemView.findViewById(R.id.meal_time);

            mealHealthRatingParent = itemView.findViewById(R.id.meal_health_rating_parent);
        }

        public ImageView getMealImage() {
            return mealImage;
        }

        public TextView getMealName() {
            return mealName;
        }

        public TextView getMealCalories() {
            return mealCalories;
        }

        public TextView getMealTime() {
            return mealTime;
        }

        public LinearLayout getMealHealthRatingParent() {
            return mealHealthRatingParent;
        }

        public void setOnClickListener(uk.ac.aston.cs3ip.plannify.models.api_recipe.Recipe recipe) {
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
    public HomeMealsAdapter(HomeMealsOnClickInterface homeInterface, ArrayList<? extends uk.ac.aston.cs3ip.plannify.models.api_recipe.Recipe> dataSet) {
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

        uk.ac.aston.cs3ip.plannify.models.api_recipe.Recipe currentItem = localDataSet.get(position);

        Picasso.get().load(currentItem.getImage())
                .error(R.drawable.img_image_not_available)
                .placeholder(R.drawable.img_image_not_available)
                .into(holder.getMealImage());
        holder.getMealName().setText(currentItem.getTitle());
        String calories = "N/A";
        if (currentItem.getNutrition() != null && currentItem.getNutrition().getNutrients() != null)
            calories = Math.round(currentItem.getNutrition().getNutrients().get(0).getAmount()) + " Calories";
        String time = currentItem.getReadyInMinutes() + "m";
        holder.getMealCalories().setText(calories);
        holder.getMealTime().setText(time);

        // health rating
        holder.getMealHealthRatingParent().removeAllViews();//clear all views to avoid duplicates
        int healthRating = currentItem.getHealthScore();
        int numStars = getStarRatingFromHealthScore(healthRating);
        ImageView[] stars = new ImageView[numStars];

            for (int i = 0; i < stars.length; i++) {
                ImageView imageView = new ImageView(holder.itemView.getContext());
                imageView.setImageResource(R.drawable.ic_star_rating);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
                holder.getMealHealthRatingParent().addView(imageView);
            }

        // set on click listener for the meal
        holder.setOnClickListener(currentItem);
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    /**
     * Updates the current data with the given one.
     *
     * @param dataSet list containing the data to update the current one with.
     */
    public void updateData(ArrayList<? extends Recipe> dataSet) {
        if (dataSet != null) {
            // creating a new arraylist instead of using the parameter
            // avoids making any unexpected changes in the original data
            // passed in the parameter - e.g. prevents deleting data in the view model.
            localDataSet = new ArrayList<>(dataSet);

            notifyDataSetChanged();
        }
    }

    /**
     * Clears the contents of the local data set.
     */
    public void clearData() {
        if (localDataSet != null)
            localDataSet.clear();
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

    /**
     * Calculates the rating in stars depending on the specified health score.
     *
     * @param healthScore health score based on which the rating is calculated.
     * @return rating for the specified health score ranging from 0 to 5.
     */
    private int getStarRatingFromHealthScore(int healthScore) {
        int result = 0;
        if (healthScore >= 0 && healthScore <= 20) result = 1;
        else if (healthScore > 20 && healthScore <= 40) result = 2;
        else if (healthScore > 40 && healthScore <= 60) result = 3;
        else if (healthScore > 60 && healthScore <= 80) result = 4;
        else if (healthScore > 80 && healthScore <= 100) result = 5;
        return result;
    }

}
