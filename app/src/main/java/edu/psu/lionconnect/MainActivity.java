package edu.psu.lionconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;
    private CheckBox remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmailField = findViewById(R.id.usernameET);
        mPasswordField = findViewById(R.id.pwdET);
        mAuth = FirebaseAuth.getInstance();
        remember= findViewById(R.id.chkRemember);
         
        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");
        if(checkbox.equals("true")){
            forwardToHome();
//            Intent intent = new Intent(MainActivity.this, );
        }else if(checkbox.equals("false")){
            Toast.makeText(this, "Please sign in", Toast.LENGTH_SHORT).show();
        }

        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                    Toast.makeText(MainActivity.this, "Checked", Toast.LENGTH_SHORT).show();
                }else if(!buttonView.isChecked()){
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                    Toast.makeText(MainActivity.this, "Unchecked", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    // Sign in
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateEmail() || !validatePassword()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String name = user.getDisplayName();

                            boolean emailVerified = user.isEmailVerified();
                                // if the user email is not verified
                                if(!emailVerified) {
                                    Toast.makeText(MainActivity.this,"Please verify your email before logging-in",
                                            Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    forwardToHome();
                                }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Wrong EmailId or Password.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Validate email id field
    private boolean validateEmail() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        if (email.indexOf('@') == -1) {
            mEmailField.setError("Please enter a valid PSU email");
            valid = false;
        } else {
            String[] email_split_on_at = email.split("@", 2);

            if (!email_split_on_at[1].equalsIgnoreCase("psu.edu")) {
                mEmailField.setError("Please enter a valid PSU email");
                valid = false;
            } else {
                mEmailField.setError(null);
            }
        }
        return valid;
    }

    // Validate password field
    private boolean validatePassword() {
        boolean valid = true;

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    // Redirect to dashboard on successful login
    private void forwardToHome(){
        Intent intent = new Intent(this, bottomNavActivity.class);
        startActivity(intent);
    }

    // Sending password reset email
    private void pwdReset(String emailAddress){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        //String emailAddress = "user@example.com";

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(MainActivity.this,"Password reset email sent",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            mEmailField.setError("Please enter a valid PSU email");
                        }
                    }
                });
    }

    // Respond to button clicks
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.login_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.new_user_button) {
            Intent intent = new Intent(this, RegistrationActivity.class);
            startActivity(intent);
        }
        else if (i == R.id.forgot_password_button){
            if (validateEmail()) {
                pwdReset(mEmailField.getText().toString());
            }
        }
    }
}
