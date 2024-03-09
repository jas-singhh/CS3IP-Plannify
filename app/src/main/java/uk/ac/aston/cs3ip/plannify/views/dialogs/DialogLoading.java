package uk.ac.aston.cs3ip.plannify.views.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import uk.ac.aston.cs3ip.plannify.R;

public class DialogLoading extends Dialog {

    // Reference: https://stackoverflow.com/questions/64259901/how-use-lottie-in-custom-progress-dialog-in-android-studio

    public DialogLoading(@NonNull Context context) {
        super(context);

        assert getWindow() != null;
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = View.inflate(context, R.layout.dialog_loading, null);

        // reference: https://copyprogramming.com/howto/changing-background-dim-color-for-dialog-appears-in-android
        // do not dim the background
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        setContentView(view);
    }
}
