package com.hubu.fan.bean;

import android.provider.MediaStore;

/**
 * Created by FAN on 2016/9/1.
 */
public class MusicListBean {
    private String title;
    private String artist;
    private String album;
    private String year;
    private int duration;
    private String path;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String data) {
        this.path = data;
    }

    @Override
    public String toString() {
        return "MusicListBean{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", year='" + year + '\'' +
                ", duration=" + duration +
                ", path='" + path + '\'' +
                '}';
    }
}
