package uk.ac.aston.cs3ip.plannify.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import uk.ac.aston.cs3ip.plannify.R;
import uk.ac.aston.cs3ip.plannify.models.network_recipe.NetworkRecipe;
import uk.ac.aston.cs3ip.plannify.utils.Utilities;

public class HomeMealsAdapter extends RecyclerView.Adapter<HomeMealsAdapter.MyViewHolder> {

//    reference: https://developer.android.com/develop/ui/views/layout/recyclerview

    private ArrayList<? extends NetworkRecipe> localDataSet;
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
        private final MaterialCardView customAttributeParent;

        private final HomeMealsOnClickInterface homeInterface;

        public MyViewHolder(@NonNull View itemView, HomeMealsOnClickInterface homeInterface) {
            super(itemView);
            this.homeInterface = homeInterface;
            mealImage = itemView.findViewById(R.id.meal_image);
            mealName = itemView.findViewById(R.id.meal_name);
            mealCalories = itemView.findViewById(R.id.meal_calories);
            mealTime = itemView.findViewById(R.id.meal_time);
            customAttributeParent = itemView.findViewById(R.id.attribute_custom);

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

        public MaterialCardView getCustomAttributeParent() {
            return customAttributeParent;
        }

        public void setOnClickListener(NetworkRecipe networkRecipe) {
            if (homeInterface != null) {
                itemView.setOnClickListener(v -> {
                    int currentPos = getAbsoluteAdapterPosition();
                    if (currentPos != RecyclerView.NO_POSITION) {
                        homeInterface.onClickMeal(networkRecipe);
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
    public HomeMealsAdapter(HomeMealsOnClickInterface homeInterface, ArrayList<? extends NetworkRecipe> dataSet) {
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

        NetworkRecipe currentItem = localDataSet.get(position);

        ImageView placeholderImage = new ImageView(holder.itemView.getContext());
        placeholderImage.setImageResource(R.drawable.img_image_not_available);
        Drawable placeholderDrawable = placeholderImage.getDrawable();
        if (placeholderDrawable != null) placeholderDrawable.setAlpha(160);

        Picasso.get().load(currentItem.getImage())
                .placeholder(placeholderImage.getDrawable())
                .into(holder.getMealImage());

        holder.getMealName().setText(currentItem.getTitle());
        String calories = "N/A";
        if (currentItem.getNutrition() != null && currentItem.getNutrition().getNutrients() != null)
            calories = Math.round(currentItem.getNutrition().getNutrients().get(0).getAmount()) + " kcal";
        String time = currentItem.getReadyInMinutes() + "m";
        holder.getMealCalories().setText(calories);
        holder.getMealTime().setText(time);

        // health rating
        holder.getMealHealthRatingParent().removeAllViews();//clear all views to avoid duplicates
        // do not display health rating if it is a custom recipe
        if (localDataSet.get(position).getId() == -1) {
            // it is a custom recipe
            TextView tv = new TextView(holder.itemView.getContext());
            tv.setText(R.string.health_rating_not_available);
            holder.getMealHealthRatingParent().addView(tv);
        } else {
            // it is not a custom recipe
            int healthRating = currentItem.getHealthScore();
            int numStars = Utilities.getStarRatingFromHealthScore(healthRating);
            ImageView[] stars = new ImageView[numStars];

            for (int i = 0; i < stars.length; i++) {
                ImageView imageView = new ImageView(holder.itemView.getContext());
                imageView.setImageResource(R.drawable.ic_star_rating);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.semi_transparent_white));
                holder.getMealHealthRatingParent().addView(imageView);
            }
        }

        // if it is a custom recipe - display the custom attribute to indicate it is a custom recipe
        if (localDataSet.get(position).getId() == -1) {
            holder.getCustomAttributeParent().setVisibility(View.VISIBLE);
        } else {
            holder.getCustomAttributeParent().setVisibility(View.GONE);
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
    public void updateData(ArrayList<? extends NetworkRecipe> dataSet) {
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
        if (localDataSet != null) {
            localDataSet.clear();
            notifyDataSetChanged();
        }
    }


    /**
     * Returns the recipe at the given position (if present).
     *
     * @param pos index where to find the recipe.
     * @return the recipe at the given index.
     */
    public NetworkRecipe getRecipeAt(int pos) {
        if (pos == RecyclerView.NO_POSITION || localDataSet.size() == 0) return null;

        return localDataSet.get(pos);
    }

}
