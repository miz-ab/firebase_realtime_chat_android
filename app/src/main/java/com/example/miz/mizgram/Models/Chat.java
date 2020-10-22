package com.example.miz.mizgram.Models;

/**
 * Created by miz on 25/1/2019.
 */

public class Chat {

    private String sender;
    private String reciver;
    private String message;
    private String fileURL;
    private String filename;


    public Chat(String sender, String reciver, String message, String fileURL, String fileName) {
        this.sender = sender;
        this.reciver = reciver;
        this.message = message;
        this.fileURL = fileURL;
        this.filename = filename;
    }

    public Chat(){

    }

    public String getFileName() {
        return filename;
    }

    public void setFileName(String fileName) {
        this.filename = fileName;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciver() {
        return reciver;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
