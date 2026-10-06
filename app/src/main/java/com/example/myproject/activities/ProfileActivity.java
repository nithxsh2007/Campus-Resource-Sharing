package com.example.myproject.activities;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myproject.R;
import com.example.myproject.model.User;
import com.example.myproject.utils.FirebaseHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfilePhoto;
    private Button btnChangePhoto, btnSave;
    private TextInputEditText etName, etPhone;
    private ProgressBar pbLoading;

    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivProfilePhoto.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ivProfilePhoto = findViewById(R.id.ivEditProfilePhoto);
        btnChangePhoto = findViewById(R.id.btnChangeProfilePhoto);
        btnSave = findViewById(R.id.btnSaveProfile);
        etName = findViewById(R.id.etEditProfileName);
        etPhone = findViewById(R.id.etEditProfilePhone);
        pbLoading = findViewById(R.id.pbProfileLoading);

        loadProfileData();

        btnChangePhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnSave.setOnClickListener(v -> saveProfileChanges());
    }

    private void loadProfileData() {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid == null) return;

        pbLoading.setVisibility(View.VISIBLE);
        FirebaseHelper.getFirestore().collection(FirebaseHelper.USERS_COLLECTION)
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    pbLoading.setVisibility(View.GONE);
                    if (doc.exists()) {
                        User user = doc.toObject(User.class);
                        if (user != null) {
                            etName.setText(user.getFullName());
                            etPhone.setText(user.getPhone());
                            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                                Glide.with(this)
                                        .load(user.getProfileImageUrl())
                                        .placeholder(R.drawable.ic_person)
                                        .into(ivProfilePhoto);
                            }
                        }
                    }
                });
    }

    private void saveProfileChanges() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            etName.setError("Name cannot be empty");
            return;
        }

        String uid = FirebaseHelper.getCurrentUserId();
        if (uid == null) return;

        pbLoading.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        if (selectedImageUri != null) {
            StorageReference ref = FirebaseHelper.getStorage().getReference().child("profile_images/" + uid + ".jpg");
            ref.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        updateFirestoreProfile(uid, name, phone, uri.toString());
                    }))
                    .addOnFailureListener(e -> updateFirestoreProfile(uid, name, phone, null));
        } else {
            updateFirestoreProfile(uid, name, phone, null);
        }
    }

    private void updateFirestoreProfile(String uid, String name, String phone, String photoUrl) {
        var db = FirebaseHelper.getFirestore().collection(FirebaseHelper.USERS_COLLECTION).document(uid);
        if (photoUrl != null) {
            db.update("fullName", name, "phone", phone, "profileImageUrl", photoUrl);
        } else {
            db.update("fullName", name, "phone", phone);
        }

        db.get().addOnCompleteListener(task -> {
            pbLoading.setVisibility(View.GONE);
            btnSave.setEnabled(true);
            Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
