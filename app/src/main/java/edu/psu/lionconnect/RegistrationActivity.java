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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mConfirmPasswordField;
    private FirebaseFirestore fsInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        fsInstance = FirebaseFirestore.getInstance();
        mEmailField = findViewById(R.id.emailET);
        mPasswordField = findViewById(R.id.pwdET);
        mConfirmPasswordField = findViewById(R.id.confPwdET);
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
                            if(user!=null){

                                //sending email verification mail
                                boolean emailVerified = user.isEmailVerified();
                                if(!emailVerified) {
                                    sendEmailVerification(user);
                                }
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                mPasswordField.setError("Weak password, password should be at least 6 characters");
                                mPasswordField.requestFocus();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                mEmailField.setError("Invalid Credentials");
                                mEmailField.requestFocus();
                            } catch(FirebaseAuthUserCollisionException e) {
                                mEmailField.setError("User already exists, please login");
                                mEmailField.requestFocus();
                            } catch(Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }
                });
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

        if(email.indexOf('@')==-1){
            mEmailField.setError("Please enter a valid PSU email");
            valid = false;
        } else {
            String[] email_split_on_at = email.split("@",2);

            if(!email_split_on_at[1].equalsIgnoreCase("psu.edu")){
                mEmailField.setError("Please enter a valid PSU email");
                valid = false;
            } else {
                mEmailField.setError(null);
            }
        }

        String password = mPasswordField.getText().toString();
        String confirm_password = mConfirmPasswordField.getText().toString();

        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        if (TextUtils.isEmpty(confirm_password)) {
            mConfirmPasswordField.setError("Required.");
            valid = false;
        } else {
            mConfirmPasswordField.setError(null);
        }

        if (!password.equals(confirm_password)) {
            mPasswordField.setError("Passwords do not match");
            mConfirmPasswordField.setError("Passwords do not match");
            valid = false;
        } else {
            mPasswordField.setError(null);
            mConfirmPasswordField.setError(null);
        }
        return valid;
    }

    // sending verification mail
    public void sendEmailVerification(FirebaseUser user) {

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(RegistrationActivity.this,"Verification email sent",
                                    Toast.LENGTH_SHORT).show();
                            forwardToProfileCreation();
                        }
                        else{
                            mEmailField.setError("Please enter a valid PSU email");
                        }
                    }
                });
    }

    // Redirect to login page on successful sign up
    private void forwardToProfileCreation(){
        Intent intent = new Intent(this, CreateProfileActivity.class);
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
