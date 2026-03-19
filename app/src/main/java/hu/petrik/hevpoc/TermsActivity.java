package hu.petrik.hevpoc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class TermsActivity extends AppCompatActivity {

    private CheckBox cbAuf;
    private CheckBox cbPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        // Apply window insets so content is not hidden behind status/nav bars.
        // The red header behind the status bar creates a visually seamless look.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.termsRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cbAuf = findViewById(R.id.cbAuf);
        cbPrivacy = findViewById(R.id.cbPrivacy);

        LinearLayout rowCheckAuf = findViewById(R.id.rowCheckAuf);
        LinearLayout rowCheckPrivacy = findViewById(R.id.rowCheckPrivacy);
        LinearLayout btnAuf = findViewById(R.id.btnAuf);
        LinearLayout btnPrivacy = findViewById(R.id.btnPrivacy);
        MaterialButton btnExit = findViewById(R.id.btnExit);
        MaterialButton btnAccept = findViewById(R.id.btnAccept);

        // Toggle checkboxes via full-row tap
        rowCheckAuf.setOnClickListener(v -> cbAuf.setChecked(!cbAuf.isChecked()));
        rowCheckPrivacy.setOnClickListener(v -> cbPrivacy.setChecked(!cbPrivacy.isChecked()));

        // Open documents in browser
        btnAuf.setOnClickListener(v -> openUrl(getString(R.string.terms_auf_url)));
        btnPrivacy.setOnClickListener(v -> openUrl(getString(R.string.terms_privacy_url)));

        // Exit: close the application
        btnExit.setOnClickListener(v -> finishAffinity());

        // Accept: require both checkboxes before proceeding
        btnAccept.setOnClickListener(v -> {
            if (cbAuf.isChecked() && cbPrivacy.isChecked()) {
                startActivity(new Intent(TermsActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, R.string.terms_toast_required, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openUrl(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}
