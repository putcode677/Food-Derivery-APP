package com.example.fooddelivery;

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

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName;
    private EditText etRegisterEmail;
    private EditText etPhone;
    private EditText etRegisterPassword;
    private EditText etConfirmPassword;

    private Spinner spinnerRole;

    private Button btnCreateAccount;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

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

            // Selected item
            @Override
            public View getView(
                    int position,
                    View convertView,
                    ViewGroup parent) {

                View view = super.getView(
                        position,
                        convertView,
                        parent
                );

                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(Color.BLACK);
                    ((TextView) view).setTextSize(15);
                }

                return view;
            }

            // Dropdown items
            @Override
            public View getDropDownView(
                    int position,
                    View convertView,
                    ViewGroup parent) {

                View view = super.getDropDownView(
                        position,
                        convertView,
                        parent
                );

                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(Color.BLACK);
                    ((TextView) view).setTextSize(15);
                }

                return view;
            }
        };

        // Dropdown layout
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerRole.setAdapter(adapter);

        // Neumorphic popup background
        spinnerRole.setPopupBackgroundDrawable(
                getDrawable(R.drawable.bg_neumorphic_spinner)
        );
    }

    private void createAccount() {

        String fullName =
                etFullName.getText().toString().trim();

        String email =
                etRegisterEmail.getText().toString().trim();

        String phone =
                etPhone.getText().toString().trim();

        String password =
                etRegisterPassword.getText().toString().trim();

        String confirmPassword =
                etConfirmPassword.getText().toString().trim();

        String role =
                spinnerRole.getSelectedItem().toString();

        // Full Name
        if (TextUtils.isEmpty(fullName)) {

            etFullName.setError(
                    "Enter your full name"
            );

            etFullName.requestFocus();
            return;
        }

        // Email
        if (TextUtils.isEmpty(email)) {

            etRegisterEmail.setError(
                    "Enter your email"
            );

            etRegisterEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()) {

            etRegisterEmail.setError(
                    "Enter a valid email"
            );

            etRegisterEmail.requestFocus();
            return;
        }

        // Phone
        if (TextUtils.isEmpty(phone)) {

            etPhone.setError(
                    "Enter your phone number"
            );

            etPhone.requestFocus();
            return;
        }

        // Password
        if (TextUtils.isEmpty(password)) {

            etRegisterPassword.setError(
                    "Create a password"
            );

            etRegisterPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {

            etRegisterPassword.setError(
                    "Password must be at least 6 characters"
            );

            etRegisterPassword.requestFocus();
            return;
        }

        // Confirm Password
        if (TextUtils.isEmpty(confirmPassword)) {

            etConfirmPassword.setError(
                    "Confirm your password"
            );

            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {

            etConfirmPassword.setError(
                    "Passwords do not match"
            );

            etConfirmPassword.requestFocus();
            return;
        }

        // Temporary registration result
        Toast.makeText(
                RegisterActivity.this,
                "Account ready!\nRole: " + role,
                Toast.LENGTH_LONG
        ).show();
    }
}
