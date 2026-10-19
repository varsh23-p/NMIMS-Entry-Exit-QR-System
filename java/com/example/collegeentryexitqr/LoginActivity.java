package com.example.collegeentryexitqr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    EditText etSapId, etPhone;

    CardView btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ✅ Initialize Views
        etSapId = findViewById(R.id.etSapId);
        etPhone = findViewById(R.id.etPhone);

        btnLogin = findViewById(R.id.btnLogin);

        // ✅ Login Click
        btnLogin.setOnClickListener(v -> loginStudent());
    }

    private void loginStudent() {

        String sapId =
                etSapId.getText().toString().trim();

        String phone =
                etPhone.getText().toString().trim();

        // ✅ Validation
        if (sapId.isEmpty()) {

            etSapId.setError("Enter SAP ID");
            return;
        }

        if (phone.isEmpty()) {

            etPhone.setError("Enter Mobile Number");
            return;
        }

        // ✅ Firebase Check
        FirebaseDatabase.getInstance()
                .getReference("Students")
                .child(sapId)
                .get()

                .addOnSuccessListener(snapshot -> {

                    if (snapshot.exists()) {

                        String savedPhone =
                                snapshot.child("phone")
                                        .getValue(String.class);

                        if (savedPhone == null) {

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Phone number missing in database",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        // ✅ Match Phone
                        if (phone.equals(savedPhone)) {

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Login Successful ✅",
                                    Toast.LENGTH_SHORT
                            ).show();

                            // ✅ Open Dashboard
                            Intent intent =
                                    new Intent(
                                            LoginActivity.this,
                                            StudentDashboardActivity.class
                                    );

                            intent.putExtra("SAP_ID", sapId);

                            startActivity(intent);

                            finish();

                        } else {

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Invalid Mobile Number ❌",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                    } else {

                        Toast.makeText(
                                LoginActivity.this,
                                "Student Not Registered ❌",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })

                .addOnFailureListener(e -> {

                    Log.e("FirebaseError", e.getMessage());

                    Toast.makeText(
                            LoginActivity.this,
                            "Database Error : " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}