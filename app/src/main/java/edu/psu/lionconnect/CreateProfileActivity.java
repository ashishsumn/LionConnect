package edu.psu.lionconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest.Builder;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class CreateProfileActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    private FirebaseFirestore fsInstance;
    private FirebaseAuth mAuth;
    private EditText mUserName;
    private EditText mFullName;
    private EditText mAbout;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_create_profile);
        this.fsInstance = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();

        this.mUserName = (EditText) findViewById(R.id.uidET);
        this.mFullName = (EditText) findViewById(R.id.nameET);
        this.mAbout = (EditText) findViewById(R.id.aboutET);
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
