package hu.petrik.hevpoc;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.ImageViewCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class HomeActivity extends AppCompatActivity {

    private static final String KEY_WITNESS_ACTIVE = "witness_active";
    private boolean witnessActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (savedInstanceState != null) {
            witnessActive = savedInstanceState.getBoolean(KEY_WITNESS_ACTIVE, true);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homeRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialButton btnAmbulance = findViewById(R.id.btnAmbulance);
        MaterialButton btnFire      = findViewById(R.id.btnFire);
        MaterialButton btnPolice    = findViewById(R.id.btnPolice);

        SwitchMaterial switchWitness   = findViewById(R.id.switchWitness);
        ImageView ivWitnessIcon        = findViewById(R.id.ivWitnessIcon);
        TextView tvWitnessStatus       = findViewById(R.id.tvWitnessStatus);
        ImageButton btnMenu            = findViewById(R.id.btnMenu);

        // Restore witness state after configuration change
        switchWitness.setChecked(witnessActive);
        applyWitnessState(ivWitnessIcon, tvWitnessStatus, witnessActive);

        // Emergency service buttons
        btnAmbulance.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, Mento02Activity.class)));
        btnFire.setOnClickListener(v -> { /* dispatch: fire department */ });
        btnPolice.setOnClickListener(v -> { /* dispatch: police */ });

        // Witness toggle via switch
        switchWitness.setOnCheckedChangeListener((btn, checked) -> {
            witnessActive = checked;
            applyWitnessState(ivWitnessIcon, tvWitnessStatus, witnessActive);
        });

        // Help / overflow menu – redesigned as BottomSheetDialog
        btnMenu.setOnClickListener(v -> showHelpSheet());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_WITNESS_ACTIVE, witnessActive);
    }

    private void applyWitnessState(ImageView icon, TextView status, boolean active) {
        int iconTint = active
                ? getColor(R.color.green_action)
                : getColor(R.color.gray_witness);
        int statusColor = active
                ? getColor(R.color.green_action)
                : getColor(R.color.gray_witness);
        int statusText = active
                ? R.string.home_witness_on
                : R.string.home_witness_off;

        ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(iconTint));
        status.setTextColor(statusColor);
        status.setText(statusText);
    }

    private void showHelpSheet() {
        BottomSheetDialog sheet = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this)
                .inflate(R.layout.bottom_sheet_help, null);

        LinearLayout itemPersonalData = sheetView.findViewById(R.id.sheetItemPersonalData);
        LinearLayout itemHelp         = sheetView.findViewById(R.id.sheetItemHelp);
        LinearLayout itemEvoaid       = sheetView.findViewById(R.id.sheetItemEvoaid);

        itemPersonalData.setOnClickListener(v -> {
            sheet.dismiss();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        });
        itemHelp.setOnClickListener(v -> {
            sheet.dismiss();
            openUrl(getString(R.string.evoaid_base_url));
        });
        itemEvoaid.setOnClickListener(v -> {
            sheet.dismiss();
            openUrl(getString(R.string.evoaid_base_url));
        });

        sheet.setContentView(sheetView);
        sheet.show();
    }

    private void openUrl(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}

