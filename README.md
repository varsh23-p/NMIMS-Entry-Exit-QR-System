# NMIMS Entry Exit QR System

## 📌 Project Overview

The **NMIMS Entry Exit QR System** is an Android-based smart campus security application developed for managing student entry and exit records using QR/Barcode scanning technology.

The system allows:

* Security staff to scan student ID cards
* Automatic entry/exit tracking
* Student registration with image upload
* Student login using SAP ID and mobile number
* Attendance monitoring through Firebase Realtime Database

This project helps improve:

* Campus security
* Attendance management
* Real-time student tracking
* Digital record maintenance

---

## 🚀 Features

### 👮 Security Dashboard

* Scan Student ID Card (Vertical Scanner)
* Register New Students
* Search Student using SAP ID
* View Entry/Exit History
* Check Student Status (Inside/Outside Campus)
* Logout Functionality

### 🎓 Student Registration

* Upload Student Photo from Gallery
* Register using:

  * SAP ID
  * Student Name
  * Mobile Number
  * Branch
  * Year (FY/SY/TY/Final)
  * Residence Type
  * DC Information
* Data stored in Firebase Realtime Database

### 👨‍🎓 Student Login

* Login using:

  * SAP ID
  * Mobile Number (as Password)
* Firebase Authentication Validation

### 📊 Student Dashboard

* Display Student Profile
* Student Image Preview
* Branch & Academic Year
* Residence & DC Information
* Attendance Summary:

  * Total Days
  * Present Days
  * Absent Days

---

## 🛠️ Technologies Used

* **Android Studio**
* **Java**
* **XML**
* **Firebase Realtime Database**
* **Firebase Authentication**
* **ZXing Barcode Scanner**
* **Material UI Components**

---

## 🗂️ Project Modules

| Module               | Description                |
| -------------------- | -------------------------- |
| Security Module      | Scan and monitor students  |
| Registration Module  | Add new student data       |
| Student Login Module | Student authentication     |
| Attendance Module    | Track presence using scans |
| Firebase Integration | Store and retrieve data    |

---

## 📱 Screens Included

* Splash Screen
* Main Selection Screen
* Security Login
* Student Registration
* Security Dashboard
* Student Login
* Student Dashboard
* QR/Barcode Scanner

---

## 🔥 Firebase Database Structure

```plaintext
Students
   └── SAP_ID
        ├── name
        ├── phone
        ├── branch
        ├── year
        ├── residenceType
        ├── dcType
        ├── dcCount
        └── photoBase64

EntryExit
   └── SAP_ID
        └── DATE
             ├── entry_time
             └── exit_time
```

---

## 📷 QR / Barcode Functionality

* Student ID card is scanned using ZXing Scanner
* SAP ID extracted automatically
* Entry and Exit timestamps recorded
* Real-time attendance updates generated

---

## 🎯 Advantages

* Paperless attendance system
* Fast student verification
* Real-time monitoring
* Secure database storage
* Easy student tracking

---

## 📌 Future Enhancements

* Face Recognition Integration
* Cloud Storage for Images
* Push Notifications
* Admin Web Dashboard
* Monthly Attendance Reports
* Dark Mode Support

---

## 👨‍💻 Developed By

**Varsha Patil**

NMIMS University

---

## 📄 License

This project is developed for educational and academic purposes.
