package hu.petrik.hevpoc;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;

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
 * Rendor02 – police incident category selection screen.
 * Shown after the user taps RENDŐR on the home screen.
 * KÖVETKEZŐ stays gray until at least one category is selected.
 * When VAGYON ELLENI CSELEKMÉNY is selected, sub-categories expand.
 */
public class Rendor02Activity extends AppCompatActivity {

    public static final String EXTRA_CATEGORIES = "rendor_categories";
    public static final String EXTRA_EGYEB = "rendor_egyeb";

    private final List<MaterialButton> mainCategoryButtons = new ArrayList<>();
    private final List<String> selectedCategories = new ArrayList<>();

    private MaterialButton btnCatProperty;
    private View vagyonSubSection;

    private final List<MaterialButton> subCategoryButtons = new ArrayList<>();
    private final List<String> selectedSubCategories = new ArrayList<>();

    private TextInputEditText etEgyeb;
    private MaterialButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rendor02);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rendor02Root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // Main category buttons
        MaterialButton btnKidnap      = findViewById(R.id.btnCatKidnap);
        MaterialButton btnAttack      = findViewById(R.id.btnCatAttack);
        MaterialButton btnRobbery     = findViewById(R.id.btnCatRobbery);
        MaterialButton btnHarassment  = findViewById(R.id.btnCatHarassment);
        MaterialButton btnBankRobbery = findViewById(R.id.btnCatBankRobbery);
        MaterialButton btnArmed       = findViewById(R.id.btnCatArmed);
        btnCatProperty                = findViewById(R.id.btnCatProperty);

        mainCategoryButtons.add(btnKidnap);
        mainCategoryButtons.add(btnAttack);
        mainCategoryButtons.add(btnRobbery);
        mainCategoryButtons.add(btnHarassment);
        mainCategoryButtons.add(btnBankRobbery);
        mainCategoryButtons.add(btnArmed);
        mainCategoryButtons.add(btnCatProperty);

        for (MaterialButton btn : mainCategoryButtons) {
            btn.setOnClickListener(v -> toggleCategory((MaterialButton) v));
        }

        // Sub-category section
        vagyonSubSection = findViewById(R.id.vagyonSubSection);

        MaterialButton btnDocs     = findViewById(R.id.btnCatDocs);
        MaterialButton btnBurglary = findViewById(R.id.btnCatBurglary);
        MaterialButton btnVehicle  = findViewById(R.id.btnCatVehicle);
        MaterialButton btnCash     = findViewById(R.id.btnCatCash);

        subCategoryButtons.add(btnDocs);
        subCategoryButtons.add(btnBurglary);
        subCategoryButtons.add(btnVehicle);
        subCategoryButtons.add(btnCash);

        for (MaterialButton btn : subCategoryButtons) {
            btn.setOnClickListener(v -> toggleSubCategory((MaterialButton) v));
        }

        etEgyeb = findViewById(R.id.etEgyeb);
        btnNext = findViewById(R.id.btnRendor02Next);
        MaterialButton btnBack = findViewById(R.id.btnRendor02Back);

        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            if (selectedCategories.isEmpty()) {
                ToastHelper.show(this, R.string.rendor02_toast_category);
                return;
            }
            ArrayList<String> allCategories = new ArrayList<>(selectedCategories);
            allCategories.addAll(selectedSubCategories);
            String egyeb = etEgyeb.getText() != null ? etEgyeb.getText().toString().trim() : "";

            Intent intent = new Intent(this, Rendor01Activity.class);
            intent.putStringArrayListExtra(EXTRA_CATEGORIES, allCategories);
            intent.putExtra(EXTRA_EGYEB, egyeb);
            startActivity(intent);
        });

        updateNextButton();
    }

    private void toggleCategory(MaterialButton btn) {
        String text = btn.getText().toString();
        boolean wasSelected = selectedCategories.contains(text);
        if (wasSelected) {
            selectedCategories.remove(text);
            setButtonSelected(btn, false);
        } else {
            selectedCategories.add(text);
            setButtonSelected(btn, true);
        }

        // Show/hide vagyon sub-section
        if (btn == btnCatProperty) {
            if (wasSelected) {
                // Deselected: hide sub-section and clear sub-selections
                vagyonSubSection.setVisibility(View.GONE);
                for (MaterialButton sub : subCategoryButtons) {
                    setButtonSelected(sub, false);
                }
                selectedSubCategories.clear();
            } else {
                vagyonSubSection.setVisibility(View.VISIBLE);
            }
        }

        updateNextButton();
    }

    private void toggleSubCategory(MaterialButton btn) {
        String text = btn.getText().toString();
        if (selectedSubCategories.contains(text)) {
            selectedSubCategories.remove(text);
            setButtonSelected(btn, false);
        } else {
            selectedSubCategories.add(text);
            setButtonSelected(btn, true);
        }
    }

    private void setButtonSelected(MaterialButton btn, boolean selected) {
        int bgColor = selected
                ? ContextCompat.getColor(this, R.color.blue_police)
                : ContextCompat.getColor(this, R.color.white);
        int textColor = selected
                ? ContextCompat.getColor(this, R.color.white)
                : ContextCompat.getColor(this, R.color.text_primary);
        btn.setBackgroundTintList(ColorStateList.valueOf(bgColor));
        btn.setTextColor(textColor);
    }

    private void updateNextButton() {
        boolean ready = !selectedCategories.isEmpty();
        int color = ready
                ? ContextCompat.getColor(this, R.color.green_action)
                : ContextCompat.getColor(this, R.color.gray_back);
        btnNext.setBackgroundTintList(ColorStateList.valueOf(color));
    }
}
