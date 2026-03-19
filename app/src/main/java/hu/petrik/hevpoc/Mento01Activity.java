package hu.petrik.hevpoc;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Mento01 – SMS finalization / confirmation screen.
 * Receives age and categories from Mento02Activity, lets user pick
 * person count (EGY / TÖBB ÉRINTETT) and mark life threat, then send.
 * SMS VÉGLEGESÍTÉSE turns green only when a count option is selected.
 */
public class Mento01Activity extends AppCompatActivity {

    private MaterialButton btnCountOne, btnCountMany, btnLifeThreat;
    private MaterialButton btnSend;
    private TextInputEditText etExtra;
    private TextView tvPreview;

    private MaterialButton selectedCount = null;
    private boolean lifeThreatActive = false;

    private String ageLabel = "";
    private List<String> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mento01);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mento01Root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // Retrieve data passed from Mento02
        if (getIntent() != null) {
            ageLabel = getIntent().getStringExtra(Mento02Activity.EXTRA_AGE);
            if (ageLabel == null) ageLabel = "";
            List<String> cats = getIntent().getStringArrayListExtra(Mento02Activity.EXTRA_CATEGORIES);
            if (cats != null) categories = cats;
        }

        btnCountOne  = findViewById(R.id.btnCountOne);
        btnCountMany = findViewById(R.id.btnCountMany);
        btnLifeThreat = findViewById(R.id.btnLifeThreat);
        btnSend      = findViewById(R.id.btnSendSms);
        etExtra      = findViewById(R.id.etExtra);
        tvPreview    = findViewById(R.id.tvPreview);

        MaterialButton btnBack = findViewById(R.id.btnMento01Back);

        btnCountOne.setOnClickListener(v -> selectCount(btnCountOne));
        btnCountMany.setOnClickListener(v -> selectCount(btnCountMany));
        btnLifeThreat.setOnClickListener(v -> toggleLifeThreat());

        btnBack.setOnClickListener(v -> finish());

        btnSend.setOnClickListener(v -> {
            if (selectedCount == null) {
                ToastHelper.show(this, R.string.mento01_toast_count);
                return;
            }
            ToastHelper.show(this, R.string.mento01_toast_sent);
            // Existing dispatch/send logic would go here
        });

        // Update preview whenever extra message changes
        etExtra.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { updatePreview(); }
        });

        updatePreview();
        updateSendButton();
    }

    private void selectCount(MaterialButton btn) {
        if (selectedCount != null) {
            setButtonSelected(selectedCount, false);
        }
        if (selectedCount == btn) {
            selectedCount = null;
        } else {
            selectedCount = btn;
            setButtonSelected(btn, true);
        }
        updateSendButton();
        updatePreview();
    }

    private void toggleLifeThreat() {
        lifeThreatActive = !lifeThreatActive;
        setButtonSelected(btnLifeThreat, lifeThreatActive);
        updatePreview();
    }

    private void setButtonSelected(MaterialButton btn, boolean selected) {
        int bgColor = selected
                ? ContextCompat.getColor(this, R.color.red_ambulance)
                : ContextCompat.getColor(this, R.color.white);
        int textColor = selected
                ? ContextCompat.getColor(this, R.color.white)
                : ContextCompat.getColor(this, R.color.text_primary);
        btn.setBackgroundTintList(ColorStateList.valueOf(bgColor));
        btn.setTextColor(textColor);
    }

    private void updateSendButton() {
        boolean ready = selectedCount != null;
        int color = ready
                ? ContextCompat.getColor(this, R.color.green_action)
                : ContextCompat.getColor(this, R.color.gray_back);
        btnSend.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void updatePreview() {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.mento01_preview_header)).append("\n");

        if (!ageLabel.isEmpty()) {
            sb.append(ageLabel);
            if (!categories.isEmpty()) {
                sb.append("; ");
            }
        }
        if (!categories.isEmpty()) {
            sb.append(android.text.TextUtils.join(", ", categories));
        }
        sb.append("\n");

        if (lifeThreatActive) {
            sb.append(getString(R.string.mento01_life_threat)).append("\n");
        }
        if (selectedCount != null) {
            sb.append(selectedCount.getText()).append("\n");
        }

        String extra = etExtra.getText() != null ? etExtra.getText().toString().trim() : "";
        if (!extra.isEmpty()) {
            sb.append(extra).append("\n");
        }

        sb.append("\n").append(getString(R.string.mento01_preview_footer));

        tvPreview.setText(sb.toString());
    }
}
