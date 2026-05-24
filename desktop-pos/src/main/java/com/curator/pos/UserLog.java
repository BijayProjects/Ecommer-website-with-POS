package com.curator.pos;

public class UserLog {
    private int id;
    private String username;
    private String action;
    private String timestamp;

    public UserLog(int id, String username, String action, String timestamp) {
        this.id = id;
        this.username = username;
        this.action = action;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getAction() { return action; }
    public String getTimestamp() { return timestamp; }
}
