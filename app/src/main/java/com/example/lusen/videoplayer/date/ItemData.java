package com.example.lusen.videoplayer.date;

/**
 * Created by lusen on 2017/5/20.
 */

public class ItemData {
    private String name;
    private String time;
    private String userName;
    private String userImg;
    private String mainImg;
    private String helfVideo;

    public String getHelfVideo() {
        return helfVideo;
    }

    public void setHelfVideo(String helfVideo) {
        this.helfVideo = helfVideo;
    }

    public String getName() {
        return name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }


    public String getMainImg() {
        return mainImg;

    }

    public void setMainImg(String mainImg) {
        this.mainImg = mainImg;
    }
}
