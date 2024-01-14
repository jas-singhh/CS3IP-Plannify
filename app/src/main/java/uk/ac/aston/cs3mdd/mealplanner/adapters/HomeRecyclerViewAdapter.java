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

import java.util.List;

import uk.ac.aston.cs3mdd.mealplanner.MainActivity;
import uk.ac.aston.cs3mdd.mealplanner.R;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Hit;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.Recipe;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.RecipeResponse;
import uk.ac.aston.cs3mdd.mealplanner.utils.Utilities;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.MyViewHolder> {

//    Reference: https://developer.android.com/develop/ui/views/layout/recyclerview

    private List<Hit> localDataSet;
    private HomeRecyclerViewInterface mInterface;

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

        private final HomeRecyclerViewInterface homeInterface;

        public MyViewHolder(@NonNull View itemView, HomeRecyclerViewInterface homeInterface) {
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
                    int currentPos = getAdapterPosition();
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
    public HomeRecyclerViewAdapter(HomeRecyclerViewInterface homeInterface, List<Hit> dataSet) {
        mInterface = homeInterface;
        localDataSet = dataSet;
    }

    @NonNull
    @Override
    public HomeRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal_row,
                parent, false);

        return new MyViewHolder(view, mInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRecyclerViewAdapter.MyViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        Hit currentItem = localDataSet.get(position);
        Picasso.get().load(currentItem.getRecipe().getImage()).into(holder.getMealImage());
        holder.getMealName().setText(currentItem.getRecipe().getLabel());
        holder.getMealHealthRating().setText(Utilities.getMealHealthRating(currentItem.getRecipe()));

        String calories = Math.round(currentItem.getRecipe().getCalories()) + " Calories";
        String time = Utilities.getHoursFromMinutes(Math.round(currentItem.getRecipe().getTotalTime()));
//        String time = Math.round(currentItem.getRecipe().getTotalTime()) + "m";
        holder.getMealCalories().setText(calories);
        holder.getMealTime().setText(time);

        // set on click listener for the meal
        holder.setOnClickListener(localDataSet.get(position).getRecipe());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void updateData(RecipeResponse recipeResponse) {
        if (recipeResponse != null && !recipeResponse.getHits().isEmpty()) {
            localDataSet = recipeResponse.getHits();
            notifyDataSetChanged();
        }
    }

}
