package com.curator.pos;

public class StaffTimesheet {
    private int id;
    private String username;
    private String date;
    private String punchInTime;
    private String punchOutTime;

    public StaffTimesheet(int id, String username, String date, String punchInTime, String punchOutTime) {
        this.id = id;
        this.username = username;
        this.date = date;
        this.punchInTime = punchInTime;
        this.punchOutTime = punchOutTime;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public String getPunchInTime() {
        return punchInTime;
    }

    public String getPunchOutTime() {
        return punchOutTime;
    }
}
