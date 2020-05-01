package edu.psu.lionconnect.ui.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.psu.lionconnect.R;
import edu.psu.lionconnect.ui.notifications.EditProfile;

public class ProfilePage extends AppCompatActivity {
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        button = (Button) findViewById(R.id.profile_edit_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileEditClick(v);
            }
        });
    }

    public void profileEditClick(View view) {
        Intent intent = new Intent(this, EditProfile.class);
        startActivity(intent);
    }
}
