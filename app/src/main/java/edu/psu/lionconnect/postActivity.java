package edu.psu.lionconnect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
        im = findViewById(R.id.image_post);
        im.setImageResource(R.drawable.image1_background);
        et = findViewById(R.id.text_post);
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
            imageBm = getPath(data.getData());

            im.setImageBitmap(imageBm);
            justAnInstance.setImagePath(data.getData());
            justAnInstance.setDescription(et.getText().toString());
            justAnInstance.setUser(user);
        }else{
//            Code for reselection prompt
            int a ;
        }
    }

    private Bitmap getPath(Uri image){
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void cancelClick(View view) {
        justAnInstance.setDescription(null);
        justAnInstance.setImagePath(null);
        Intent intent = new Intent(this, bottomNavActivity.class);
        startActivity(intent);
        finish();
    }

    public void postClick(View view) {
        justAnInstance.setUser(user);
        final String temp = justAnInstance.getUser();
        Uri file = justAnInstance.getImagePath();
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
        StorageReference userRef = fbsInstance.getReference().child("images/"+ temp+"/"+photoId);
        UploadTask upTask = userRef.putFile(file);

        Toast.makeText(getApplicationContext(),"Posting",Toast.LENGTH_SHORT).show();
        findViewById(R.id.button_cancel).setVisibility(View.GONE);
        findViewById(R.id.button_post).setVisibility(View.GONE);
        findViewById(R.id.progress_circular).setVisibility(View.VISIBLE);

        upTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Post Uploaded",Toast.LENGTH_SHORT).show();
                    findViewById(R.id.button_cancel).setVisibility(View.GONE);
                    findViewById(R.id.button_post).setVisibility(View.GONE);
                    findViewById(R.id.progress_circular).setVisibility(View.GONE);
                    findViewById(R.id.button_done).setVisibility(View.VISIBLE);

                    HashMap<String, Object> postHash = new HashMap<>();
                    HashMap<String, Object> childUpdates = new HashMap<>();
                    postHash.put("photos", photoId);
                    postHash.put("description",justAnInstance.getDescription());
                    postHash.put("timestamp", ServerValue.TIMESTAMP);

                    Map<String,Object> postUserHash = new HashMap<>();
                    postUserHash.put("posts."+postId, true);

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
        });
    }
}
