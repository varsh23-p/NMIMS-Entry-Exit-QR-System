package com.example.collegeentryexitqr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SecurityDashboardActivity extends AppCompatActivity {

    // =========================
    // VARIABLES
    // =========================

    CardView btnScan, btnSearch, btnLogout, btnRegisterStudent;

    EditText etSapSearch;

    TextView tvHistory, tvSName, tvSBranch, tvSYear;
    TextView tvSStatus, tvResidence, tvDC;

    // =========================
    // ON CREATE
    // =========================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_dashboard);

        // =========================
        // LINK XML IDS
        // =========================

        btnScan = findViewById(R.id.btnScan);
        btnSearch = findViewById(R.id.btnSearch);
        btnLogout = findViewById(R.id.btnLogout);
        btnRegisterStudent = findViewById(R.id.btnRegisterStudent);

        etSapSearch = findViewById(R.id.etSapSearch);

        tvHistory = findViewById(R.id.tvHistory);

        tvSName = findViewById(R.id.tvSName);
        tvSBranch = findViewById(R.id.tvSBranch);
        tvSYear = findViewById(R.id.tvSYear);

        tvSStatus = findViewById(R.id.tvSStatus);
        tvResidence = findViewById(R.id.tvResidence);
        tvDC = findViewById(R.id.tvDC);

        // =========================
        // BUTTON ACTIONS
        // =========================

        btnScan.setOnClickListener(v -> startScan());

        btnSearch.setOnClickListener(v -> searchStudent());

        btnLogout.setOnClickListener(v -> logoutSecurity());

        btnRegisterStudent.setOnClickListener(v ->
                startActivity(new Intent(
                        SecurityDashboardActivity.this,
                        StudentRegistration.class
                )));
    }

    // =========================
    // START QR SCANNER
    // =========================

    private void startScan() {

        ScanOptions options = new ScanOptions();

        options.setDesiredBarcodeFormats(
                ScanOptions.ALL_CODE_TYPES);

        options.setPrompt("Scan Student ID Card");

        options.setBeepEnabled(true);

        options.setOrientationLocked(true);

        // VERTICAL SCANNER
        options.setCaptureActivity(
                PortraitCaptureActivity.class);

        barcodeLauncher.launch(options);
    }

    // =========================
    // QR RESULT
    // =========================

    ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(
                    new ScanContract(),
                    result -> {

                        if (result.getContents() != null) {

                            processScannedData(
                                    result.getContents());

                        } else {

                            Toast.makeText(
                                    this,
                                    "Scan Cancelled",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });

    // =========================
    // PROCESS QR DATA
    // =========================

    private void processScannedData(String rawScan) {

        String digitsOnly =
                rawScan.replaceAll("\\D", "");

        String sapId = null;

        for (int i = 0;
             i <= digitsOnly.length() - 11;
             i++) {

            String candidate =
                    digitsOnly.substring(i, i + 11);

            if (candidate.startsWith("7")) {

                sapId = candidate;
                break;
            }
        }

        if (sapId == null || sapId.length() != 11) {

            Toast.makeText(
                    this,
                    "Invalid SAP ID",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        etSapSearch.setText(sapId);

        saveEntryExit(sapId);

        loadStudentInfo(sapId);

        loadEntryExitStatusAndHistory(sapId);
    }

    // =========================
    // SAVE ENTRY EXIT
    // =========================

    private void saveEntryExit(String sapId) {

        String todayDate =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                ).format(new Date());

        String currentTime =
                new SimpleDateFormat(
                        "HH:mm:ss",
                        Locale.getDefault()
                ).format(new Date());

        FirebaseDatabase.getInstance()
                .getReference("EntryExit")
                .child(sapId)
                .child(todayDate)
                .get()

                .addOnSuccessListener(snapshot -> {

                    // ENTRY
                    if (!snapshot.exists()) {

                        FirebaseDatabase.getInstance()
                                .getReference("EntryExit")
                                .child(sapId)
                                .child(todayDate)
                                .child("entry_time")
                                .setValue(currentTime);

                        Toast.makeText(
                                this,
                                "ENTRY SUCCESSFUL ✅",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    // EXIT
                    else if (!snapshot.child(
                            "exit_time").exists()) {

                        FirebaseDatabase.getInstance()
                                .getReference("EntryExit")
                                .child(sapId)
                                .child(todayDate)
                                .child("exit_time")
                                .setValue(currentTime);

                        Toast.makeText(
                                this,
                                "EXIT SUCCESSFUL ✅",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    // ALREADY EXITED
                    else {

                        Toast.makeText(
                                this,
                                "Student Already Exited",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // =========================
    // SEARCH STUDENT
    // =========================

    private void searchStudent() {

        String sapId =
                etSapSearch.getText()
                        .toString()
                        .trim();

        if (sapId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Enter SAP ID",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        loadStudentInfo(sapId);

        loadEntryExitStatusAndHistory(sapId);
    }

    // =========================
    // LOAD STUDENT INFO
    // =========================

    private void loadStudentInfo(String sapId) {

        FirebaseDatabase.getInstance()
                .getReference("Students")
                .child(sapId)
                .get()

                .addOnSuccessListener(snapshot -> {

                    if (snapshot.exists()) {

                        tvSName.setText(
                                "👤 Name : " +
                                        safe(snapshot, "name"));

                        tvSBranch.setText(
                                "🏫 Branch : " +
                                        safe(snapshot, "branch"));

                        tvSYear.setText(
                                "📘 Year : " +
                                        safe(snapshot, "year"));

                        tvResidence.setText(
                                "🏠 Residence : " +
                                        safe(snapshot,
                                                "residenceType"));

                        String dcType =
                                safe(snapshot, "dcType");

                        String dcCount =
                                safe(snapshot, "dcCount");

                        tvDC.setText(
                                "🪪 DC : " +
                                        dcType +
                                        (dcType.equals("DC")
                                                ? " (" + dcCount + ")"
                                                : ""));

                    } else {

                        tvSName.setText(
                                "Student Not Registered");

                        tvSBranch.setText(
                                "🏫 Branch : ---");

                        tvSYear.setText(
                                "📘 Year : ---");

                        tvResidence.setText(
                                "🏠 Residence : ---");

                        tvDC.setText(
                                "🪪 DC : ---");

                        tvSStatus.setText(
                                "Status : ---");

                        tvHistory.setText(
                                "No History Found");
                    }
                });
    }

    // =========================
    // NULL SAFE METHOD
    // =========================

    private String safe(DataSnapshot snapshot,
                        String key) {

        String value =
                snapshot.child(key)
                        .getValue(String.class);

        return value != null ? value : "---";
    }

    // =========================
    // LOAD HISTORY
    // =========================

    private void loadEntryExitStatusAndHistory(
            String sapId) {

        String today =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                ).format(new Date());

        FirebaseDatabase.getInstance()
                .getReference("EntryExit")
                .child(sapId)
                .get()

                .addOnSuccessListener(snapshot -> {

                    StringBuilder history =
                            new StringBuilder();

                    boolean inside = false;

                    for (DataSnapshot dateSnap :
                            snapshot.getChildren()) {

                        String date =
                                dateSnap.getKey();

                        String entry =
                                dateSnap.child(
                                                "entry_time")
                                        .getValue(String.class);

                        String exit =
                                dateSnap.child(
                                                "exit_time")
                                        .getValue(String.class);

                        history.append("📅 ")
                                .append(date)
                                .append("\n");

                        history.append("Entry : ")
                                .append(entry != null
                                        ? entry
                                        : "N/A")
                                .append("\n");

                        history.append("Exit : ")
                                .append(exit != null
                                        ? exit
                                        : "N/A")
                                .append("\n\n");

                        if (date.equals(today)
                                && entry != null
                                && exit == null) {

                            inside = true;
                        }
                    }

                    if (history.length() == 0) {

                        tvHistory.setText(
                                "No History Found");

                    } else {

                        tvHistory.setText(
                                history.toString());
                    }

                    if (inside) {

                        tvSStatus.setText(
                                "✅ INSIDE CAMPUS");

                    } else {

                        tvSStatus.setText(
                                "❌ OUTSIDE CAMPUS");
                    }
                });
    }

    // =========================
    // LOGOUT
    // =========================

    private void logoutSecurity() {

        FirebaseAuth.getInstance().signOut();

        startActivity(new Intent(
                SecurityDashboardActivity.this,
                ScanActivity.class));

        finish();
    }
}