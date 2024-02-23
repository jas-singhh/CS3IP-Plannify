package uk.ac.aston.cs3ip.plannify.views.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

import uk.ac.aston.cs3ip.plannify.R;
import uk.ac.aston.cs3ip.plannify.adapters.CustomMealIngredientsAdapter;
import uk.ac.aston.cs3ip.plannify.adapters.DialogSaveCustomRecipeOnClickInterface;
import uk.ac.aston.cs3ip.plannify.enums.EnumMealType;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.AnalyzedInstruction;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.ExtendedIngredient;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.Measures;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.Metric;
import uk.ac.aston.cs3ip.plannify.models.local_recipe.LocalRecipe;
import uk.ac.aston.cs3ip.plannify.utils.CalendarUtils;

public class DialogCustomMeal extends Dialog {

//    reference: https://stackoverflow.com/questions/8232012/in-android-how-do-i-create-a-popup-and-submit-data-to-the-main-view

    private Dialog dialog;
    private EditText mealName;
    private MaterialAutoCompleteTextView mealTypeDropdown;
    private TextView mealDate;
    private MaterialAutoCompleteTextView timeDropdown;
    private MaterialAutoCompleteTextView servingsDropdown;
    private EditText instructions;

    private EditText ingredientName;
    private EditText ingredientQuantity;
    private EditText ingredientUnit;
    private CardView addIngredientButton;
    private RecyclerView ingredientsList;
    private MaterialButton saveButton;
    private final CustomMealIngredientsAdapter mAdapter;
    private DatePickerDialog datePickerDialog;
    private LocalDate selectedDate;
    private final DialogSaveCustomRecipeOnClickInterface mListener;
    private final HashMap<Integer, String> timeMap;

    public DialogCustomMeal(@NonNull Context context, DialogSaveCustomRecipeOnClickInterface listener) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_custom_recipe, null);

        mAdapter = new CustomMealIngredientsAdapter(new ArrayList<>(0));
        selectedDate = LocalDate.now();
        mListener = listener;
        // initialise map with 5h max allowed preparation time
        timeMap = new HashMap<>(300);//5h max
        for (int i = 0; i < 300; i++) {
            timeMap.put(i, getTimeArrayInRange(0, 300)[i]);
        }

        initRecyclerView(view);
        initFields(view);
        initDatePicker(context);
        initDropdowns(context);

        onClickDate();
        initAddIngredientsButton(view);
        onClickSave();

        createAlertDialog(context, view);
    }


    /**
     * Initialises the onclick listener for the add ingredients button which adds
     * the typed ingredient and amount to the list.
     *
     * @param view view required to get the button reference.
     */
    private void initAddIngredientsButton(View view) {
        addIngredientButton.setOnClickListener(v -> {
            String name = ingredientName.getText().toString();
            String quantity = ingredientQuantity.getText().toString();
            String unit = ingredientUnit.getText().toString();

            if (name.isEmpty()) {
                return;
            }

            // make an extended ingredient to add to the list
            ExtendedIngredient ingredient = new ExtendedIngredient();
            ingredient.setName(name);
            ingredient.setAmount(Double.parseDouble(quantity));
            Metric metric = new Metric();
            metric.setUnitShort(unit);
            Measures measures = new Measures();
            measures.setMetric(metric);
            ingredient.setMeasures(measures);

            // update the adapter
            mAdapter.addSingleItem(ingredient);

            // clear the content after adding the ingredient and quantity
            ingredientName.setText(null);
            ingredientQuantity.setText(null);

            // hide the keyboard
            // reference: https://stackoverflow.com/questions/1109022/how-can-i-close-hide-the-android-soft-keyboard-programmatically
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });
    }

    /**
     * Initialises the fields in the layout.
     *
     * @param view view required to get the fields.
     */
    private void initFields(View view) {
        mealName = view.findViewById(R.id.custom_meal_name);
        mealTypeDropdown = view.findViewById(R.id.custom_meal_type_dropdown);
        mealDate = view.findViewById(R.id.custom_meal_date);
        timeDropdown = view.findViewById(R.id.custom_meal_time_dropdown);
        servingsDropdown = view.findViewById(R.id.custom_meal_servings_dropdown);
        instructions = view.findViewById(R.id.custom_meal_instructions);
        ingredientName = view.findViewById(R.id.custom_meal_ingredient);
        ingredientQuantity = view.findViewById(R.id.custom_meal_ingredient_quantity);
        ingredientUnit = view.findViewById(R.id.custom_meal_ingredient_unit);
        addIngredientButton = view.findViewById(R.id.add_ingredient);
        saveButton = view.findViewById(R.id.custom_meal_save_btn);
    }


    /**
     * Populates the dropdowns in the view with the appropriate adapters.
     *
     * @param context context required to initialise the adapters for each dropdown.
     */
    private void initDropdowns(Context context) {
        // reference: https://developer.android.com/reference/android/widget/AutoCompleteTextView

        // meal types dropdown
        ArrayAdapter<EnumMealType> mealTypesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line,
                EnumMealType.values());
        mealTypeDropdown.setAdapter(mealTypesAdapter);

        // time dropdown
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line,
                getTimeArrayInRange(0, 300));
        timeDropdown.setAdapter(timeAdapter);

        // servings dropdown
        Integer[] servingsArray = IntStream.range(1, 20)
                .boxed()//this is done as the array adapter does not support primitive types
                .toArray(Integer[]::new);
        ArrayAdapter<Integer> servingsAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line,
                servingsArray);
        servingsDropdown.setAdapter(servingsAdapter);
    }

    /**
     * Sets up the recycler view and its adapter.
     *
     * @param view view is required to get the recycler view.
     */
    private void initRecyclerView(View view) {
        ingredientsList = view.findViewById(R.id.rv_ingredients);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        ingredientsList.setLayoutManager(layout);
        if (mAdapter != null) ingredientsList.setAdapter(mAdapter);
    }

    /**
     * Initialises the date picker dialog to show once the user wants to
     * edit the date.
     *
     * @param context context required to create the date picker dialog
     */
    private void initDatePicker(Context context) {
        datePickerDialog = new DatePickerDialog(context);
        datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            // default format is yyyy-mm-dd

            // parse to a local date
            month += 1;

            // format date to match with dd/mm/yyyy format
            String date = String.format(Locale.getDefault(), "%d/%02d/%02d", year, month, dayOfMonth);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/dd");
            selectedDate = LocalDate.parse(date, formatter);

            // format to a more readable version - Day Month Year
            mealDate.setText(CalendarUtils.getFormattedDayMonthYearFromDate(selectedDate));
        });
    }


    /**
     * Creates an alert dialog for allowing the user to create a custom meal.
     *
     * @param context context required to create the alert dialog.
     * @param view    custom view contained in the alert dialog.
     */
    private void createAlertDialog(@NonNull Context context, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Create Recipe");
        builder.setView(view);
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        dialog = builder.create();

        if (dialog.getWindow() != null)
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        dialog.show();
    }

    /**
     * Sets up the onclick listener for the date button which shows a date picker.
     */
    private void onClickDate() {
        mealDate.setOnClickListener(v -> datePickerDialog.show());
    }

    /**
     * Set up the onclick listener for the save button which will check if all
     * required fields are provided and save accordingly.
     */
    private void onClickSave() {
        // check if all fields are provided
        saveButton.setOnClickListener(v -> {
            if (areRequiredFieldsProvided()) {

                // all fields provided, so perform a callback to notify listeners
                if (mListener != null)
                    mListener.onClick(getLocalRecipeFromThisCustomRecipe());

                if (dialog != null)
                    dialog.dismiss();
            }
        });
    }

    /**
     * Creates a local recipe from the details provided in order to save it in the
     * local storage.
     *
     * @return local recipe from the current information.
     */
    private LocalRecipe getLocalRecipeFromThisCustomRecipe() {
        LocalRecipe localRecipe = new LocalRecipe();
        localRecipe.setTitle(mealName.getText().toString());
        localRecipe.setMealTypeSavedFor(Enum.valueOf(EnumMealType.class, mealTypeDropdown.getText().toString()));
        localRecipe.setDateSavedFor(selectedDate);

        // the following fields are optional, so check if they are provided before
        // adding them
        // minutes
        int readyInMinutes = getMinutesFromInputtedTime();
        localRecipe.setReadyInMinutes(readyInMinutes);
        // servings
        if (!servingsDropdown.getText().toString().isEmpty())
            localRecipe.setServings(Integer.parseInt(servingsDropdown.getText().toString()));
        // instructions
        if (!instructions.getText().toString().isEmpty()) {
            AnalyzedInstruction analyzedInstruction = new AnalyzedInstruction();
            analyzedInstruction.setName(instructions.getText().toString());
            ArrayList<AnalyzedInstruction> instructions = new ArrayList<>();
            instructions.add(analyzedInstruction);
            localRecipe.setAnalyzedInstructions(instructions);
        }
        // ingredients
        if (!mAdapter.getIngredients().isEmpty())
            localRecipe.setExtendedIngredients(mAdapter.getIngredients());


        return localRecipe;
    }

    /**
     * Returns an array of String containing a list of time ranging from minutes to hours
     * within the specified range.
     *
     * @param from value from which to return the time array.
     * @param to   value to which to return the time array.
     * @return an array of String containing times in format of e.g. 1h 10m.
     */
    private String[] getTimeArrayInRange(int from, int to) {
        // reference: https://stackoverflow.com/questions/50406260/how-to-convert-a-range-to-a-delimited-string-in-java-8
        return IntStream.range(from, to)
                .mapToObj(i -> {
                    if (i < 60) {
                        // minutes
                        return i + "m";
                    } else {
                        if (i % 60 > 0) {
                            // there are minutes over the hour
                            return i / 60 + "h " + i % 60 + "m";
                        }
                        return i / 60 + "h";
                    }
                }).toArray(String[]::new);
    }

    /**
     * Checks if the required fields, i.e. meal name, date, and type are provided.
     *
     * @return true if the required fields are provided, false otherwise.
     */
    private boolean areRequiredFieldsProvided() {
        boolean result = true;
        // required fields are meal name, meal date and meal type
        if (mealDate.getText().toString().isEmpty()) {
            mealDate.setError("Type is required");
            mealDate.requestFocus();
            result = false;
        }
        if (mealTypeDropdown.getText().toString().isEmpty()) {
            mealTypeDropdown.setError("Type is required");
            mealTypeDropdown.requestFocus();
            result = false;
        }
        if (mealName.getText().toString().isEmpty()) {
            mealName.setError("Name is required");
            mealName.requestFocus();
            result = false;
        }

        return result;
    }

    /**
     * Retrieves the minutes from the selected time by the user.
     *
     * @return the preparation time in minutes, translated from the selected value by the user.
     */
    private int getMinutesFromInputtedTime() {
        int result = 0;
        if (!timeDropdown.getText().toString().isEmpty()) {
            String value = timeDropdown.getText().toString();
            if (!timeMap.isEmpty()) {
                for (Map.Entry<Integer, String> entry : timeMap.entrySet()) {
                    if (value.equals(entry.getValue())) {
                        result = (int) entry.getKey();
                        break;
                    }
                }
            }
        }
        return result;
    }

}