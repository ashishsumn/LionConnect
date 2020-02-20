package edu.psu.lionconnect.ui.home;

public class feedDataStructure {
    private Integer image_path;
    private String text;

    public feedDataStructure(Integer image, String txt){
        image_path = image;
        text = txt;
    }


    public Integer getImage_path() {
        return image_path;
    }

    public String getText(){
        return text;
    }


}