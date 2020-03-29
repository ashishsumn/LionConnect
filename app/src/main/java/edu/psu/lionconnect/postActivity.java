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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class postActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 0;
    private static postDataModel justAnInstance = postDataModel.getInstance();
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
    }

    public void postClick(View view) {
        postDataModel instance = postDataModel.getInstance();
        instance.setUser(user);
        String temp = instance.getUser();
        Uri file = instance.getImagePath();
        FirebaseApp.initializeApp(getApplicationContext());
        String temp2 = file.getPath();
        int cut = temp2.lastIndexOf('/');
        if (cut != -1) {
            temp2 = temp2.substring(cut + 1);
        }
        FirebaseStorage fbsInstance = FirebaseStorage.getInstance();
        StorageReference userRef = fbsInstance.getReference().child("images/"+ temp+"/"+temp2);
        UploadTask upTask = userRef.putFile(file);

        upTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Unable to upload the post",Toast.LENGTH_SHORT).show();
                findViewById(R.id.button_cancel).setVisibility(View.VISIBLE);
                findViewById(R.id.button_post).setVisibility(View.VISIBLE);
                findViewById(R.id.progress_circular).setVisibility(View.GONE);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(),"Post Uploaded",Toast.LENGTH_SHORT).show();
                findViewById(R.id.button_cancel).setVisibility(View.GONE);
                findViewById(R.id.button_post).setVisibility(View.GONE);
                findViewById(R.id.progress_circular).setVisibility(View.GONE);
                findViewById(R.id.button_done).setVisibility(View.VISIBLE);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(),"Posting",Toast.LENGTH_SHORT).show();
                findViewById(R.id.button_cancel).setVisibility(View.GONE);
                findViewById(R.id.button_post).setVisibility(View.GONE);
                findViewById(R.id.progress_circular).setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
