package com.example.fakeinstagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage, backArrow;
    private TextView usernameText;
    private Button editProfileButton;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.profile_image);
        backArrow = findViewById(R.id.back_arrow);
        usernameText = findViewById(R.id.username_text);
        editProfileButton = findViewById(R.id.edit_profile_button);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadUserData();

        backArrow.setOnClickListener(v -> finish());

        editProfileButton.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "Szerkesztés még nincs kész", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserData() {
        String uid = auth.getCurrentUser().getUid();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        usernameText.setText(username != null ? username : "Ismeretlen");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Hiba a profil betöltésekor", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1000);
        usernameText.startAnimation(fadeIn);
    }
}
