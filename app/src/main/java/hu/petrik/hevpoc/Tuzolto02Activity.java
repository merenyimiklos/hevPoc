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
 * Tuzolto02 – fire department category selection screen.
 * Shown after the user taps TŰZOLTÓ on the home screen.
 * KÖVETKEZŐ stays gray until at least one category is selected.
 */
public class Tuzolto02Activity extends AppCompatActivity {

    public static final String EXTRA_CATEGORIES = "tuzolto_categories";

    private final List<MaterialButton> categoryButtons = new ArrayList<>();
    private final List<String> selectedCategories = new ArrayList<>();

    private MaterialButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuzolto02);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tuzolto02Root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        MaterialButton btnAccident = findViewById(R.id.btnCatAccident);
        MaterialButton btnFire     = findViewById(R.id.btnCatFireEvent);
        MaterialButton btnDisaster = findViewById(R.id.btnCatDisaster);
        MaterialButton btnRescue   = findViewById(R.id.btnCatRescue);

        categoryButtons.add(btnAccident);
        categoryButtons.add(btnFire);
        categoryButtons.add(btnDisaster);
        categoryButtons.add(btnRescue);

        for (MaterialButton btn : categoryButtons) {
            btn.setOnClickListener(v -> toggleCategory((MaterialButton) v));
        }

        btnNext = findViewById(R.id.btnTuzolto02Next);
        MaterialButton btnBack = findViewById(R.id.btnTuzolto02Back);

        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            if (selectedCategories.isEmpty()) {
                ToastHelper.show(this, R.string.tuzolto02_toast_category);
                return;
            }
            Intent intent = new Intent(this, Tuzolto01Activity.class);
            intent.putStringArrayListExtra(EXTRA_CATEGORIES, new ArrayList<>(selectedCategories));
            startActivity(intent);
        });

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
                ? ContextCompat.getColor(this, R.color.yellow_fire)
                : ContextCompat.getColor(this, R.color.btn_unselected_dark);
        int textColor = selected
                ? ContextCompat.getColor(this, R.color.white)
                : ContextCompat.getColor(this, R.color.btn_unselected_dark_text);
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
