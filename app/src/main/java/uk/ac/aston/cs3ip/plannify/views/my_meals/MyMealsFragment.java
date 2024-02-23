package uk.ac.aston.cs3ip.plannify.views.my_meals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayoutMediator;

import java.time.LocalDate;
import java.util.ArrayList;

import uk.ac.aston.cs3ip.plannify.adapters.MyMealsCalendarAdapter;
import uk.ac.aston.cs3ip.plannify.adapters.MyMealsCalendarOnClickInterface;
import uk.ac.aston.cs3ip.plannify.adapters.MyMealsViewPagerAdapter;
import uk.ac.aston.cs3ip.plannify.databinding.FragmentMyMealsBinding;
import uk.ac.aston.cs3ip.plannify.utils.CalendarUtils;
import uk.ac.aston.cs3ip.plannify.viewmodels.CalendarViewModel;

public class MyMealsFragment extends Fragment implements MyMealsCalendarOnClickInterface {

    private FragmentMyMealsBinding binding;
    private MyMealsCalendarAdapter mAdapter;
    private CalendarViewModel calendarViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialisation
        calendarViewModel = new ViewModelProvider(requireActivity()).get(CalendarViewModel.class);
        mAdapter = new MyMealsCalendarAdapter(CalendarUtils.
                getDatesInWeekBasedOnDate(calendarViewModel.getSelectedDate().getValue()),
                this);//pass the current week days
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyMealsBinding.inflate(inflater, container, false);

        initViewPager();
        initRecyclerView();
        initOnClickNextWeek();
        initOnClickPreviousWeek();

        subscribeToViewingDateChanges();

        return binding.getRoot();
    }

    /**
     * Subscribes to the changes in the viewing date, which is updated when the user
     * visits the previous or next week within the horizontal calendar view,
     * and updates the month text accordingly.
     */
    private void subscribeToViewingDateChanges() {
        calendarViewModel.getViewingDate().observe(getViewLifecycleOwner(), viewingDate -> binding.tvMonth.setText(CalendarUtils.getFormattedMonthYearFromDate(viewingDate)));
    }

    /**
     * Sets up the recycler view and its adapter.
     */
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvWeekDays.setLayoutManager(layoutManager);
        if (mAdapter != null) binding.rvWeekDays.setAdapter(mAdapter);
    }

    /**
     * Sets up the view pager to allow tab switching.
     */
    private void initViewPager() {
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
    private void initOnClickPreviousWeek() {
        binding.btnPreviousWeek.setOnClickListener(v -> {
            if (calendarViewModel.getViewingDate().getValue() != null)
                calendarViewModel.setViewingDate(calendarViewModel.getViewingDate().getValue().minusWeeks(1));
            ArrayList<LocalDate> previousWeek = CalendarUtils.getDatesInWeekBasedOnDate(calendarViewModel.getViewingDate().getValue());

            if (mAdapter != null) {
                mAdapter.updateData(previousWeek);
            }
        });
    }

    /**
     * Sets up the on click listener for the next week button
     * by fetching the next week from the Calendar Utils.
     */
    private void initOnClickNextWeek() {
        binding.btnNextWeek.setOnClickListener(v -> {
            if (calendarViewModel.getViewingDate().getValue() != null)
                calendarViewModel.setViewingDate(calendarViewModel.getViewingDate().getValue().plusWeeks(1));
            ArrayList<LocalDate> nextWeek = CalendarUtils.getDatesInWeekBasedOnDate(calendarViewModel.getViewingDate().getValue());

            if (mAdapter != null) {
                mAdapter.updateData(nextWeek);
            }
        });
    }

    @Override
    public void onClick(LocalDate selectedDate) {
        calendarViewModel.setSelectedDate(selectedDate);
    }
}