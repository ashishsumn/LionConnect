package edu.psu.lionconnect.ui.notifications;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import edu.psu.lionconnect.R;
import io.grpc.Context;

public class EditProfile extends AppCompatActivity {
    private static final String TAG = "TAG";
    int TAKE_IMAGE_CODE = 10001;
    ImageView profileImage;
    Button changeProfileImage;
    Button saveBtn;
    StorageReference storageReference;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser user;
    EditText profile_fullname;
    EditText profile_campus;
    EditText profile_city;
    EditText profile_about_me;
    EditText profile_degree;
    EditText profile_major;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        profileImage = findViewById(R.id.edit_profile_image_view);
        changeProfileImage = findViewById(R.id.change_profile_button);
        saveBtn = findViewById(R.id.edit_save_button);
        storageReference = FirebaseStorage.getInstance().getReference();

        Intent data = getIntent();

        String fullname = data.getStringExtra("name");
        String campus = data.getStringExtra("campus");
        String city = data.getStringExtra("city");
        String about_me = data.getStringExtra("about_me");
        String degree = data.getStringExtra("degree");
        String major = data.getStringExtra("major");

        profile_fullname= findViewById(R.id.et_name_edit_text);
        profile_campus= findViewById(R.id.et_campus_edit_text);
        profile_city= findViewById(R.id.et_city_edit_text);
        profile_about_me= findViewById(R.id.et_bio_edit_text);
        profile_degree= findViewById(R.id.et_degree_edit_text);
        profile_major= findViewById(R.id.et_major_edit_text);




        Log.d(TAG, "onCreate: "+ fullname + " " + campus + " " + city  + " " + about_me + " " + degree + " " + major);
        StorageReference profileRef = storageReference.child("users"+fAuth.getCurrentUser().getUid()+"profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(profileImage);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","Save button clicked -- OnCLickLisr");
                if(profile_fullname.getText().toString().isEmpty() ||
                        profile_campus.getText().toString().isEmpty() ||
                        profile_city.getText().toString().isEmpty() ||
                        profile_about_me.getText().toString().isEmpty() ||
                        profile_degree.getText().toString().isEmpty() ||
                        profile_major.getText().toString().isEmpty()){
                    Toast.makeText(EditProfile.this, "One or many fields are empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                DocumentReference docRef = fStore.collection("users").document(user.getUid());
                Map<String, Object> edited = new HashMap<>();
                Log.d("TAG","Mapcreate -- OnCLickLisr");
                edited.put("name", profile_fullname.getText().toString());
                edited.put("city", profile_city.getText().toString());
                edited.put("campus", profile_campus.getText().toString());
                edited.put("about_me", profile_about_me.getText().toString());
                edited.put("degree", profile_degree.getText().toString());
                edited.put("major", profile_major.getText().toString());
                docRef.set(edited).addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG","Go back to profile-- OnCLickLisr");
                        Toast.makeText(EditProfile.this, "Profile is updated on DB", Toast.LENGTH_SHORT).show();
                        //send user back to profile
                        startActivity(new Intent(getApplicationContext(), NotificationsFragment.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", e.toString());
                        Toast.makeText(EditProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        profile_fullname.setText(fullname);
        profile_campus.setText(campus);
        profile_city.setText(city);
        profile_about_me.setText(about_me);
        profile_degree.setText(degree);
        profile_major.setText(major);

        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
//                profileImage.setImageURI(imageUri);

                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageURI) {
        //Upload Image to Firebase storage
        final StorageReference fileRef = storageReference.child("users"+fAuth.getCurrentUser().getUid()+"profile.jpg");
        fileRef.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(EditProfile.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void returnToProfile(View view) {
        Fragment fragment =new NotificationsFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment).commit();
//        startActivity(new Intent(getApplicationContext(), NotificationsFragment.class));
    }

    public void saveEditPofile(View view) {
        Log.d("TAG","Save button clicked -- saveEditProfile");
    }


}
