package com.example.collegeentryexitqr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;

public class SecurityLoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;

    CardView btnLogin;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_login);

        // XML Views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        // LOGIN BUTTON
        btnLogin = findViewById(R.id.btnLogin);

        // Firebase
        mAuth = FirebaseAuth.getInstance();

        // CLICK EVENT
        btnLogin.setOnClickListener(v -> loginSecurity());
    }

    private void loginSecurity() {

        String email = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // VALIDATION
        if (email.isEmpty() || password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter email and password",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // FIREBASE LOGIN
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                this,
                                "Security Login Successful ✅",
                                Toast.LENGTH_SHORT
                        ).show();

                        // OPEN DASHBOARD
                        Intent intent = new Intent(
                                SecurityLoginActivity.this,
                                SecurityDashboardActivity.class
                        );

                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(
                                this,
                                "Authentication Failed ❌",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}