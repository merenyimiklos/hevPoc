package hu.petrik.hevpoc;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Rendor01 – police SMS finalization screen.
 * Receives categories from Rendor02Activity, shows live GPS coordinates,
 * lets user pick person count and mark life threat, then opens SMS app.
 */
public class Rendor01Activity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1001;
    private static final String PREFS_NAME = "user_data";

    private MaterialButton btnCountOne, btnCountMany, btnLifeThreat, btnSend;
    private TextInputEditText etExtra;
    private TextView tvPreview, tvLocation;

    private MaterialButton selectedCount = null;
    private boolean lifeThreatActive = false;

    private List<String> categories = new ArrayList<>();
    private String egyebFromCategory = "";

    // Location state
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double currentLat = Double.NaN;
    private double currentLon = Double.NaN;
    private float currentAccuracy = 0;
    private String currentAddress = "";
    private volatile boolean geocodingActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rendor01);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rendor01Root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        if (getIntent() != null) {
            List<String> cats = getIntent().getStringArrayListExtra(Rendor02Activity.EXTRA_CATEGORIES);
            if (cats != null) categories = cats;
            String egyeb = getIntent().getStringExtra(Rendor02Activity.EXTRA_EGYEB);
            if (egyeb != null) egyebFromCategory = egyeb;
        }

        btnCountOne   = findViewById(R.id.btnCountOne);
        btnCountMany  = findViewById(R.id.btnCountMany);
        btnLifeThreat = findViewById(R.id.btnLifeThreat);
        btnSend       = findViewById(R.id.btnSendSms);
        etExtra       = findViewById(R.id.etExtra);
        tvPreview     = findViewById(R.id.tvPreview);
        tvLocation    = findViewById(R.id.tvLocation);

        MaterialButton btnBack = findViewById(R.id.btnRendor01Back);

        btnCountOne.setOnClickListener(v -> selectCount(btnCountOne));
        btnCountMany.setOnClickListener(v -> selectCount(btnCountMany));
        btnLifeThreat.setOnClickListener(v -> toggleLifeThreat());
        btnBack.setOnClickListener(v -> finish());

        btnSend.setOnClickListener(v -> {
            if (selectedCount == null) {
                ToastHelper.show(this, R.string.mento01_toast_count);
                return;
            }
            sendSms();
        });

        etExtra.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { updatePreview(); }
        });

        startLocationUpdates();
        updatePreview();
        updateSendButton();
    }

    private void startLocationUpdates() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            tvLocation.setText(getString(R.string.location_permission_needed));
            return;
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLat = location.getLatitude();
                currentLon = location.getLongitude();
                currentAccuracy = location.getAccuracy();
                updateLocationDisplay();
                fetchAddress(location);
                updatePreview();
            }

            @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override public void onProviderEnabled(String provider) {}
            @Override public void onProviderDisabled(String provider) {}
        };

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    3000, 0, locationListener);
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    3000, 0, locationListener);
        }

        // Show last known location immediately while waiting for fresh fix
        Location lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnown == null) {
            lastKnown = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (lastKnown != null) {
            locationListener.onLocationChanged(lastKnown);
        } else {
            tvLocation.setText(getString(R.string.location_waiting));
        }
    }

    private void updateLocationDisplay() {
        if (Double.isNaN(currentLat)) {
            tvLocation.setText(getString(R.string.location_waiting));
        } else {
            String text = String.format(Locale.US, "📍 %.7f, %.7f  ±%.1fm",
                    currentLat, currentLon, currentAccuracy);
            if (!currentAddress.isEmpty()) {
                text += "\n" + currentAddress;
            }
            tvLocation.setText(text);
        }
    }

    private void fetchAddress(Location loc) {
        if (geocodingActive) return;
        geocodingActive = true;
        new Thread(() -> {
            String address = "";
            try {
                Geocoder gc = new Geocoder(this, new Locale("hu", "HU"));
                List<Address> results = gc.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                if (results != null && !results.isEmpty()) {
                    address = results.get(0).getAddressLine(0);
                }
            } catch (IOException e) {
                // ignore – address is optional
            }
            final String finalAddr = address;
            runOnUiThread(() -> {
                geocodingActive = false;
                currentAddress = finalAddr;
                updateLocationDisplay();
                updatePreview();
            });
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private void selectCount(MaterialButton btn) {
        if (selectedCount != null) setButtonSelected(selectedCount, false);
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
                ? ContextCompat.getColor(this, R.color.blue_police)
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
        sb.append("SOS üzenet\n");

        if (!categories.isEmpty()) {
            sb.append(android.text.TextUtils.join(", ", categories)).append("\n");
        }
        if (!egyebFromCategory.isEmpty()) {
            sb.append(egyebFromCategory).append("\n");
        }

        sb.append("Segélykérő helyzete:\n");
        if (!Double.isNaN(currentLat)) {
            sb.append(String.format(Locale.US, "%.7f\n%.7f, GPS pontosság %.2f m\n",
                    currentLat, currentLon, currentAccuracy));
            if (!currentAddress.isEmpty()) {
                sb.append("Kb. cím: ").append(currentAddress).append("\n");
            }
        } else {
            sb.append("(Helymeghatározás folyamatban...)\n");
        }
        sb.append("Kérem küldjenek segítséget!\n");

        if (lifeThreatActive) {
            sb.append("ÉLETVESZÉLY\n");
        }
        if (selectedCount != null) {
            sb.append(selectedCount.getText()).append("\n");
        }

        String extra = etExtra.getText() != null ? etExtra.getText().toString().trim() : "";
        if (!extra.isEmpty()) {
            sb.append(extra).append("\n");
        }

        // Personal data from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString("name", "");
        String dob = prefs.getString("dob", "");
        String homeAddress = prefs.getString("address", "");
        if (!name.isEmpty()) {
            sb.append("\nSegélykérő adatai: ").append(name);
            if (!dob.isEmpty()) sb.append(", szül. ideje ").append(dob);
            if (!homeAddress.isEmpty()) sb.append(",\nlakcím: ").append(homeAddress);
        }

        tvPreview.setText(sb.toString().trim());
    }

    private void sendSms() {
        String body = tvPreview.getText().toString();
        try {
            Uri smsUri = Uri.parse("smsto:" + getString(R.string.sms_emergency_number));
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, smsUri);
            smsIntent.putExtra("sms_body", body);
            startActivity(smsIntent);
        } catch (ActivityNotFoundException e) {
            ToastHelper.show(this, R.string.sms_app_not_found);
        }
    }
}
