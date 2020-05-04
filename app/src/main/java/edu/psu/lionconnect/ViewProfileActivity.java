package edu.psu.lionconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import edu.psu.lionconnect.R;

public class ViewProfileActivity extends AppCompatActivity {

    private String target_UID;
    private FirebaseFirestore fsInstance;
    private TextView main_fullname, main_campus, main_city, main_about_me, main_degree, main_major;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        target_UID = getIntent().getStringExtra("targetUID");

        main_fullname = findViewById(R.id.tv_profile_name);
        main_campus = findViewById(R.id.tv_profile_campus);
        main_city = findViewById(R.id.tv_profile_city);
        main_about_me = findViewById(R.id.tv_profile_bio);
        main_degree = findViewById(R.id.tv_profile_degree);
        main_major = findViewById(R.id.tv_profile_major);

        displayProfile();

    }

    private void displayProfile(){
        final String targetUID_local_copy = target_UID;

        fsInstance = FirebaseFirestore.getInstance();

        final DocumentReference target_user_ref = fsInstance.collection("users").document(targetUID_local_copy);

        target_user_ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> target_details = task.getResult().getData();

                    //"UserName", "about_me", "campus", "degree", "email", "follows", "followedBy", "major", "name", "posts", city
                    main_fullname.setText(target_details.get("name").toString());
                    if(target_details.get("campus")!=null)
                        main_campus.setText(target_details.get("campus").toString());
                    if(target_details.get("city")!=null)
                        main_city.setText(target_details.get("city").toString());
                    main_about_me.setText(target_details.get("about_me").toString());
                    if(target_details.get("degree")!=null)
                        main_degree.setText(target_details.get("degree").toString());
                    if(target_details.get("major")!=null)
                        main_major.setText(target_details.get("major").toString());
                }
            }
        });
    }
}
