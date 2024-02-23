package uk.ac.aston.cs3ip.plannify.views.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.ac.aston.cs3ip.plannify.MainActivity;
import uk.ac.aston.cs3ip.plannify.R;
import uk.ac.aston.cs3ip.plannify.enums.EnumCuisine;
import uk.ac.aston.cs3ip.plannify.enums.EnumDiet;
import uk.ac.aston.cs3ip.plannify.enums.EnumFilterId;
import uk.ac.aston.cs3ip.plannify.enums.EnumMaxReadyTime;
import uk.ac.aston.cs3ip.plannify.enums.EnumMealType;
import uk.ac.aston.cs3ip.plannify.viewmodels.FindMealsViewModel;

public class DialogFindMealsFilters extends Dialog {


    private final Context context;
    private final FindMealsViewModel findMealsViewModel;
    private HashMap<EnumFilterId, List<String>> filtersMap;
    private List<String> mealTypesOptionsList;
    private List<String> cuisinesOptionsList;
    private List<String> dietsOptionsList;
    private List<String> maxReadyTimeOptionsList;
    private Dialog dialog;

    public DialogFindMealsFilters(@NonNull Context context, MainActivity activity) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        View view = getLayoutInflater().inflate(R.layout.dialog_find_meals_filters, null);
        this.context = context;
        findMealsViewModel = new ViewModelProvider(activity,
                ViewModelProvider.Factory.from(FindMealsViewModel.initializer)).get(FindMealsViewModel.class);

        initFiltersMap();
        initAvailableOptionsLists();

        setupListViews(view);
        initFilterOptions(view);

        // on click listeners
        onClickReset(view);
        onClickCancel(view);
        onClickSave(view);

        createAlertDialog(context, view);
    }

    /**
     * Initialises the filters map containing the user's selections for various
     * filters and syncs it with the view model for consistency.
     */
    private void initFiltersMap() {
        if (findMealsViewModel.getFiltersMap().getValue() != null)
            // map has already been initialised - retrieve it so we can make changes to it in this class
            // and they will be reflected in the view model
            filtersMap = findMealsViewModel.getFiltersMap().getValue();
        else {
            // map not already initialised so set its values here - and set it in the
            // view model so we can make changes to it and they will be reflected in the view model
            filtersMap = new HashMap<>();// map has not yet been initialised
            findMealsViewModel.setFiltersMap(filtersMap);

            filtersMap.put(EnumFilterId.MEAL_TYPE, new ArrayList<>());
            filtersMap.put(EnumFilterId.CUISINES, new ArrayList<>());
            filtersMap.put(EnumFilterId.DIETS, new ArrayList<>());
            filtersMap.put(EnumFilterId.MAX_READY_TIME, new ArrayList<>());
        }
    }

    /**
     * Initialises the lists of available options for different filters.
     */
    private void initAvailableOptionsLists() {
        // list of options to display
        mealTypesOptionsList = getStringArrayFromEnum(EnumMealType.class);
        cuisinesOptionsList = getStringArrayFromEnum(EnumCuisine.class);
        dietsOptionsList = getStringArrayFromEnum(EnumDiet.class);
        maxReadyTimeOptionsList = getStringArrayFromEnum(EnumMaxReadyTime.class);
    }


    /**
     * Initialises the array adapters with the appropriate list of options
     * for each filtering option.
     *
     * @param view view required to a reference of the layout elements.
     */
    private void setupListViews(View view) {
        if (view == null) return;

        // meal types
        ArrayAdapter<String> mealTypesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_multiple_choice,
                mealTypesOptionsList);
        ListView mealsListView = view.findViewById(R.id.dialog_filter_meal_types_list);
        mealsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mealsListView.setAdapter(mealTypesAdapter);

        // cuisines
        ArrayAdapter<String> cuisinesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_multiple_choice,
                cuisinesOptionsList);
        ListView cuisinesListView = view.findViewById(R.id.dialog_filter_cuisines_list);
        cuisinesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        cuisinesListView.setAdapter(cuisinesAdapter);

        // diets
        ArrayAdapter<String> dietsAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_multiple_choice, dietsOptionsList);
        ListView dietsListView = view.findViewById(R.id.dialog_filter_diets_list);
        dietsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        dietsListView.setAdapter(dietsAdapter);

        // max ready time
        ArrayAdapter<String> maxReadyTimeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_single_choice, maxReadyTimeOptionsList);
        // single selection for max ready time
        ListView maxReadyTimeListView = view.findViewById(R.id.dialog_filter_max_ready_time_list);
        maxReadyTimeListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        maxReadyTimeListView.setAdapter(maxReadyTimeAdapter);
        maxReadyTimeListView.setItemChecked(4, true);//default value is more than 2h

        // load the pre-selected options
        setListOptionsAccordingToViewModelData(EnumFilterId.MEAL_TYPE, mealsListView, mealTypesAdapter);
        setListOptionsAccordingToViewModelData(EnumFilterId.CUISINES, cuisinesListView, cuisinesAdapter);
        setListOptionsAccordingToViewModelData(EnumFilterId.DIETS, dietsListView, dietsAdapter);
        setListOptionsAccordingToViewModelData(EnumFilterId.MAX_READY_TIME, maxReadyTimeListView, maxReadyTimeAdapter);
    }

    private void setListOptionsAccordingToViewModelData(EnumFilterId filterId, ListView listView, ArrayAdapter<String> adapter) {
        if (filterId == null || listView == null || adapter == null) return;

        // check if the filters map in the view model already has some values stored
        // and update the list view accordingly

        // use a hashset to check if the options are already selected for efficiency purposes
        // as hashsets are quicker to find elements
        List<String> _preSelectedOptions = filtersMap.get(filterId);
        HashSet<String> preSelectedOptions = new HashSet<>();
        if (_preSelectedOptions != null) {
            preSelectedOptions.addAll(_preSelectedOptions);
        }

        for (int i = 0; i < adapter.getCount(); i++) {
            String item = (String) listView.getItemAtPosition(i);
            listView.setItemChecked(i, preSelectedOptions.contains(item));
        }
    }

    /**
     * Initialises the different filtering options by setting up their on click
     * listeners to show/hide the corresponding list of options.
     *
     * @param view view required to get a reference of the layout elements.
     */
    private void initFilterOptions(View view) {
        TextView mealTypes = view.findViewById(R.id.dialog_filter_meal_types);
        ListView mealTypesList = view.findViewById(R.id.dialog_filter_meal_types_list);
        TextView cuisines = view.findViewById(R.id.dialog_filter_cuisines);
        ListView cuisinesList = view.findViewById(R.id.dialog_filter_cuisines_list);
        TextView diets = view.findViewById(R.id.dialog_filter_diets);
        ListView dietsList = view.findViewById(R.id.dialog_filter_diets_list);
        TextView maxReadyTime = view.findViewById(R.id.dialog_filter_max_ready_time);
        ListView maxReadyTimeList = view.findViewById(R.id.dialog_filter_max_ready_time_list);

        // storing variables in a map and initialising their on click listeners
        // for efficiency purposes
        HashMap<TextView, ListView> viewsMap = new HashMap<>(4);
        viewsMap.put(mealTypes, mealTypesList);
        viewsMap.put(cuisines, cuisinesList);
        viewsMap.put(diets, dietsList);
        viewsMap.put(maxReadyTime, maxReadyTimeList);

        // set up text view on click listeners
        for (TextView v : viewsMap.keySet()) {
            toggleListVisibilityOnClick(v, viewsMap.get(v));
        }

        // set up list view on item selected listeners
        setupFiltersSelections(EnumFilterId.MEAL_TYPE, mealTypesList);
        setupFiltersSelections(EnumFilterId.CUISINES, cuisinesList);
        setupFiltersSelections(EnumFilterId.DIETS, dietsList);
        setupFiltersSelections(EnumFilterId.MAX_READY_TIME, maxReadyTimeList);
    }

    /**
     * Sets up the on click listener for the reset button to clear all selections.
     *
     * @param view view required to get a reference of the reset button.
     */
    private void onClickReset(View view) {
        if (view == null) return;

        view.findViewById(R.id.dialog_filter_reset_btn).setOnClickListener(v -> {
            // clear all selections in the map
            for (List<String> list : filtersMap.values()) list.clear();

            // clear all the list views
            ListView mealTypesList = view.findViewById(R.id.dialog_filter_meal_types_list);
            ListView cuisinesList = view.findViewById(R.id.dialog_filter_cuisines_list);
            ListView dietsList = view.findViewById(R.id.dialog_filter_diets_list);
            ListView maxReadyTimeList = view.findViewById(R.id.dialog_filter_max_ready_time_list);
            ListView[] listViews = new ListView[]{mealTypesList, cuisinesList, dietsList, maxReadyTimeList};

            for (ListView listView : listViews) {
                if (listView.getCheckedItemCount() > 0) {
                    listView.clearChoices();
                    listView.requestLayout();//updates layout
                }
            }

            // display system status - Nielsen's principle
            Toast.makeText(view.getContext(), "Selections Cleared", Toast.LENGTH_SHORT).show();
        });

    }

    /**
     * Sets up the on click listener for the cancel button to dismiss the dialog.
     *
     * @param view view required to get a reference of the cancel button.
     */
    private void onClickCancel(View view) {
        if (view == null) return;

        view.findViewById(R.id.dialog_filter_cancel_btn).setOnClickListener(v -> {
            if (dialog != null) dialog.dismiss();
        });
    }

    /**
     * Sets up the on click listener for the save button to save the filtering options
     * and dismiss the dialog.
     *
     * @param view view required to get a reference of the save button.
     */
    private void onClickSave(View view) {
        if (view == null) return;

        view.findViewById(R.id.dialog_filter_save_btn).setOnClickListener(v -> {
            // save the filters for searching
            findMealsViewModel.setFiltersMap(filtersMap);

            if (dialog != null) dialog.dismiss();
        });
    }


    /**
     * Sets up the filter selection logic by adding the selected option to the list.
     *
     * @param filterId filter id to identify which type of filter it is.
     * @param listView list of options that the user can select from.
     */
    private void setupFiltersSelections(EnumFilterId filterId, ListView listView) {
        if (filterId == null || listView == null) return;

        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            String selectedValue = (String) parent.getItemAtPosition(position);

            List<String> preSelectedOptions = filtersMap.get(filterId);

            if (listView.isItemChecked(position)) {
                // checked - add the checked item in the list of selected items for the specific filter
                if (preSelectedOptions == null) preSelectedOptions = new ArrayList<>();

                preSelectedOptions.add(selectedValue);
            } else {
                // unchecked - check if the item is in map, and delete it as it has been unchecked
                if (preSelectedOptions == null) return;// nothing selected

                preSelectedOptions.remove(selectedValue);
            }

            listView.requestLayout();
        });
    }

    /**
     * Hides or shows the given list based on whether it was already visible or not.
     *
     * @param textView view to which the given list is linked to.
     * @param listView list to show or hide depending on whether it is already visible or not.,
     */
    private void toggleListVisibilityOnClick(TextView textView, ListView listView) {
        if (textView == null || listView == null) return;

        textView.setOnClickListener(v -> {
            // change the dropdown symbol based on whether the list is visible or not
            if (listView.getVisibility() == View.VISIBLE) {
                // arrow down and hide list
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                listView.setVisibility(View.GONE);
            } else {
                // arrow up and show list
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                listView.setVisibility(View.VISIBLE);
            }
        });
    }


    /**
     * Creates an alert dialog for allowing the user to select filters for a query.
     *
     * @param context context required to create the alert dialog.
     * @param view    custom view contained in the alert dialog.
     */
    private void createAlertDialog(@NonNull Context context, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.create();


        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }

    /**
     * Returns an array of String containing the values for the specified
     * Enum class.
     *
     * @param enumClass enum class to translate into an array of String.
     * @param <T>       wildcard accepting any time of Enum class.
     * @return a list of String if the Enum class is specified, null otherwise.
     */
    private <T extends Enum<T>> List<String> getStringArrayFromEnum(Class<T> enumClass) {
        // reference: https://stackoverflow.com/questions/13783295/getting-all-names-in-an-enum-as-a-string
        if (enumClass != null) {
            return Stream.of(enumClass.getEnumConstants()).map(Enum::toString).collect(Collectors.toList());
        }

        return null;
    }
}