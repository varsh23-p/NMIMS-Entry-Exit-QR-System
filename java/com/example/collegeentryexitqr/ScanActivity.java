package com.example.collegeentryexitqr;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScanActivity extends AppCompatActivity {

    CardView btnSecurityLogin, btnLogin;

    TextView tvEnteredCount, tvExitedCount, tvInsideCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // LOGIN CARDS
        btnSecurityLogin = findViewById(R.id.btnSecurityLogin);
        btnLogin = findViewById(R.id.btnLogin);

        // SUMMARY TEXTS
        tvEnteredCount = findViewById(R.id.tvEnteredCount);
        tvExitedCount = findViewById(R.id.tvExitedCount);
        tvInsideCount = findViewById(R.id.tvInsideCount);

        // SECURITY LOGIN CLICK
        btnSecurityLogin.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ScanActivity.this,
                    SecurityLoginActivity.class
            );
            startActivity(intent);
        });

        // STUDENT LOGIN CLICK
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ScanActivity.this,
                    LoginActivity.class
            );
            startActivity(intent);
        });

        // LOAD SUMMARY
        loadTodaySummary();
    }

    // TODAY SUMMARY
    private void loadTodaySummary() {

        String todayDate = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());

        FirebaseDatabase.getInstance()
                .getReference("EntryExit")
                .get()
                .addOnSuccessListener(snapshot -> {

                    int entered = 0;
                    int exited = 0;

                    for (DataSnapshot studentSnap : snapshot.getChildren()) {

                        if (studentSnap.child(todayDate).exists()) {

                            if (studentSnap.child(todayDate)
                                    .child("entry_time").exists()) {
                                entered++;
                            }

                            if (studentSnap.child(todayDate)
                                    .child("exit_time").exists()) {
                                exited++;
                            }
                        }
                    }

                    tvEnteredCount.setText(String.valueOf(entered));
                    tvExitedCount.setText(String.valueOf(exited));
                    tvInsideCount.setText(String.valueOf(entered - exited));
                });
    }
}