package edu.psu.lionconnect;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class postDataModel {

    private static postDataModel instance = null;
    private Uri imagePath;
    private String description;
    private String user;


    private postDataModel(){}

    public static postDataModel getInstance(){
        if(instance == null){
            instance = new postDataModel();
        }
        return instance;
    }

    public static void setImagePath(Uri input){
        instance.imagePath = input;
    }

    public static void setDescription(String input){
        instance.description = input;
    }

    public static void setUser(String input){
        instance.user = input;
    }

    public static Uri getImagePath(){
        return instance.imagePath;
    }

    public static String getDescription(){
        return instance.description;
    }

    public static String getUser() {
        return instance.user;
    }


}
