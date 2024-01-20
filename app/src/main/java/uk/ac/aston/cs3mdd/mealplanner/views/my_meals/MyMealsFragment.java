package uk.ac.aston.cs3mdd.mealplanner.views.my_meals;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayoutMediator;

import java.time.LocalDate;
import java.util.ArrayList;

import uk.ac.aston.cs3mdd.mealplanner.MainActivity;
import uk.ac.aston.cs3mdd.mealplanner.adapters.MyMealsCalendarAdapter;
import uk.ac.aston.cs3mdd.mealplanner.adapters.MyMealsCalendarOnClickInterface;
import uk.ac.aston.cs3mdd.mealplanner.adapters.MyMealsViewPagerAdapter;
import uk.ac.aston.cs3mdd.mealplanner.databinding.FragmentMyMealsBinding;
import uk.ac.aston.cs3mdd.mealplanner.utils.CalendarUtils;

public class MyMealsFragment extends Fragment implements MyMealsCalendarOnClickInterface {

    private FragmentMyMealsBinding binding;
    private MyMealsCalendarAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialisation
        mAdapter = new MyMealsCalendarAdapter(CalendarUtils.
                getDatesInWeekBasedOnDate(CalendarUtils.getCurrentDate()),
                this);//pass the current week days
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyMealsBinding.inflate(inflater, container, false);

        setMonthText();
        setupViewPager();
        setupRecyclerView();
        setupOnClickNextWeek();
        setupOnClickPreviousWeek();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvWeekDays.setLayoutManager(layoutManager);
        if (mAdapter != null) binding.rvWeekDays.setAdapter(mAdapter);
    }

    /**
     * Sets up the current month name.
     */
    private void setMonthText() {
        binding.tvMonth.setText(CalendarUtils.getFormattedMonthYearFromDate(CalendarUtils.getCurrentDate()));
    }

    /**
     * Sets up the view pager to allow tab switching.
     */
    private void setupViewPager() {
        binding.viewPager.setAdapter(new MyMealsViewPagerAdapter(this));

        // reference: https://developer.android.com/guide/navigation/navigation-swipe-view-2
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                ((tab, position) -> {
                    if (position == 0) tab.setText("Saved Meals");
                    if (position == 1) tab.setText("Grocery List");
                })).attach();
    }

    /**
     * Sets up the on click listener for the previous week button
     * by fetching the previous week from the Calendar Utils.
     */
    private void setupOnClickPreviousWeek() {
        binding.btnPreviousWeek.setOnClickListener(v -> {
            CalendarUtils.setCurrentDate(CalendarUtils.getCurrentDate()
                    .minusWeeks(1));
            ArrayList<LocalDate> previousWeek = CalendarUtils.getDatesInWeekBasedOnDate(CalendarUtils.getCurrentDate());

            if (mAdapter != null) {
                mAdapter.updateData(previousWeek);
            }

            // update the month text after changing the week
            setMonthText();
        });
    }

    /**
     * Sets up the on click listener for the next week button
     * by fetching the next week from the Calendar Utils.
     */
    private void setupOnClickNextWeek() {
        binding.btnNextWeek.setOnClickListener(v -> {
            CalendarUtils.setCurrentDate(CalendarUtils.getCurrentDate()
                    .plusWeeks(1));
            ArrayList<LocalDate> previousWeek = CalendarUtils.getDatesInWeekBasedOnDate(CalendarUtils.getCurrentDate());

            if (mAdapter != null) {
                mAdapter.updateData(previousWeek);
            }

            // update the month text after changing the week
            setMonthText();
        });
    }

    @Override
    public void onClick(LocalDate selectedDate) {
        CalendarUtils.setCurrentDate(selectedDate);
        Log.d(MainActivity.TAG, "selected date: " + selectedDate);
    }
}