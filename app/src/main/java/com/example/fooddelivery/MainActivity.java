package com.example.fooddelivery;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;

    private Button btnLogin;
    private TextView tvRegister;
    private TextView tvForgotPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Firebase
        mAuth = FirebaseAuth.getInstance();

        // Find views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // LOGIN
        btnLogin.setOnClickListener(v -> login());

        // REGISTER
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // FORGOT PASSWORD
        tvForgotPassword.setOnClickListener(v -> {
            resetPassword();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // If already logged in, skip login screen
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            goToHome();
        }
    }

    private void login() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Check email
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter your email");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }

        // Check password
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter your password");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    btnLogin.setEnabled(true);

                    Toast.makeText(MainActivity.this,
                            "Login successful",
                            Toast.LENGTH_SHORT).show();

                    goToHome();
                })
                .addOnFailureListener(e -> {
                    btnLogin.setEnabled(true);

                    Toast.makeText(MainActivity.this,
                            "Login failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void resetPassword() {

        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter your email first, then tap 'Forgot password?'");
            etEmail.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(MainActivity.this,
                            "Password reset link sent to " + email,
                            Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this,
                            "Could not send reset link: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void goToHome() {

        String currentUid = mAuth.getCurrentUser().getUid();

        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUid)
                .get()
                .addOnSuccessListener(doc -> {

                    String role = doc.getString("role");

                    if ("Restaurant".equals(role)) {
                        startActivity(new Intent(MainActivity.this, RestaurantMainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Logged in as " + role + " (dashboard not built yet)",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this,
                                "Could not load user role: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }
}