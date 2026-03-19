package hu.petrik.hevpoc;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class TermsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "user_data";
    private static final String KEY_TERMS_ACCEPTED = "terms_accepted";
    private static final int REQUEST_LOCATION_PERMISSION = 1001;

    private CheckBox cbAuf;
    private CheckBox cbPrivacy;
    private MaterialButton btnAccept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Skip setup screens if user already accepted terms and filled in their data
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(KEY_TERMS_ACCEPTED, false)) {
            String name = prefs.getString("name", "");
            Class<?> dest = name.isEmpty() ? MainActivity.class : HomeActivity.class;
            startActivity(new Intent(this, dest));
            finish();
            return;
        }

        setContentView(R.layout.activity_terms);

        // Request location permission immediately on first launch so the dialog
        // appears before the user interacts with the terms screen.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    REQUEST_LOCATION_PERMISSION);
        }

        // Apply window insets so content is not hidden behind status/nav bars.
        // The red header behind the status bar creates a visually seamless look.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.termsRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cbAuf = findViewById(R.id.cbAuf);
        cbPrivacy = findViewById(R.id.cbPrivacy);
        btnAccept = findViewById(R.id.btnAccept);

        LinearLayout rowCheckAuf = findViewById(R.id.rowCheckAuf);
        LinearLayout rowCheckPrivacy = findViewById(R.id.rowCheckPrivacy);
        LinearLayout btnAuf = findViewById(R.id.btnAuf);
        LinearLayout btnPrivacy = findViewById(R.id.btnPrivacy);
        MaterialButton btnExit = findViewById(R.id.btnExit);

        // Toggle checkboxes via full-row tap
        rowCheckAuf.setOnClickListener(v -> cbAuf.setChecked(!cbAuf.isChecked()));
        rowCheckPrivacy.setOnClickListener(v -> cbPrivacy.setChecked(!cbPrivacy.isChecked()));

        // Open documents in browser
        btnAuf.setOnClickListener(v -> openUrl(getString(R.string.terms_auf_url)));
        btnPrivacy.setOnClickListener(v -> openUrl(getString(R.string.terms_privacy_url)));

        // Exit: close the application
        btnExit.setOnClickListener(v -> finishAffinity());

        // Accept button state tracking
        CompoundButton.OnCheckedChangeListener checkWatcher = (btn, checked) -> updateAcceptButton();
        cbAuf.setOnCheckedChangeListener(checkWatcher);
        cbPrivacy.setOnCheckedChangeListener(checkWatcher);

        // Accept: require both checkboxes before proceeding
        btnAccept.setOnClickListener(v -> {
            if (cbAuf.isChecked() && cbPrivacy.isChecked()) {
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                        .edit()
                        .putBoolean(KEY_TERMS_ACCEPTED, true)
                        .apply();
                startActivity(new Intent(TermsActivity.this, MainActivity.class));
                finish();
            } else {
                ToastHelper.show(this, R.string.terms_toast_required);
            }
        });

        updateAcceptButton();
    }

    private void updateAcceptButton() {
        boolean ready = cbAuf.isChecked() && cbPrivacy.isChecked();
        int color = ready
                ? ContextCompat.getColor(this, R.color.green_action)
                : ContextCompat.getColor(this, R.color.gray_back);
        btnAccept.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void openUrl(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}

