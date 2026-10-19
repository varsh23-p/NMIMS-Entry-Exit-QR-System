package com.example.collegeentryexitqr;

public class StudentModel {

    public String id, name, phone, branch, year;
    public String residenceType, dcType, dcCount;
    public String photoBase64;

    public StudentModel() {
    }

    public StudentModel(String id,
                        String name,
                        String phone,
                        String branch,
                        String year,
                        String residenceType,
                        String dcType,
                        String dcCount,
                        String photoBase64) {

        this.id = id;
        this.name = name;
        this.phone = phone;
        this.branch = branch;
        this.year = year;
        this.residenceType = residenceType;
        this.dcType = dcType;
        this.dcCount = dcCount;
        this.photoBase64 = photoBase64;
    }
}