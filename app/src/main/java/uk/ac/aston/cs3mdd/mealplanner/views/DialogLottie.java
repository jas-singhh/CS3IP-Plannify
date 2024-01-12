package uk.ac.aston.cs3mdd.mealplanner.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import uk.ac.aston.cs3mdd.mealplanner.R;

public class DialogLottie extends Dialog {

    // Reference: https://stackoverflow.com/questions/64259901/how-use-lottie-in-custom-progress-dialog-in-android-studio

    public DialogLottie(@NonNull Context context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        assert  getWindow() != null;
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_lottie,
                null);

        setContentView(view);
    }
}
