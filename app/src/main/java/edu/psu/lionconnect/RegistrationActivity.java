package edu.psu.lionconnect;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
//import com.google.firebase.auth.FirebaseAuthMultiFactorException;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.MultiFactorResolver;
////import com.google.firebase.quickstart.auth.R;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mEmailField = findViewById(R.id.emailET);
        mPasswordField = findViewById(R.id.pwdET);
        mUserName = findViewById(R.id.uidET);

        mAuth = FirebaseAuth.getInstance();
    }

    // Sign up
    public void createAccount(String email, String password){

        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                            updateUserProfile(user);
                            forwardToLogin();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

//                        // [START_EXCLUDE]
//                        hideProgressBar();
//                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Validate email id and password fields
    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    //Updating name and profile pic
    private void updateUserProfile(FirebaseUser user){
        Log.d(TAG, "User profile updated.");
        String name = mUserName.getText().toString();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name).build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
    }

    // Redirect to login page on successful sign up
    private void forwardToLogin(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Respond to button clicks
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.register_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.to_login_button) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}
