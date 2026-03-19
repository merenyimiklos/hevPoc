package hu.petrik.hevpoc;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

/**
 * Shows a custom-branded Snackbar toast (dark rounded card + app icon) instead
 * of the default system Toast, giving every screen a consistent look.
 */
public class ToastHelper {

    private ToastHelper() {}

    public static void show(Activity activity, int messageResId) {
        show(activity, activity.getString(messageResId));
    }

    public static void show(Activity activity, String message) {
        View root = activity.getWindow().getDecorView().getRootView();
        Snackbar snackbar = Snackbar.make(root, message, Snackbar.LENGTH_LONG);

        View snackView = snackbar.getView();
        snackView.setBackgroundResource(R.drawable.bg_toast_dark);

        TextView tv = snackView.findViewById(
                com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(android.graphics.Color.WHITE);
        tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14f);
        tv.setMaxLines(3);

        // Attach a 28dp version of the app icon as compound drawable
        Drawable icon = ContextCompat.getDrawable(activity, R.drawable.logo_2022_512);
        if (icon != null) {
            float density = activity.getResources().getDisplayMetrics().density;
            int sizePx = Math.round(28 * density);
            icon.setBounds(0, 0, sizePx, sizePx);
            tv.setCompoundDrawables(icon, null, null, null);
            tv.setCompoundDrawablePadding(Math.round(12 * density));
        }

        // Center the snackbar on screen
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackView.getLayoutParams();
        params.gravity = Gravity.CENTER;
        snackView.setLayoutParams(params);

        snackbar.show();
    }
}
