package hu.petrik.hevpoc;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

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
        MaterialButton btnFire = findViewById(R.id.btnFire);
        MaterialButton btnPolice = findViewById(R.id.btnPolice);
        MaterialButton btnWitness = findViewById(R.id.btnWitness);
        ImageButton btnMenu = findViewById(R.id.btnMenu);

        // Restore witness button appearance after configuration change
        applyWitnessState(btnWitness, witnessActive);

        // Emergency service buttons – connect to dispatch backend when logic is wired up
        btnAmbulance.setOnClickListener(v -> { /* dispatch: ambulance */ });
        btnFire.setOnClickListener(v -> { /* dispatch: fire department */ });
        btnPolice.setOnClickListener(v -> { /* dispatch: police */ });

        // Witness button toggles the user's witness role on/off
        btnWitness.setOnClickListener(v -> {
            witnessActive = !witnessActive;
            applyWitnessState(btnWitness, witnessActive);
        });

        // Overflow menu
        btnMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenuInflater().inflate(R.menu.home_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.menu_personal_data) {
                    startActivity(new Intent(HomeActivity.this, MainActivity.class));
                    return true;
                } else if (id == R.id.menu_help || id == R.id.menu_evoaid) {
                    openUrl(getString(R.string.evoaid_base_url));
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_WITNESS_ACTIVE, witnessActive);
    }

    private void applyWitnessState(MaterialButton btn, boolean active) {
        if (active) {
            btn.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green_action)));
            btn.setTextColor(ContextCompat.getColor(this, R.color.white));
            btn.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_eye));
            btn.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            btn.setStrokeWidth(0);
        } else {
            btn.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            btn.setTextColor(ContextCompat.getColor(this, R.color.gray_witness));
            btn.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_eye_off));
            btn.setIconTint(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.gray_witness)));
            btn.setStrokeColor(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.border_default)));
            btn.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.witness_stroke_width));
        }
    }

    private void openUrl(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}
