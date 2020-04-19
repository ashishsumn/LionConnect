package edu.psu.lionconnect.ui.home;

import android.graphics.Bitmap;

public class feedDataStructure {
    private Integer image_path;
    private String text;
    private String user;
    private Bitmap image;


    public feedDataStructure(Integer image, String txt, String user){
        image_path = image;
        text = txt;
        this.user = user;
    }


    public Integer getImage_path() {
        return image_path;
    }

    public String getText(){
        return text;
    }

    public String getUser(){
        return user;
    }


}