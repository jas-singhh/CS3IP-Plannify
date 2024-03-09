package uk.ac.aston.cs3ip.plannify.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.time.LocalDate;
import java.util.ArrayList;

import uk.ac.aston.cs3ip.plannify.R;
import uk.ac.aston.cs3ip.plannify.utils.CalendarUtils;

public class MyMealsCalendarAdapter extends RecyclerView.Adapter<MyMealsCalendarAdapter.MyViewHolder> {
    // reference: https://developer.android.com/develop/ui/views/layout/recyclerview

    private ArrayList<LocalDate> localDataSet;

    private final MyMealsCalendarOnClickInterface myMealsCalendarOnClickInterface;
    private LocalDate selectedDate;
    private int lastSelectedDateIndex;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView dayParent;
        private final TextView dayName;
        private final TextView day;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dayParent = itemView.findViewById(R.id.day_parent);
            dayName = itemView.findViewById(R.id.tv_day_name);
            day = itemView.findViewById(R.id.tv_day);
        }

        public MaterialCardView getDayParent() {
            return dayParent;
        }

        public TextView getDayName() {
            return dayName;
        }

        public TextView getDay() {
            return day;
        }
    }

    public MyMealsCalendarAdapter(ArrayList<LocalDate> dataset, MyMealsCalendarOnClickInterface myMealsCalendarOnClickInterface) {
        localDataSet = dataset;
        this.myMealsCalendarOnClickInterface = myMealsCalendarOnClickInterface;
        selectedDate = LocalDate.now();//default selected date
        lastSelectedDateIndex = -1;//default
    }

    @NonNull
    @Override
    public MyMealsCalendarAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day_column, parent, false);

        // evenly distribute the horizontal space in the recycler view for the 7 days of the week
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = parent.getMeasuredWidth() / 7;

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyMealsCalendarAdapter.MyViewHolder holder, int position) {
        // dataset format is "EEE dd" - e.g. "Mon 15"
        String formattedDate = CalendarUtils.getFormattedDayNameAndDayFromDate(localDataSet.get(position));
        String[] datasetValues = formattedDate.split(" ");//space delimited
        if (datasetValues.length < 2) return;

        String dayName = datasetValues[0];//day name is the first value in the array
        String day = datasetValues[1];//day is the second value in the array

        holder.getDayName().setText(dayName);
        holder.getDay().setText(day);

        // update the selected date when user clicks on a different date
        holder.itemView.setOnClickListener(v -> {
            selectedDate = localDataSet.get(position);

            // update the data at recorded positions rather than updating entire data set
            // this approach improves efficiency
            if (lastSelectedDateIndex != -1) {
                notifyItemChanged(position);
                notifyItemChanged(lastSelectedDateIndex);
            }

            // perform the callback to notify about the clicked item
            if (myMealsCalendarOnClickInterface != null && position != RecyclerView.NO_POSITION) {
                myMealsCalendarOnClickInterface.onClick(selectedDate);
            }
        });

        // update the background and foreground colour on the selected date
        if (localDataSet.get(position).equals(selectedDate)) {
            lastSelectedDateIndex = holder.getAbsoluteAdapterPosition();//update last selected date for efficiency

            holder.getDayParent().setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_white));
            holder.getDay().setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_black));
            holder.getDayName().setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_black));
        } else {
            holder.getDayParent().setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.transparent));
            holder.getDay().setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_white));
            holder.getDayName().setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_white));
        }
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
    public void updateData(ArrayList<LocalDate> dataSet) {
        if (dataSet != null) {
            localDataSet = dataSet;
            notifyDataSetChanged();
        }
    }

}
