package hu.petrik.hevpoc;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "user_data";

    private TextInputEditText etName;
    private TextInputEditText etDob;
    private TextInputEditText etAddress;
    private MaterialButton btnHearingYes;
    private MaterialButton btnHearingNo;
    private MaterialButton btnSave;

    private boolean hearingSelected = false;
    private boolean isHearingImpaired = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.personalDataRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etName = findViewById(R.id.etName);
        etDob = findViewById(R.id.etDob);
        etAddress = findViewById(R.id.etAddress);
        btnHearingYes = findViewById(R.id.btnHearingYes);
        btnHearingNo = findViewById(R.id.btnHearingNo);
        btnSave = findViewById(R.id.btnSave);
        TextInputLayout tilDob = findViewById(R.id.tilDob);

        // Date of birth: tap the field or the calendar icon to open the picker
        etDob.setOnClickListener(v -> showDatePicker());
        tilDob.setEndIconOnClickListener(v -> showDatePicker());

        // Hearing impaired toggle
        btnHearingYes.setOnClickListener(v -> setHearing(true));
        btnHearingNo.setOnClickListener(v -> setHearing(false));

        // Save button state watcher
        TextWatcher fieldWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { updateSaveButton(); }
        };
        etName.addTextChangedListener(fieldWatcher);
        etAddress.addTextChangedListener(fieldWatcher);

        // Save
        btnSave.setOnClickListener(v -> saveData());

        loadSavedData();
        updateSaveButton();
    }

    private void loadSavedData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString("name", "");
        String dob = prefs.getString("dob", "");
        String address = prefs.getString("address", "");

        if (!name.isEmpty()) etName.setText(name);
        if (!dob.isEmpty()) etDob.setText(dob);
        if (!address.isEmpty()) etAddress.setText(address);

        if (prefs.contains("hearing_impaired")) {
            setHearing(prefs.getBoolean("hearing_impaired", false));
        }
    }

    private void showDatePicker() {
        // Maximum allowed date: 14 years ago from today (minimum age 14, no future dates)
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, -14);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            String date = String.format("%04d. %02d. %02d.", y, m + 1, d);
            etDob.setText(date);
            updateSaveButton();
        }, maxDate.get(Calendar.YEAR), maxDate.get(Calendar.MONTH), maxDate.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        dialog.show();
    }

    private void setHearing(boolean isYes) {
        hearingSelected = true;
        isHearingImpaired = isYes;

        int activeColor = ContextCompat.getColor(this, R.color.green_action);
        int inactiveColor = ContextCompat.getColor(this, R.color.border_default);
        int activeTextColor = ContextCompat.getColor(this, R.color.white);
        int inactiveTextColor = ContextCompat.getColor(this, R.color.text_primary);

        btnHearingYes.setBackgroundTintList(
                ColorStateList.valueOf(isYes ? activeColor : inactiveColor));
        btnHearingYes.setTextColor(isYes ? activeTextColor : inactiveTextColor);

        btnHearingNo.setBackgroundTintList(
                ColorStateList.valueOf(isYes ? inactiveColor : activeColor));
        btnHearingNo.setTextColor(isYes ? inactiveTextColor : activeTextColor);

        updateSaveButton();
    }

    private void updateSaveButton() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String dob = etDob.getText() != null ? etDob.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
        boolean ready = !name.isEmpty() && !dob.isEmpty() && !address.isEmpty() && hearingSelected;
        int color = ready
                ? ContextCompat.getColor(this, R.color.green_action)
                : ContextCompat.getColor(this, R.color.gray_back);
        btnSave.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void saveData() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String dob = etDob.getText() != null ? etDob.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";

        if (name.isEmpty() || dob.isEmpty() || address.isEmpty() || !hearingSelected) {
            ToastHelper.show(this, R.string.personal_data_toast_required);
            return;
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .putString("name", name)
                .putString("dob", dob)
                .putString("address", address)
                .putBoolean("hearing_impaired", isHearingImpaired)
                .apply();

        ToastHelper.show(this, R.string.personal_data_saved);
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
