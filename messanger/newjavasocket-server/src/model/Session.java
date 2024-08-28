package model;

import java.util.ArrayList;

public class Session {

    private int id;

    private User user;

    private long start;

    private long end;

    private boolean status;

    public Session(User user,long start) {
        this.user=user;
        this.start=start;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public boolean isStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
