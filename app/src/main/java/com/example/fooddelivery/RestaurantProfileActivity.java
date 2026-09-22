package com.example.fooddelivery;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RestaurantProfileActivity extends AppCompatActivity {

    private EditText etRestaurantName, etDescription, etRestaurantPhone, etAddress;
    private EditText etDeliveryFee, etMinOrder;
    private Button btnOpeningTime, btnClosingTime, btnSaveProfile;
    private Switch switchOpen;

    private String openingTime = "";
    private String closingTime = "";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        etRestaurantName = findViewById(R.id.etRestaurantName);
        etDescription = findViewById(R.id.etDescription);
        etRestaurantPhone = findViewById(R.id.etRestaurantPhone);
        etAddress = findViewById(R.id.etAddress);
        etDeliveryFee = findViewById(R.id.etDeliveryFee);
        etMinOrder = findViewById(R.id.etMinOrder);

        btnOpeningTime = findViewById(R.id.btnOpeningTime);
        btnClosingTime = findViewById(R.id.btnClosingTime);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        switchOpen = findViewById(R.id.switchOpen);

        btnOpeningTime.setOnClickListener(v -> pickTime(true));
        btnClosingTime.setOnClickListener(v -> pickTime(false));

        btnSaveProfile.setOnClickListener(v -> saveProfile());

        loadExistingProfile();
    }

    private void pickTime(boolean isOpeningTime) {

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {

                    Calendar time = Calendar.getInstance();
                    time.set(Calendar.HOUR_OF_DAY, selectedHour);
                    time.set(Calendar.MINUTE, selectedMinute);

                    String formatted = String.format(
                            Locale.getDefault(),
                            "%tI:%tM %tp",
                            time, time, time
                    );

                    if (isOpeningTime) {
                        openingTime = formatted;
                        btnOpeningTime.setText(formatted);
                    } else {
                        closingTime = formatted;
                        btnClosingTime.setText(formatted);
                    }
                },
                hour,
                minute,
                false
        );

        dialog.show();
    }

    private void saveProfile() {

        String name = etRestaurantName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String phone = etRestaurantPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String deliveryFeeStr = etDeliveryFee.getText().toString().trim();
        String minOrderStr = etMinOrder.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etRestaurantName.setError("Enter restaurant name");
            etRestaurantName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etRestaurantPhone.setError("Enter phone number");
            etRestaurantPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(address)) {
            etAddress.setError("Enter address");
            etAddress.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(openingTime) || TextUtils.isEmpty(closingTime)) {
            Toast.makeText(this, "Please set opening and closing time", Toast.LENGTH_SHORT).show();
            return;
        }

        double deliveryFee = 0;
        double minOrder = 0;

        try {
            if (!TextUtils.isEmpty(deliveryFeeStr)) {
                deliveryFee = Double.parseDouble(deliveryFeeStr);
            }
            if (!TextUtils.isEmpty(minOrderStr)) {
                minOrder = Double.parseDouble(minOrderStr);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter valid numbers for fee/min order", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSaveProfile.setEnabled(false);

        Map<String, Object> profile = new HashMap<>();
        profile.put("ownerId", uid);
        profile.put("name", name);
        profile.put("description", description);
        profile.put("phone", phone);
        profile.put("address", address);
        profile.put("openingTime", openingTime);
        profile.put("closingTime", closingTime);
        profile.put("deliveryFee", deliveryFee);
        profile.put("minOrder", minOrder);
        profile.put("isOpen", switchOpen.isChecked());

        db.collection("restaurants")
                .document(uid)
                .set(profile)
                .addOnSuccessListener(unused -> {
                    btnSaveProfile.setEnabled(true);
                    Toast.makeText(this, "Restaurant profile saved!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    btnSaveProfile.setEnabled(true);
                    Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void loadExistingProfile() {

        db.collection("restaurants")
                .document(uid)
                .get()
                .addOnSuccessListener(this::fillFormIfExists)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Could not load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void fillFormIfExists(DocumentSnapshot doc) {

        if (!doc.exists()) {
            return;
        }

        etRestaurantName.setText(doc.getString("name"));
        etDescription.setText(doc.getString("description"));
        etRestaurantPhone.setText(doc.getString("phone"));
        etAddress.setText(doc.getString("address"));

        openingTime = doc.getString("openingTime") != null ? doc.getString("openingTime") : "";
        closingTime = doc.getString("closingTime") != null ? doc.getString("closingTime") : "";

        if (!TextUtils.isEmpty(openingTime)) {
            btnOpeningTime.setText(openingTime);
        }

        if (!TextUtils.isEmpty(closingTime)) {
            btnClosingTime.setText(closingTime);
        }

        Double deliveryFee = doc.getDouble("deliveryFee");
        Double minOrder = doc.getDouble("minOrder");

        if (deliveryFee != null) {
            etDeliveryFee.setText(String.valueOf(deliveryFee));
        }

        if (minOrder != null) {
            etMinOrder.setText(String.valueOf(minOrder));
        }

        Boolean isOpen = doc.getBoolean("isOpen");
        if (isOpen != null) {
            switchOpen.setChecked(isOpen);
        }
    }
}