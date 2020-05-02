package edu.psu.lionconnect.ui.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import edu.psu.lionconnect.R;
import edu.psu.lionconnect.ui.notifications.EditProfile;

public class ProfilePage extends AppCompatActivity {
    private Button button;
    private TextView email;
    private FirebaseStorage fbsInstance;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;
    private ImageView profileImage;
    private StorageReference storageReference;


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

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        profileImage = findViewById(R.id.profile_page_image);
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users"+fAuth.getCurrentUser().getUid()+"profile.jpg");



        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void profileEditClick(View view) {
        Intent intent = new Intent(this, EditProfile.class);
        startActivity(intent);
    }



}
