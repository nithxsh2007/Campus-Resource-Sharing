package com.example.myproject.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myproject.R;
import com.example.myproject.model.Review;
import com.example.myproject.model.User;
import com.example.myproject.utils.FirebaseHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;

public class RateOwnerActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private TextInputEditText etComment;
    private Button btnSubmit;

    private String ownerId, resourceId, reviewerName = "Student";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_owner);

        ownerId = getIntent().getStringExtra("ownerId");
        resourceId = getIntent().getStringExtra("resourceId");

        ratingBar = findViewById(R.id.rbRateOwner);
        etComment = findViewById(R.id.etRateComment);
        btnSubmit = findViewById(R.id.btnSubmitRating);

        loadReviewerName();

        btnSubmit.setOnClickListener(v -> submitReview());
    }

    private void loadReviewerName() {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid != null) {
            FirebaseHelper.getFirestore().collection(FirebaseHelper.USERS_COLLECTION)
                    .document(uid).get().addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            User u = doc.toObject(User.class);
                            if (u != null) reviewerName = u.getFullName();
                        }
                    });
        }
    }

    private void submitReview() {
        float rating = ratingBar.getRating();
        String comment = etComment.getText() != null ? etComment.getText().toString().trim() : "";

        String reviewerId = FirebaseHelper.getCurrentUserId();
        if (reviewerId == null || ownerId == null) return;

        btnSubmit.setEnabled(false);

        String reviewId = FirebaseHelper.getFirestore().collection(FirebaseHelper.REVIEWS_COLLECTION).document().getId();
        Review review = new Review(reviewId, resourceId, ownerId, reviewerId, reviewerName, rating, comment);

        FirebaseHelper.getFirestore().collection(FirebaseHelper.REVIEWS_COLLECTION)
                .document(reviewId)
                .set(review)
                .addOnSuccessListener(aVoid -> {
                    updateOwnerRating(ownerId);
                    Toast.makeText(RateOwnerActivity.this, "Thank you for your rating!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSubmit.setEnabled(true);
                    Toast.makeText(RateOwnerActivity.this, "Failed to submit rating: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateOwnerRating(String ownerId) {
        FirebaseHelper.getFirestore().collection(FirebaseHelper.REVIEWS_COLLECTION)
                .whereEqualTo("ownerId", ownerId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) return;

                    double total = 0;
                    int count = querySnapshot.size();

                    for (DocumentSnapshot doc : querySnapshot) {
                        Double r = doc.getDouble("rating");
                        if (r != null) total += r;
                    }

                    double avg = total / count;

                    FirebaseHelper.getFirestore().collection(FirebaseHelper.USERS_COLLECTION)
                            .document(ownerId)
                            .update("rating", avg, "ratingCount", count);
                });
    }
}
