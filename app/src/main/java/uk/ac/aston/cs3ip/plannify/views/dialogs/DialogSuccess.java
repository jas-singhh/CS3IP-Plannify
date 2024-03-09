package uk.ac.aston.cs3ip.plannify.views.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import uk.ac.aston.cs3ip.plannify.R;

public class DialogSuccess extends Dialog {

    // Reference: https://stackoverflow.com/questions/64259901/how-use-lottie-in-custom-progress-dialog-in-android-studio

    public DialogSuccess(@NonNull Context context, String description) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        View view = View.inflate(context, R.layout.dialog_success, null);

        TextView descriptionTextView = view.findViewById(R.id.dialog_confirmation_text);
        descriptionTextView.setText(description);

        createAlertDialog(context, view);
    }

    private void createAlertDialog(Context context, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(true);
        builder.setNegativeButton(null, null);
        builder.setPositiveButton(null, null);

        Dialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            View v = dialog.getWindow().getDecorView();
            v.setBackgroundResource(android.R.color.transparent);
        }
        dialog.show();

//        reference: https://stackoverflow.com/questions/14445745/android-close-dialog-after-5-seconds
        // automatically close the dialog after 5 seconds
        Handler handler = new Handler();
        Runnable runnable = () -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        };

        dialog.setOnDismissListener(dialog1 -> handler.removeCallbacks(runnable));

        handler.postDelayed(runnable, 4 * 1000);
    }

}
