package com.example.fakeinstagram;

import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CreatePostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int CAMERA_REQUEST_CODE = 2;
    private Button postButton;
    private Button selectImageButton;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);


        requestNotificationPermission();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "post_channel",
                    "Post Értesítések",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        postButton = findViewById(R.id.post_button);
        selectImageButton = findViewById(R.id.select_image_button);
        ImageButton backButton = findViewById(R.id.back_button);


        backButton.setOnClickListener(v -> finish());


        selectImageButton.setOnClickListener(v -> openGallery());


        postButton.setOnClickListener(v -> {
            if (imageUri != null) {
                StorageReference storageRef = FirebaseStorage.getInstance().getReference("posts")
                        .child(System.currentTimeMillis() + ".jpg");

                UploadTask uploadTask = storageRef.putFile(imageUri);

                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        long timestamp = System.currentTimeMillis();


                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String postId = db.collection("posts").document().getId();

                        Post post = new Post(postId, imageUrl, userId, timestamp);

                        db.collection("posts").document(postId)
                                .set(post)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Poszt létrehozva Firestore-ban!", Toast.LENGTH_SHORT).show();


                                    showNotification();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Hiba Firestore mentés közben!", Toast.LENGTH_SHORT).show();
                                });

                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Kép feltöltése sikertelen!", Toast.LENGTH_SHORT).show();
                });

            } else {
                Toast.makeText(this, "Kérlek válassz egy képet!", Toast.LENGTH_SHORT).show();
            }
        });
        Button openCameraButton = findViewById(R.id.open_camera_button);
        openCameraButton.setOnClickListener(v -> openCamera());

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Értesítési engedély megadva", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Értesítési engedély megtagadva", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null && extras.get("data") != null) {

                Toast.makeText(this, "Kép elkészült (bitmap)", Toast.LENGTH_SHORT).show();

            }
        }
    }
    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "post_channel")
                .setSmallIcon(R.drawable.ic_home)
                .setContentTitle("FakeInstagram")
                .setContentText("A poszt sikeresen létrejött!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }
    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "Üdv újra a posztkészítésnél!", Toast.LENGTH_SHORT).show();
    }
    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }


}
