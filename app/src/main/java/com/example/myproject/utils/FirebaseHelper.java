package com.example.myproject.utils;

import com.example.myproject.model.NotificationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseHelper {

    public static final String USERS_COLLECTION = "users";
    public static final String RESOURCES_COLLECTION = "resources";
    public static final String BORROW_REQUESTS_COLLECTION = "borrowRequests";
    public static final String SAVED_RESOURCES_COLLECTION = "savedResources";
    public static final String REVIEWS_COLLECTION = "reviews";
    public static final String NOTIFICATIONS_COLLECTION = "notifications";
    public static final String REPORTS_COLLECTION = "reports";

    public static FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseFirestore getFirestore() {
        return FirebaseFirestore.getInstance();
    }

    public static FirebaseStorage getStorage() {
        return FirebaseStorage.getInstance();
    }

    public static String getCurrentUserId() {
        FirebaseUser user = getAuth().getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public static boolean isUserLoggedIn() {
        return getAuth().getCurrentUser() != null;
    }

    public static void sendNotification(String targetUserId, String title, String message, String type) {
        if (targetUserId == null || targetUserId.isEmpty()) return;

        FirebaseFirestore db = getFirestore();
        String notifId = db.collection(NOTIFICATIONS_COLLECTION).document().getId();
        NotificationModel notif = new NotificationModel(notifId, targetUserId, title, message, type);
        db.collection(NOTIFICATIONS_COLLECTION).document(notifId).set(notif);
    }
}
