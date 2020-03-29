package edu.psu.lionconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

//    final public FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void buttonClick(View button) {
        Intent intent = new Intent(this, RegisterationActivity.class);
        startActivity(intent);
    }

    public void loginClick(View view) {
        Intent intent = new Intent(this, bottomNavActivity.class);
        startActivity(intent);
    }
}
