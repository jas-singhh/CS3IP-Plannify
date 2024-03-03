package uk.ac.aston.cs3ip.plannify.views.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import uk.ac.aston.cs3ip.plannify.MainActivity;
import uk.ac.aston.cs3ip.plannify.R;
import uk.ac.aston.cs3ip.plannify.adapters.DialogSaveRecipeOnClickInterface;
import uk.ac.aston.cs3ip.plannify.enums.EnumMealType;
import uk.ac.aston.cs3ip.plannify.models.api_recipe.NetworkRecipe;
import uk.ac.aston.cs3ip.plannify.utils.CalendarUtils;
import uk.ac.aston.cs3ip.plannify.utils.Utilities;


public class DialogSaveRecipe extends Dialog {

    private Dialog dialog;
    private DatePickerDialog datePickerDialog;
    private final TextView dateBtn;
    private final MaterialAutoCompleteTextView mealTypeAutoTextView;
    private LocalDate selectedDate;
    private final TextView cancelBtn;
    private final MaterialButton saveBtn;
    private final DialogSaveRecipeOnClickInterface mListener;

    public DialogSaveRecipe(@NonNull Context context, NetworkRecipe selectedNetworkRecipe, DialogSaveRecipeOnClickInterface listener) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_save_recipe, null);
        mListener = listener;

        selectedDate = LocalDate.now();
        dateBtn = view.findViewById(R.id.btn_date);
        mealTypeAutoTextView = view.findViewById(R.id.auto_tv_meal_type);
        cancelBtn = view.findViewById(R.id.custom_meal_cancel_btn);
        saveBtn = view.findViewById(R.id.custom_meal_save_btn);

        initRecipeDetails(view, selectedNetworkRecipe);
        initDatePicker(context);
        initDateButton();
        initMealTypeAutoTextView(context);
        initCancelButton();
        initSaveButton();

        createAlertDialog(context, view);
    }

    private void initRecipeDetails(View view, NetworkRecipe selectedNetworkRecipe) {
        if (view == null || selectedNetworkRecipe == null) return;

        // set up the meal image
        ImageView img = view.findViewById(R.id.dialog_save_meal_image);
        Picasso.get().load(selectedNetworkRecipe.getImage())
                .error(R.drawable.img_image_not_available)
                .placeholder(R.drawable.img_image_not_available)
                .into(img);

        // set up name
        ((TextView)(view.findViewById(R.id.dialog_save_meal_name))).setText(selectedNetworkRecipe.getTitle());

        // set up the meal health rating
        LinearLayout parentLayout = view.findViewById(R.id.dialog_save_meal_health_rating_parent);
        int numStars = Utilities.getStarRatingFromHealthScore(selectedNetworkRecipe.getHealthScore());
        ImageView[] stars = new ImageView[numStars];
        for (int i = 0; i < stars.length; i++) {
            ImageView star = new ImageView(view.getContext());
            star.setImageResource(R.drawable.ic_star_rating);
            star.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            star.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.semi_transparent_white));
            parentLayout.addView(star);
        }

        // set up the meal calories and preparation time
        String calories = "N/A";
        if (selectedNetworkRecipe.getNutrition() != null && selectedNetworkRecipe.getNutrition().getNutrients() != null) {
            calories = Math.round(selectedNetworkRecipe.getNutrition().getNutrients().get(0).getAmount()) + " kcal";
        }
        ((TextView)(view.findViewById(R.id.dialog_save_meal_calories))).setText(calories);

        String readyInMinutes = selectedNetworkRecipe.getReadyInMinutes() + " m";
        ((TextView)(view.findViewById(R.id.dialog_save_meal_preparation_time))).setText(readyInMinutes);
    }


    private void createAlertDialog(Context context, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setNegativeButton(null, null);
        builder.setPositiveButton(null, null);

        dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            View v = dialog.getWindow().getDecorView();
            v.setBackgroundResource(android.R.color.transparent);

        }
        dialog.show();
    }


    /**
     * Sets up the meal type autocomplete text view by setting its adapter containing
     * the meal types.
     *
     * @param context context required to create the adapter.
     */
    private void initMealTypeAutoTextView(Context context) {
        if (mealTypeAutoTextView == null) return;

        // NOTE: the order is important here - setting the adapter before the default value does
        // not display all the values in the adapter.

        // meal type auto text view adapter uses enum meal types for error prevention (Nielsen's principle)
        ArrayAdapter<EnumMealType> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item, EnumMealType.values());

        // set the default value as breakfast
        EnumMealType selection = adapter.getItem(adapter.getPosition(EnumMealType.BREAKFAST));
        if (selection != null) {
            mealTypeAutoTextView.setText(selection.getMealType());
        }

        mealTypeAutoTextView.setAdapter(adapter);
    }

    /**
     * Sets up the date button to show the date picker on click.
     */
    private void initDateButton() {
        if (dateBtn == null) return;
        dateBtn.setOnClickListener(v -> datePickerDialog.show());

        // set default value for date button as today
        dateBtn.setText(R.string.today);
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

            updateDateButtonText();
        });
    }

    /**
     * Updates the text of the date button to reflect the selected date by the user.
     * The default value displayed is "Today" if the user does not make any selection.
     */
    private void updateDateButtonText() {
        // only display the selected date if it is not today's date as we have already set the text to "today" at initialisation
        if (!selectedDate.isEqual(LocalDate.now())) {

            // display tomorrow if the selected date is tomorrow's
            if (selectedDate.isEqual(LocalDate.now().plusDays(1)))
                dateBtn.setText(R.string.tomorrow);
            else
                // format to a more readable version - Day Month Year
                dateBtn.setText(CalendarUtils.getFormattedDayMonthYearFromDate(selectedDate));
        }
    }

    /**
     * Initialises the cancel button to dismiss the dialog.
     */
    private void initCancelButton() {
        if (cancelBtn != null) {
            cancelBtn.setOnClickListener(v -> {
                // dismiss the dialog
                if (dialog != null) dialog.dismiss();
            });
        }
    }

    /**
     * Initialises the save button to perform the callback containing the selected
     * date and meal type.
     */
    private void initSaveButton() {
        if (saveBtn != null) {
            saveBtn.setOnClickListener(v -> {
                Log.d(MainActivity.TAG, "save clicked");
                // perform a callback
                if (mListener != null) mListener.onClickSave(selectedDate,
                        EnumMealType.getEnumMealTypeFromValue(mealTypeAutoTextView.getText().toString()));

                // display a toast
                Toast.makeText(v.getContext(), "Recipe Saved", Toast.LENGTH_SHORT).show();

                // close the dialog
                if (dialog != null) dialog.dismiss();
            });
        }
    }
}
