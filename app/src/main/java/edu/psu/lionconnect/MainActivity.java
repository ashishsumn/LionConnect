package edu.psu.lionconnect;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.psu.LionConnect.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    /* access modifiers changed from: private */
    public FirebaseAuth mAuth;
    /* access modifiers changed from: private */
    public EditText mEmailField;
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
//            forwardToHome();
//            Intent intent = new Intent(MainActivity.this, );
        }else if(checkbox.equals("false")){
            Toast.makeText(this, "Please sign in", Toast.LENGTH_SHORT).show();
        }
        this.remember.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String str = "remember";
                String str2 = "checkbox";
                if (buttonView.isChecked()) {
                    Editor editor = MainActivity.this.getSharedPreferences(str2, 0).edit();
                    editor.putString(str, "true");
                    editor.apply();
                    Toast.makeText(MainActivity.this, "Checked", Toast.LENGTH_SHORT).show();
                } else if (!buttonView.isChecked()) {
                    Editor editor2 = MainActivity.this.getSharedPreferences(str2, 0).edit();
                    editor2.putString(str, "false");
                    editor2.apply();
                    Toast.makeText(MainActivity.this, "Unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signIn(String email, String password) {
        StringBuilder sb = new StringBuilder();
        sb.append("signIn:");
        sb.append(email);
        Log.d(TAG, sb.toString());
        if (validateEmail() && validatePassword()) {
            this.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((Activity) this, new OnCompleteListener<AuthResult>() {
                public void onComplete(Task<AuthResult> task) {
                    boolean isSuccessful = task.isSuccessful();
                    String str = MainActivity.TAG;
                    if (isSuccessful) {
                        Log.d(str, "signInWithEmail:success");
                        FirebaseUser user = MainActivity.this.mAuth.getCurrentUser();
                        String displayName = user.getDisplayName();
                        if (!user.isEmailVerified()) {
                            Toast.makeText(MainActivity.this, "Please verify your email before logging-in", Toast.LENGTH_SHORT).show();
                        } else {
                            MainActivity.this.forwardToHome();
                        }
                    } else {
                        Log.w(str, "signInWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Wrong EmailId or Password.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean validateEmail() {
        boolean valid = true;
        String email = this.mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            this.mEmailField.setError("Required.");
            valid = false;
        } else {
            this.mEmailField.setError(null);
        }
        String str = "Please enter a valid PSU email";
        if (email.indexOf(64) == -1) {
            this.mEmailField.setError(str);
            return false;
        } else if (!email.split("@", 2)[1].equalsIgnoreCase("psu.edu")) {
            this.mEmailField.setError(str);
            return false;
        } else {
            this.mEmailField.setError(null);
            return valid;
        }
    }

    private boolean validatePassword() {
        if (TextUtils.isEmpty(this.mPasswordField.getText().toString())) {
            this.mPasswordField.setError("Required.");
            return false;
        }
        this.mPasswordField.setError(null);
        return true;
    }

    /* access modifiers changed from: private */
    public void forwardToHome() {
        startActivity(new Intent(this, bottomNavActivity.class));
    }

    private void pwdReset(String emailAddress) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {

            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(MainActivity.TAG, "Email sent.");
                    Toast.makeText(MainActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                    return;
                }
                MainActivity.this.mEmailField.setError("Please enter a valid PSU email");
            }
        });
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.login_button) {
            signIn(this.mEmailField.getText().toString(), this.mPasswordField.getText().toString());
        } else if (i == R.id.new_user_button) {
            startActivity(new Intent(this, RegistrationActivity.class));
        } else if (i == R.id.forgot_password_button && validateEmail()) {
            pwdReset(this.mEmailField.getText().toString());
        }
    }
}
