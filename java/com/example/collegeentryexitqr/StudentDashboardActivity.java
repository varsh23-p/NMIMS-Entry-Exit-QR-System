package com.example.collegeentryexitqr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class StudentDashboardActivity extends AppCompatActivity {

    // ✅ TextViews
    TextView tvStudentName,
            tvStudentBranch,
            tvStudentYear,
            tvResidence,
            tvDC;

    TextView tvTotalDays,
            tvPresentDays,
            tvAbsentDays;

    // ✅ Image
    ImageView imgStudentPhoto;

    // ✅ Logout Button
    CardView btnLogout;

    // ✅ Student SAP ID
    String sapId;

    // ✅ Total Working Days
    private static final int TOTAL_WORKING_DAYS = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_dashboard);

        // ✅ Initialize Views

        tvStudentName =
                findViewById(R.id.tvStudentName);

        tvStudentBranch =
                findViewById(R.id.tvStudentBranch);

        tvStudentYear =
                findViewById(R.id.tvStudentYear);

        tvResidence =
                findViewById(R.id.tvResidence);

        tvDC =
                findViewById(R.id.tvDC);

        tvTotalDays =
                findViewById(R.id.tvTotalDays);

        tvPresentDays =
                findViewById(R.id.tvPresentDays);

        tvAbsentDays =
                findViewById(R.id.tvAbsentDays);

        imgStudentPhoto =
                findViewById(R.id.imgStudentPhoto);

        btnLogout =
                findViewById(R.id.btnLogout);

        // ✅ Get SAP ID from Login
        sapId = getIntent().getStringExtra("SAP_ID");

        // ✅ Validation
        if (sapId == null || sapId.isEmpty()) {

            Toast.makeText(
                    this,
                    "SAP ID Missing",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // ✅ Load Student Details
        loadStudentDetails();

        // ✅ Load Attendance
        calculateAttendance();

        // ✅ Logout
        btnLogout.setOnClickListener(v -> logoutStudent());
    }

    // =========================================================
    // ✅ LOAD STUDENT DETAILS
    // =========================================================

    private void loadStudentDetails() {

        FirebaseDatabase.getInstance()
                .getReference("Students")
                .child(sapId)
                .get()

                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.exists()) {

                        Toast.makeText(
                                this,
                                "Student Data Not Found",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    // ✅ Name
                    String name =
                            snapshot.child("name")
                                    .getValue(String.class);

                    tvStudentName.setText(
                            "Name : " +
                                    (name != null ? name : "---")
                    );

                    // ✅ Branch
                    String branch =
                            snapshot.child("branch")
                                    .getValue(String.class);

                    tvStudentBranch.setText(
                            "Branch : " +
                                    (branch != null ? branch : "---")
                    );

                    // ✅ Year
                    String year =
                            snapshot.child("year")
                                    .getValue(String.class);

                    tvStudentYear.setText(
                            "Year : " +
                                    (year != null ? year : "---")
                    );

                    // ✅ Residence
                    String residence =
                            snapshot.child("residenceType")
                                    .getValue(String.class);

                    tvResidence.setText(
                            "Residence : " +
                                    (residence != null ? residence : "---")
                    );

                    // ✅ DC
                    String dcType =
                            snapshot.child("dcType")
                                    .getValue(String.class);

                    String dcCount =
                            snapshot.child("dcCount")
                                    .getValue(String.class);

                    if (dcType != null &&
                            dcType.equals("DC")) {

                        tvDC.setText(
                                "DC : " +
                                        dcType +
                                        " (" +
                                        (dcCount != null ? dcCount : "0")
                                        + ")"
                        );

                    } else {

                        tvDC.setText(
                                "DC : Non-DC"
                        );
                    }

                    // ✅ Student Photo
                    String base64Image =
                            snapshot.child("photoBase64")
                                    .getValue(String.class);

                    if (base64Image != null &&
                            !base64Image.isEmpty()) {

                        try {

                            byte[] decodedBytes =
                                    Base64.decode(
                                            base64Image,
                                            Base64.DEFAULT
                                    );

                            Bitmap bitmap =
                                    BitmapFactory.decodeByteArray(
                                            decodedBytes,
                                            0,
                                            decodedBytes.length
                                    );

                            imgStudentPhoto.setImageBitmap(bitmap);

                        } catch (Exception e) {

                            e.printStackTrace();

                            Toast.makeText(
                                    this,
                                    "Image Load Failed",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Database Error : "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // =========================================================
    // ✅ CALCULATE ATTENDANCE
    // =========================================================

    private void calculateAttendance() {

        FirebaseDatabase.getInstance()
                .getReference("EntryExit")
                .child(sapId)
                .get()

                .addOnSuccessListener(snapshot -> {

                    int presentDays = 0;

                    if (snapshot.exists()) {

                        for (DataSnapshot dateSnap
                                : snapshot.getChildren()) {

                            // ✅ If entry exists count present
                            if (dateSnap.child("entry_time")
                                    .exists()) {

                                presentDays++;
                            }
                        }
                    }

                    // ✅ Absent
                    int absentDays =
                            TOTAL_WORKING_DAYS
                                    - presentDays;

                    if (absentDays < 0) {
                        absentDays = 0;
                    }

                    // ✅ Set Values
                    tvTotalDays.setText(
                            String.valueOf(TOTAL_WORKING_DAYS)
                    );

                    tvPresentDays.setText(
                            String.valueOf(presentDays)
                    );

                    tvAbsentDays.setText(
                            String.valueOf(absentDays)
                    );
                })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Attendance Load Failed",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    // =========================================================
    // ✅ LOGOUT
    // =========================================================

    private void logoutStudent() {

        Intent intent =
                new Intent(
                        StudentDashboardActivity.this,
                        LoginActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}