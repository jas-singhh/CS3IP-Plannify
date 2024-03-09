package uk.ac.aston.cs3ip.plannify.views.dialogs;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import uk.ac.aston.cs3ip.plannify.MainActivity;
import uk.ac.aston.cs3ip.plannify.R;

public class DialogGetNotified extends Dialog {

    // Reference: https://stackoverflow.com/questions/64259901/how-use-lottie-in-custom-progress-dialog-in-android-studio

    public DialogGetNotified(@NonNull Context context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        assert getWindow() != null;
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = View.inflate(context, R.layout.dialog_get_notified,
                null);

        initCancelButton(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            initAcceptButton(view, context);
        }

        setContentView(view);

        this.show();
    }

    private void initCancelButton(View view) {
        Button cancelBtn = view.findViewById(R.id.notifications_cancel_btn);
        cancelBtn.setOnClickListener(v -> dismiss());
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void initAcceptButton(View view, Context context) {
        Button acceptBtn = view.findViewById(R.id.notifications_accept_btn);
        acceptBtn.setOnClickListener(v -> ((MainActivity)(context)).requestPermissions(new String[] {Manifest.permission.POST_NOTIFICATIONS}, MainActivity.RC_NOTIFICATIONS));
    }
}
