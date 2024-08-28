package model;

import java.io.Serializable;

public class Message implements Serializable {
    private int sender;
    private String reciver;
    private long time;
    private String message;
    public Message(int sender,String reciver,long time,String message){
        this.sender=sender;
        this.reciver=reciver;
        this.time=time;
        this.message=message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getSender() {
        return sender;
    }

    public String getReciver() {
        return reciver;
    }

    public long getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }
}
