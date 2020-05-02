package edu.psu.lionconnect.ui.home;

import android.graphics.Bitmap;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/* renamed from: edu.psu.lionconnect.ui.home.feedDataStructure */
public class feedDataStructure {
    private String description;
    private Bitmap image;
    private Integer image_path;
    private StorageReference photoId;
    private String photos;
    private String timeStamp;
    private String user;
    private String userId;

    public feedDataStructure(Integer image, String txt, String user) {
        this.image_path = image;
        this.description = txt;
        this.user = user;
    }

    public feedDataStructure(String photos, String description, String user, String userId,String timeStamp) {
        this.description = description;
        this.timeStamp = timeStamp;
        this.user = user;
        this.userId = userId;
        FirebaseStorage instance = FirebaseStorage.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append("images/");
        sb.append(this.userId);
        this.photoId = instance.getReference(sb.toString()).child(photos);
    }

    public Integer getImage_path() {
        return this.image_path;
    }

    public String getText() {
        return this.description;
    }

    public String getUser() {
        return this.user;
    }

    public String getTimeStamp() {
        return this.timeStamp;
    }

    public StorageReference getPhotoPath() {
        return this.photoId;
    }
}
