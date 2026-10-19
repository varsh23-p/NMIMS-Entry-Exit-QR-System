package com.example.collegeentryexitqr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class StudentRegistration extends AppCompatActivity {

    // EditTexts
    EditText etStudentId, etName, etPhone,
            etBranch, etDCCount;

    // Spinner
    Spinner spinnerYear;

    // Image
    ImageView imgStudentPhoto;

    // RadioGroups
    RadioGroup radioGroupResidence, radioGroupDC;

    // Image URI
    Uri imageUri;

    // Image Picker Launcher
    ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);

        // ✅ Initialize Views
        etStudentId = findViewById(R.id.etStudentId);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etBranch = findViewById(R.id.etBranch);
        etDCCount = findViewById(R.id.etDCCount);

        // ✅ Spinner
        spinnerYear = findViewById(R.id.spinnerYear);

        // ✅ Image
        imgStudentPhoto = findViewById(R.id.imgStudentPhoto);

        // ✅ Radio Groups
        radioGroupResidence = findViewById(R.id.radioGroupResidence);
        radioGroupDC = findViewById(R.id.radioGroupDC);

        // =========================================
        // ✅ YEAR SPINNER DATA
        // =========================================

        String[] years = {
                "Select Year",
                "FY",
                "SY",
                "TY",
                "Final"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        years
                );

        spinnerYear.setAdapter(adapter);

        // =========================================
        // ✅ HIDE DC COUNT INITIALLY
        // =========================================

        etDCCount.setVisibility(View.GONE);

        // =========================================
        // ✅ SHOW/HIDE DC COUNT
        // =========================================

        radioGroupDC.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.rbDC) {

                etDCCount.setVisibility(View.VISIBLE);

            } else {

                etDCCount.setVisibility(View.GONE);
                etDCCount.setText("");
            }
        });

        // =========================================
        // ✅ IMAGE PICKER
        // =========================================

        imagePickerLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {

                            if (result.getResultCode() == RESULT_OK
                                    && result.getData() != null) {

                                imageUri = result.getData().getData();

                                imgStudentPhoto.setImageURI(imageUri);
                            }
                        });

        // =========================================
        // ✅ OPEN GALLERY
        // =========================================

        imgStudentPhoto.setOnClickListener(v -> openGallery());

        // =========================================
        // ✅ REGISTER BUTTON
        // =========================================

        findViewById(R.id.btnRegister)
                .setOnClickListener(v -> registerStudent());
    }

    // ==================================================
    // ✅ OPEN GALLERY
    // ==================================================

    private void openGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");

        imagePickerLauncher.launch(intent);
    }

    // ==================================================
    // ✅ CONVERT IMAGE TO BASE64
    // ==================================================

    private String imageToBase64(Uri uri) {

        try {

            Bitmap bitmap = MediaStore.Images.Media
                    .getBitmap(this.getContentResolver(), uri);

            ByteArrayOutputStream baos =
                    new ByteArrayOutputStream();

            bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    60,
                    baos
            );

            byte[] imageBytes = baos.toByteArray();

            return android.util.Base64.encodeToString(
                    imageBytes,
                    android.util.Base64.DEFAULT
            );

        } catch (IOException e) {

            e.printStackTrace();

            return null;
        }
    }

    // ==================================================
    // ✅ REGISTER STUDENT
    // ==================================================

    private void registerStudent() {

        // =========================================
        // GET VALUES
        // =========================================

        String id =
                etStudentId.getText().toString().trim();

        String name =
                etName.getText().toString().trim();

        String phone =
                etPhone.getText().toString().trim();

        String branch =
                etBranch.getText().toString().trim();

        // ✅ GET YEAR FROM SPINNER
        String year =
                spinnerYear.getSelectedItem().toString();

        // =========================================
        // RESIDENCE TYPE
        // =========================================

        String residenceType = "";

        int resId =
                radioGroupResidence.getCheckedRadioButtonId();

        if (resId == R.id.rbHosteller) {

            residenceType = "Hosteller";

        } else if (resId == R.id.rbDayScholar) {

            residenceType = "Day Scholar";
        }

        // =========================================
        // DC TYPE
        // =========================================

        String dcType = "";

        int dcId =
                radioGroupDC.getCheckedRadioButtonId();

        if (dcId == R.id.rbDC) {

            dcType = "DC";

        } else if (dcId == R.id.rbNonDC) {

            dcType = "Non-DC";
        }

        String dcCount =
                etDCCount.getText().toString().trim();

        // =========================================
        // VALIDATION
        // =========================================

        if (id.isEmpty()
                || name.isEmpty()
                || phone.isEmpty()
                || branch.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // ✅ YEAR VALIDATION

        if (year.equals("Select Year")) {

            Toast.makeText(
                    this,
                    "Please Select Year",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // ✅ RESIDENCE VALIDATION

        if (residenceType.isEmpty()) {

            Toast.makeText(
                    this,
                    "Select Residence Type",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // ✅ DC VALIDATION

        if (dcType.isEmpty()) {

            Toast.makeText(
                    this,
                    "Select DC Type",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // ✅ DC COUNT VALIDATION

        if (dcType.equals("DC")
                && dcCount.isEmpty()) {

            Toast.makeText(
                    this,
                    "Enter DC Count",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // ✅ IMAGE VALIDATION

        if (imageUri == null) {

            Toast.makeText(
                    this,
                    "Upload Student Photo",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =========================================
        // CONVERT IMAGE
        // =========================================

        String base64Image =
                imageToBase64(imageUri);

        if (base64Image == null) {

            Toast.makeText(
                    this,
                    "Image Conversion Failed",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =========================================
        // CREATE OBJECT
        // =========================================

        StudentModel student =
                new StudentModel(
                        id,
                        name,
                        phone,
                        branch,
                        year,
                        residenceType,
                        dcType,
                        dcType.equals("DC")
                                ? dcCount
                                : "0",
                        base64Image
                );

        // =========================================
        // SAVE TO FIREBASE
        // =========================================

        FirebaseDatabase.getInstance()
                .getReference("Students")
                .child(id)
                .setValue(student)

                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Student Registered Successfully ✅",
                            Toast.LENGTH_LONG
                    ).show();

                    clearForm();
                })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed : " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // ==================================================
    // ✅ CLEAR FORM
    // ==================================================

    private void clearForm() {

        etStudentId.setText("");

        etName.setText("");

        etPhone.setText("");

        etBranch.setText("");

        etDCCount.setText("");

        // ✅ RESET SPINNER
        spinnerYear.setSelection(0);

        radioGroupResidence.clearCheck();

        radioGroupDC.clearCheck();

        etDCCount.setVisibility(View.GONE);

        imgStudentPhoto.setImageResource(R.drawable.stud);

        imageUri = null;
    }
}