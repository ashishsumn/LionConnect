package edu.psu.lionconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import edu.psu.lionconnect.R;

public class CreateProfileActivity extends AppCompatActivity {

    private EditText mUserName;
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private FirebaseFirestore fsInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        fsInstance = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUserName = findViewById(R.id.uidET);
    }

    private void updateUserProfile(){
        final FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){

            //updating userName/Display name for the current user
            String name_to_update = mUserName.getText().toString();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name_to_update).build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Log.d(TAG, "User profile update failed.");
                            }
                            else{
                                String name = user.getDisplayName();
                                String email = user.getEmail();
                                String uid = user.getUid();
                                Uri photoUrl = user.getPhotoUrl();

                                createUserInFirebase(name, email, uid, photoUrl);
                                forwardToLogin();
                            }
                        }
                    });
        }
    }

    // Create User in firebase
    private void createUserInFirebase(String name, String email, String uid, Uri photo){
        final Map<String,Object> createUserhash = new HashMap();
        final Map<String,Object> createFriendshash = new HashMap();
        final String userId = uid;
        createFriendshash.put("testUser",true);
        createUserhash.put("UserName", name);
        createUserhash.put("email", email);
        createUserhash.put("friends", createFriendshash);
        Log.d("userId",userId);
        fsInstance.collection("users").document(userId).set(createUserhash).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CreateProfileActivity.this, "Account Created! You are good to go!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Proceed to Login
    private void forwardToLogin(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Respond to button clicks
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.create_button) {
            updateUserProfile();
        }
    }
}
