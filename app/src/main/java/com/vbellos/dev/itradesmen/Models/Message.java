package com.vbellos.dev.itradesmen.Models;

public class Message {
    String message_id,sender,text,image;
    long time;
    boolean read;

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Message(){}
    public Message(String message_id, String sender, String text, String image, long time, boolean read) {
        this.message_id = message_id;
        this.sender = sender;
        this.text = text;
        this.image = image;
        this.time = time;
        this.read = read;
    }

}
