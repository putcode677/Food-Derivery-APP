package com.example.fooddelivery;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName;
    private EditText etRegisterEmail;
    private EditText etPhone;
    private EditText etRegisterPassword;
    private EditText etConfirmPassword;

    private Spinner spinnerRole;

    private Button btnCreateAccount;
    private TextView tvBackToLogin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inputs
        etFullName = findViewById(R.id.etFullName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etPhone = findViewById(R.id.etPhone);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        // Role
        spinnerRole = findViewById(R.id.spinnerRole);

        // Buttons
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // Setup role spinner
        setupRoleSpinner();

        // Create account
        btnCreateAccount.setOnClickListener(v -> createAccount());

        // Back to login
        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void setupRoleSpinner() {

        String[] roles = {
                "Customer",
                "Restaurant",
                "Delivery Rider"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                roles
        ) {

            @Override
            public View getView(
                    int position,
                    View convertView,
                    ViewGroup parent) {

                View view = super.getView(position, convertView, parent);

                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(Color.BLACK);
                    ((TextView) view).setTextSize(15);
                }

                return view;
            }

            @Override
            public View getDropDownView(
                    int position,
                    View convertView,
                    ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);

                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(Color.BLACK);
                    ((TextView) view).setTextSize(15);
                }

                return view;
            }
        };

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerRole.setAdapter(adapter);

        spinnerRole.setPopupBackgroundDrawable(
                getDrawable(R.drawable.bg_neumorphic_spinner)
        );
    }

    private void createAccount() {

        String fullName = etFullName.getText().toString().trim();
        String email = etRegisterEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Enter your full name");
            etFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etRegisterEmail.setError("Enter your email");
            etRegisterEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etRegisterEmail.setError("Enter a valid email");
            etRegisterEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Enter your phone number");
            etPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etRegisterPassword.setError("Create a password");
            etRegisterPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etRegisterPassword.setError("Password must be at least 6 characters");
            etRegisterPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Confirm your password");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        btnCreateAccount.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user == null) {
                        btnCreateAccount.setEnabled(true);
                        Toast.makeText(RegisterActivity.this,
                                "Something went wrong. Try again.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String uid = user.getUid();

                    Map<String, Object> userProfile = new HashMap<>();
                    userProfile.put("fullName", fullName);
                    userProfile.put("email", email);
                    userProfile.put("phone", phone);
                    userProfile.put("role", role);
                    userProfile.put("uid", uid);

                    db.collection("users")
                            .document(uid)
                            .set(userProfile)
                            .addOnSuccessListener(unused -> {

                                btnCreateAccount.setEnabled(true);

                                Toast.makeText(RegisterActivity.this,
                                        "Account created successfully!",
                                        Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                btnCreateAccount.setEnabled(true);
                                Toast.makeText(RegisterActivity.this,
                                        "Account created, but saving profile failed: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    btnCreateAccount.setEnabled(true);
                    Toast.makeText(RegisterActivity.this,
                            "Registration failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}