# Campus Resource Sharing App

An easy-to-intermediate level Android mobile application developed in Java for B.Tech IT college projects.

---

## 📖 Description

The **Campus Resource Sharing App** enables college students to share, list, borrow, and return essential study resources, gadgets, and lab equipment on campus. It fosters a collaborative peer-to-peer sharing ecosystem within the college community.

---

## 🎯 Problem Statement & Objectives

### Problem Statement
College students frequently require reference books, calculators, lab coats, drawing kits, and electronics for short durations. Buying new equipment for temporary usage is expensive and inefficient.

### Objectives
1. Provide a central platform for college students to share idle resources.
2. Streamline the borrow-request and return cycle.
3. Keep track of item availability and return deadlines.
4. Promote peer interaction, sustainability, and trust through user ratings and reviews.

---

## ✨ Features

- **Authentication**: Secure Register, Login, Logout, and Forgot Password via Firebase Auth.
- **Resource Management**: Add, view, edit, search, filter, and delete resources with image uploads via Firebase Storage.
- **Borrowing System**: Send borrow requests with expected return dates, accept/reject requests, mark items as returned, and view borrowing history.
- **Saved Resources**: Heart/Bookmark resources to view later.
- **User Dashboard & Profile**: Profile photo upload, contact details, user rating display, and live dashboard stats.
- **In-App Notifications**: Real-time notifications for borrow requests, accept/reject updates, and return confirmations.
- **Ratings & Reviews**: Rate resource owners (1–5 stars) with comments upon returning items.
- **Report Resource**: Flag inappropriate or fake resources with specific reasons.
- **Dark Mode**: Simple Light/Dark theme toggle saved via SharedPreferences.

---

## 🛠️ Technology Stack

- **Platform**: Android SDK (Min SDK 24, Target SDK 35, Compile SDK 37)
- **Language**: Java 11
- **UI Toolkit**: Material Design Components & XML Layouts
- **Database**: Firebase Cloud Firestore
- **Authentication**: Firebase Authentication (Email/Password)
- **Storage**: Firebase Storage
- **Image Loading**: Glide 4.16.0
- **Architecture**: Classic Java Activities, Fragments, and RecyclerView Adapters

---

## 📂 Project Structure

```text
com.example.myproject
├── activities
│   ├── SplashActivity.java
│   ├── LoginActivity.java
│   ├── RegisterActivity.java
│   ├── ForgotPasswordActivity.java
│   ├── MainActivity.java
│   ├── AddResourceActivity.java
│   ├── EditResourceActivity.java
│   ├── ResourceDetailsActivity.java
│   ├── BorrowRequestActivity.java
│   ├── BorrowRequestsOwnerActivity.java
│   ├── BorrowedItemsActivity.java
│   ├── MyResourcesActivity.java
│   ├── SavedResourcesActivity.java
│   ├── ProfileActivity.java
│   ├── SettingsActivity.java
│   ├── BorrowingHistoryActivity.java
│   ├── NotificationsActivity.java
│   ├── ReportResourceActivity.java
│   └── RateOwnerActivity.java
│
├── fragments
│   ├── HomeFragment.java
│   ├── ExploreFragment.java
│   ├── SavedFragment.java
│   ├── ActivityFragment.java
│   └── ProfileFragment.java
│
├── adapter
│   ├── ResourceAdapter.java
│   ├── BorrowRequestAdapter.java
│   ├── SavedResourceAdapter.java
│   ├── NotificationAdapter.java
│   └── ReviewAdapter.java
│
├── model
│   ├── User.java
│   ├── Resource.java
│   ├── BorrowRequest.java
│   ├── SavedResource.java
│   ├── NotificationModel.java
│   ├── Report.java
│   └── Review.java
│
└── utils
    ├── FirebaseHelper.java
    └── ThemeUtils.java
```

---

## 🗄️ Firestore Collections

1. **`users`**: User profiles (`userId`, `fullName`, `email`, `phone`, `profileImageUrl`, `rating`, `ratingCount`, `createdAt`).
2. **`resources`**: Listed items (`resourceId`, `resourceName`, `category`, `description`, `condition`, `location`, `imageUrl`, `ownerId`, `ownerName`, `ownerPhone`, `availability`, `createdAt`, `updatedAt`).
3. **`borrowRequests`**: Transactions (`requestId`, `resourceId`, `resourceName`, `ownerId`, `borrowerId`, `borrowerName`, `requestDate`, `returnDate`, `status`, `note`, `timestamp`).
4. **`savedResources`**: Bookmarks (`savedId`, `userId`, `resourceId`, `savedAt`).
5. **`reviews`**: Ratings & feedback (`reviewId`, `resourceId`, `ownerId`, `reviewerId`, `reviewerName`, `rating`, `comment`, `createdAt`).
6. **`notifications`**: In-app alerts (`notificationId`, `userId`, `title`, `message`, `type`, `timestamp`, `read`).
7. **`reports`**: Resource flags (`reportId`, `resourceId`, `reportedBy`, `reason`, `description`, `createdAt`).

---

## ⚡ How to Set Up Firebase

1. Go to [Firebase Console](https://console.firebase.google.com/).
2. Create a new project called `CampusResourceSharingApp`.
3. Add an Android app with Package Name: `com.example.myproject`.
4. Download `google-services.json` and place it in the `app/` directory of the project.
5. Enable **Email/Password Authentication** in Firebase Console -> Authentication -> Sign-in method.
6. Create a **Cloud Firestore** database in test/production mode.
7. Create a **Firebase Storage** bucket for uploading images.
8. Apply the rules provided in `firestore.rules` and `storage.rules`.

---

## 🧪 Sample Test Data

To test the application during your demonstration, create 2 user accounts and add items like:

- **Java Programming Book** (Category: Books, Condition: Good)
- **Scientific Calculator** (Category: Calculators, Condition: Good)
- **USB-C Fast Charger** (Category: Electronics, Condition: Used)
- **Drawing Kit / Mini Drafter** (Category: Accessories, Condition: Good)
- **Arduino Uno Starter Kit** (Category: Lab Equipment, Condition: New)
- **Wireless Mouse** (Category: Electronics, Condition: Good)

---

## 🚀 How to Run

1. Open the project in **Android Studio**.
2. Sync Gradle files (`File -> Sync Project with Gradle Files`).
3. Select an emulator or connected physical Android device (API 24+).
4. Click **Run** (`Shift + F10`).

---

## 🔮 Future Enhancements

- Push Notifications via FCM.
- In-app direct chat messaging between borrower and owner.
- Geolocation / Map-based resource discovery.
