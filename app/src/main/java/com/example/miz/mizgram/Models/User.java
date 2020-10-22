package com.example.miz.mizgram.Models;

/**
 * Created by miz on 13/1/2019.
 */

public class User {

    private String id;
    private String username;
    private String imageURL;
    private String status;
    private String lastseen;
    private String typing;

    public String getTyping() {
        return typing;
    }

    public void setTyping(String typing) {
        this.typing = typing;
    }

    public String getLastseen() {
        return lastseen;
    }

    public void setLastseen(String lastseen) {
        this.lastseen = lastseen;
    }

    public User(String id, String username, String imageURL, String status, String lastseen, String typing) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
        this.lastseen = lastseen;
        this.typing = typing;
    }

    public User(){


    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
