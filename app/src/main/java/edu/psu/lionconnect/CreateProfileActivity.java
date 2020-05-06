package edu.psu.lionconnect;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest.Builder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import edu.psu.lionconnect.ui.notifications.EditProfile;

public class CreateProfileActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    private FirebaseFirestore fsInstance;
    private FirebaseAuth mAuth;
    private EditText mUserName;
    private EditText mFullName;
    private EditText mAbout;

    ImageView profileImage;
    Button changeProfileImage;
    StorageReference storageReference;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_create_profile);
        this.fsInstance = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();

        this.mUserName = (EditText) findViewById(R.id.uidET);
        this.mFullName = (EditText) findViewById(R.id.nameET);
        this.mAbout = (EditText) findViewById(R.id.aboutET);

        profileImage = findViewById(R.id.create_profile_image_view);
        changeProfileImage = findViewById(R.id.image_button);
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });
        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
//                profileImage.setImageURI(imageUri);

                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageURI) {
        //Upload Image to Firebase storage
        final StorageReference fileRef = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(CreateProfileActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateProfileActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateUserProfile() {
        final FirebaseUser user = this.mAuth.getCurrentUser();
        if (user != null) {
            user.updateProfile(new Builder().setDisplayName(this.mUserName.getText().toString()).build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                public void onComplete(Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Log.d(CreateProfileActivity.TAG, "User profile update failed.");
                        return;
                    }
                    CreateProfileActivity.this.createUserInFirebase(user.getDisplayName(), user.getEmail(), user.getUid(), user.getPhotoUrl());
                    CreateProfileActivity.this.forwardToLogin();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void createUserInFirebase(String name, String email, String uid, Uri photo) {
        Map<String, Object> createUserhash = new HashMap<>();
        Map<String, Object> emptyHashMap = new HashMap<>();

        //"UserName", "about_me", "campus", "degree", "email", "follows", "followedBy", "major", "name", "posts", city
        Map<String, Object> createPostHash = new HashMap<>();
        createUserhash.put("UserName", name);
        createUserhash.put("about_me", mAbout.getText().toString());
        createUserhash.put("campus", null);
        createUserhash.put("degree", null);
        createUserhash.put("email", email);
        createUserhash.put("follows", emptyHashMap);
        createUserhash.put("followedBy", emptyHashMap);
        createUserhash.put("major", null);
        createUserhash.put("city", null);
        createUserhash.put("name", mFullName.getText().toString());
        createUserhash.put("posts", emptyHashMap);


        this.fsInstance.collection("users").document(uid).set(createUserhash).addOnCompleteListener(new OnCompleteListener<Void>() {
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CreateProfileActivity.this, "Account Created! You are good to go!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void forwardToLogin() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void onClick(View v) {
        if (v.getId() == R.id.create_button) {
            updateUserProfile();
        }
    }
}
