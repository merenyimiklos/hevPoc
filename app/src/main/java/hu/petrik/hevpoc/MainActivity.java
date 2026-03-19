package hu.petrik.hevpoc;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Toast;

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
        MaterialButton btnSave = findViewById(R.id.btnSave);
        TextInputLayout tilDob = findViewById(R.id.tilDob);

        // Date of birth: tap the field or the calendar icon to open the picker
        etDob.setOnClickListener(v -> showDatePicker());
        tilDob.setEndIconOnClickListener(v -> showDatePicker());

        // Hearing impaired toggle
        btnHearingYes.setOnClickListener(v -> setHearing(true));
        btnHearingNo.setOnClickListener(v -> setHearing(false));

        // Save
        btnSave.setOnClickListener(v -> saveData());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, y, m, d) -> {
            String date = String.format("%04d. %02d. %02d.", y, m + 1, d);
            etDob.setText(date);
        }, year, month, day).show();
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
    }

    private void saveData() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String dob = etDob.getText() != null ? etDob.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";

        if (name.isEmpty() || dob.isEmpty() || address.isEmpty() || !hearingSelected) {
            Toast.makeText(this, R.string.personal_data_toast_required, Toast.LENGTH_LONG).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .putString("name", name)
                .putString("dob", dob)
                .putString("address", address)
                .putBoolean("hearing_impaired", isHearingImpaired)
                .apply();

        Toast.makeText(this, R.string.personal_data_saved, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}