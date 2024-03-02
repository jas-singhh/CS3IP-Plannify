package uk.ac.aston.cs3ip.plannify.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;

/**
 * This class is an adaptation of the text watcher to suit the needs for the
 * CreateRecipeFragment requirements.
 */
public class CreateRecipeCustomTextWatcher implements TextWatcher {

    private final TextView textView;// text view or edit text for which to implement the text watcher
    private final boolean checkAndClearErrorsForTextView;// flag to indicate whether to check and clear errors for the given text view
    private final Button buttonToEnableOnTextChanged;// button to enable when the text is changed - this is for the clear button in the CreateRecipeFragment

    public CreateRecipeCustomTextWatcher(TextView textView,boolean checkAndClearErrorsForTextView, Button buttonToEnableOnTextChanged) {
        this.textView = textView;
        this.checkAndClearErrorsForTextView = checkAndClearErrorsForTextView;
        this.buttonToEnableOnTextChanged = buttonToEnableOnTextChanged;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        // enable the button
        if(buttonToEnableOnTextChanged != null && !s.toString().trim().isEmpty()) buttonToEnableOnTextChanged.setEnabled(true);

        // check if we have to check and clear for errors
        if (checkAndClearErrorsForTextView) {
            if(textView != null && textView.getError() != null) textView.setError(null);
        }
    }
}
