package hu.petrik.hevpoc;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Mento02 – age-group and incident-category selection screen.
 * Shown after the user taps MENTŐ on the home screen.
 * KÖVETKEZŐ stays gray until an age group AND at least one category are selected.
 */
public class Mento02Activity extends AppCompatActivity {

    public static final String EXTRA_AGE = "mento_age";
    public static final String EXTRA_CATEGORIES = "mento_categories";

    // Age group buttons (single selection)
    private MaterialButton btnAge0, btnAge1, btnAge2;
    private MaterialButton selectedAge = null;

    // Category buttons (multi-selection)
    private final List<MaterialButton> categoryButtons = new ArrayList<>();
    private final List<String> selectedCategories = new ArrayList<>();

    private MaterialButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mento02);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mento02Root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        btnAge0 = findViewById(R.id.btnAge0);
        btnAge1 = findViewById(R.id.btnAge1);
        btnAge2 = findViewById(R.id.btnAge2);

        MaterialButton btnCatHome    = findViewById(R.id.btnCatHome);
        MaterialButton btnCatRoad    = findViewById(R.id.btnCatRoad);
        MaterialButton btnCatInjury  = findViewById(R.id.btnCatInjury);
        MaterialButton btnCatIll     = findViewById(R.id.btnCatIll);
        MaterialButton btnCatTrapped = findViewById(R.id.btnCatTrapped);
        MaterialButton btnCatAllergy = findViewById(R.id.btnCatAllergy);
        MaterialButton btnCatPoison  = findViewById(R.id.btnCatPoison);
        MaterialButton btnCatChoke   = findViewById(R.id.btnCatChoke);

        btnNext = findViewById(R.id.btnMento02Next);
        MaterialButton btnBack = findViewById(R.id.btnMento02Back);

        // Age group single-select
        btnAge0.setOnClickListener(v -> selectAge(btnAge0, getString(R.string.mento02_age_0_1)));
        btnAge1.setOnClickListener(v -> selectAge(btnAge1, getString(R.string.mento02_age_1_14)));
        btnAge2.setOnClickListener(v -> selectAge(btnAge2, getString(R.string.mento02_age_14plus)));

        // Category multi-select – register each button
        categoryButtons.add(btnCatHome);
        categoryButtons.add(btnCatRoad);
        categoryButtons.add(btnCatInjury);
        categoryButtons.add(btnCatIll);
        categoryButtons.add(btnCatTrapped);
        categoryButtons.add(btnCatAllergy);
        categoryButtons.add(btnCatPoison);
        categoryButtons.add(btnCatChoke);

        for (MaterialButton btn : categoryButtons) {
            btn.setOnClickListener(v -> toggleCategory((MaterialButton) v));
        }

        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            if (selectedAge == null) {
                ToastHelper.show(this, R.string.mento02_toast_age);
                return;
            }
            if (selectedCategories.isEmpty()) {
                ToastHelper.show(this, R.string.mento02_toast_category);
                return;
            }
            Intent intent = new Intent(this, Mento01Activity.class);
            intent.putExtra(EXTRA_AGE, selectedAge.getText().toString());
            intent.putStringArrayListExtra(EXTRA_CATEGORIES, new ArrayList<>(selectedCategories));
            startActivity(intent);
        });

        updateNextButton();
    }

    private void selectAge(MaterialButton btn, String label) {
        // Deselect previously selected age button
        if (selectedAge != null) {
            setButtonSelected(selectedAge, false);
        }
        if (selectedAge == btn) {
            // Tapping same button deselects
            selectedAge = null;
        } else {
            selectedAge = btn;
            setButtonSelected(btn, true);
        }
        updateNextButton();
    }

    private void toggleCategory(MaterialButton btn) {
        String text = btn.getText().toString();
        if (selectedCategories.contains(text)) {
            selectedCategories.remove(text);
            setButtonSelected(btn, false);
        } else {
            selectedCategories.add(text);
            setButtonSelected(btn, true);
        }
        updateNextButton();
    }

    private void setButtonSelected(MaterialButton btn, boolean selected) {
        int bgColor = selected
                ? ContextCompat.getColor(this, R.color.red_ambulance)
                : ContextCompat.getColor(this, R.color.btn_unselected_dark);
        int textColor = selected
                ? ContextCompat.getColor(this, R.color.white)
                : ContextCompat.getColor(this, R.color.btn_unselected_dark_text);

        btn.setBackgroundTintList(ColorStateList.valueOf(bgColor));
        btn.setTextColor(textColor);
    }

    private void updateNextButton() {
        boolean ready = (selectedAge != null) && !selectedCategories.isEmpty();
        int color = ready
                ? ContextCompat.getColor(this, R.color.green_action)
                : ContextCompat.getColor(this, R.color.gray_back);
        btnNext.setBackgroundTintList(ColorStateList.valueOf(color));
    }
}
