package uk.ac.aston.cs3mdd.mealplanner.views.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import uk.ac.aston.cs3mdd.mealplanner.R;
import uk.ac.aston.cs3mdd.mealplanner.adapters.DialogSaveRecipeOnClickInterface;
import uk.ac.aston.cs3mdd.mealplanner.data.recipe.enums.EnumMealType;
import uk.ac.aston.cs3mdd.mealplanner.utils.CalendarUtils;

public class DialogSaveRecipe extends Dialog {

//    reference: https://stackoverflow.com/questions/8232012/in-android-how-do-i-create-a-popup-and-submit-data-to-the-main-view

    private DatePickerDialog datePickerDialog;
    private final Button dateButton;
    private final Spinner mealTypeSpinner;
    private LocalDate selectedDate;
    private final DialogSaveRecipeOnClickInterface mListener;

    public DialogSaveRecipe(@NonNull Context context, DialogSaveRecipeOnClickInterface listener) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        this.mListener = listener;
        selectedDate = LocalDate.now();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_save_recipe,
                null);

        dateButton = view.findViewById(R.id.btn_date);
        mealTypeSpinner = view.findViewById(R.id.spinner_meal_type);

        setupDateButton();
        initDatePicker(context);
        setupMealTypeSpinner(context);

        createAlertDialog(context, view);
    }

    /**
     * Creates an alert dialog for requesting the user for the date to save it for,
     * and for the meal type.
     *
     * @param context context required to create the alert dialog.
     * @param view custom view contained in the alert dialog.
     */
    private void createAlertDialog(@NonNull Context context, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Save Recipe");
        builder.setView(view);
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("Save", (dialog, which) -> mListener.onClickSave(selectedDate, (EnumMealType) mealTypeSpinner.getSelectedItem()));
        builder.show();
    }

    /**
     * Sets up the spinner by setting its adapter containing the meal types.
     *
     * @param context context required to create the adapter.
     */
    private void setupMealTypeSpinner(Context context) {
        if (mealTypeSpinner == null) return;

        // spinner adapter uses enum meal types for error prevention (Nielsen's principle)
        ArrayAdapter<EnumMealType> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, EnumMealType.values());
        mealTypeSpinner.setAdapter(adapter);
    }

    /**
     * Sets up the date button to show the date picker on click.
     */
    private void setupDateButton() {
        if (dateButton == null) return;
        dateButton.setOnClickListener(v -> datePickerDialog.show());
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
            month+=1;
            String date = year + "/" + month + "/" + dayOfMonth;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/dd");
            selectedDate = LocalDate.parse(date, formatter);

            // format to a more readable version - Day Month Year
            dateButton.setText(CalendarUtils.getFormattedDayMonthYearFromDate(selectedDate));
        });
    }

}
