package model;

import java.io.Serializable;

public class User implements Serializable {
    private int id;

    private String name;

    private String username;

    private String password;

    private long created_at;

    private long updated_at;

    private long accessed_at;

    public User(String name,String username,String password,long created_at) {
        this.name = name;
        this.username=username;
        this.password=password;
        this.created_at=created_at;
        this.id=-1;
        updated_at=created_at;
        accessed_at=created_at;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public long getCreated_at() {
        return created_at;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public long getAccessed_at() {
        return accessed_at;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }

    public void setAccessed_at(long accessed_at) {
        this.accessed_at = accessed_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
