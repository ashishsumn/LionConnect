package edu.psu.lionconnect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ServerValue;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class postActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 0;
    private static postDataModel justAnInstance = postDataModel.getInstance();
    private FirebaseStorage fbsInstance;
//    private FirebaseDatabase dbInstance;
    private FirebaseFirestore fsInstance;
    ImageView im;
    EditText et;
    String user = "testUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        justAnInstance = postDataModel.getInstance();
        im = findViewById(R.id.image_post);
        im.setImageResource(R.drawable.image1_background);
        et = findViewById(R.id.text_post);
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        justAnInstance.setUser(user);
    }

    public void selectImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
//            Code for uploading image
            Bitmap imageBm;
            assert data != null;
            imageBm = getPath(data.getData());

            im.setImageBitmap(imageBm);
            justAnInstance.setImagePath(data.getData());
        }
    }

    private Bitmap getPath(Uri image){
        float imageViewHeight = (float) this.im.getHeight();
        float width = (float) ((View) this.im.getParent()).getWidth();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image);
            float scaler = imageViewHeight / ((float) bitmap.getHeight());
            if (scaler < 1.0f) {
                return Bitmap.createScaledBitmap(bitmap, (int) (((float) bitmap.getWidth()) * scaler), (int) (((float) bitmap.getHeight()) * scaler), true);
            }
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public void cancelClick(View view) {
        justAnInstance.clearInstance();
        Intent intent = new Intent(this, bottomNavActivity.class);
        startActivity(intent);
        finish();
    }

    public void postClick(View view) {
        if (postDataModel.getImagePath() == null) {
            Toast.makeText(getApplicationContext(), "Please select an image to post", Toast.LENGTH_SHORT).show();
            return;
        }
//        justAnInstance.setUser(user);
        final String temp = justAnInstance.getUser();
        Uri file = justAnInstance.getImagePath();
        justAnInstance.setDescription(et.getText().toString());
        FirebaseApp.initializeApp(getApplicationContext());

//        Get an instance of database and storage
        fbsInstance = FirebaseStorage.getInstance();
        fsInstance = FirebaseFirestore.getInstance();

//        Get a reference to the posts collection in database
        final DocumentReference postRef = fsInstance.collection("posts").document();
        final String postId = postRef.getId();

//        Get a reference to the images in posts in database
        DocumentReference photoRef = fsInstance.collection("posts/" + postId + "/photos").document();
        final String photoId = photoRef.getId();

//        Get reference for the image to be inserted on the storage
        final StorageReference userRef = fbsInstance.getReference().child("images/"+ user +"/"+photoId);
        UploadTask upTask = userRef.putFile(file);

        Toast.makeText(getApplicationContext(),"Posting",Toast.LENGTH_SHORT).show();
        findViewById(R.id.button_cancel).setVisibility(View.GONE);
        findViewById(R.id.button_post).setVisibility(View.GONE);
        findViewById(R.id.progress_circular).setVisibility(View.VISIBLE);

        upTask.addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Post Uploaded",Toast.LENGTH_SHORT).show();
                    findViewById(R.id.button_cancel).setVisibility(View.GONE);
                    findViewById(R.id.button_post).setVisibility(View.GONE);
                    findViewById(R.id.progress_circular).setVisibility(View.GONE);
                    findViewById(R.id.button_done).setVisibility(View.VISIBLE);
                    FieldValue serverTimestamp = FieldValue.serverTimestamp();
                    HashMap<String, Object> postHash = new HashMap<>();
                    HashMap<String, Object> childUpdates = new HashMap<>();
                    postHash.put("user", user);
                    postHash.put("photos", photoId);
                    postHash.put("description",justAnInstance.getDescription());
                    postHash.put("timestamp", serverTimestamp);
                    postHash.put("userId", user);
                    postHash.put("user", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

                    Map<String,Object> postUserHash = new HashMap<>();
                    postUserHash.put("posts."+postId, serverTimestamp);

                    fsInstance.collection("posts").document(postId).set(postHash);
                    fsInstance.collection("users").document(user).update(postUserHash);

//                    postRef.setValue(postHash);
                }else{
                    Toast.makeText(getApplicationContext(),"Unable to upload the post",Toast.LENGTH_SHORT).show();
                    findViewById(R.id.button_cancel).setVisibility(View.VISIBLE);
                    findViewById(R.id.button_post).setVisibility(View.VISIBLE);
                    findViewById(R.id.progress_circular).setVisibility(View.GONE);
                }

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                userRef.delete();
                Toast.makeText(getApplicationContext(),"Unable to upload the post",Toast.LENGTH_SHORT).show();
                findViewById(R.id.button_cancel).setVisibility(View.VISIBLE);
                findViewById(R.id.button_post).setVisibility(View.VISIBLE);
                findViewById(R.id.progress_circular).setVisibility(View.GONE);
                justAnInstance.clearInstance();
            }
        });
    }
}
