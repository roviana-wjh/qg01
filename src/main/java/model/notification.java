package model;

public class notification {
    private int username;
    private String message;
    private String time;
    public notification(int username, String message, int gid, String time) {
        this.username = username;
        this.message = message;
        this.gid = gid;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUsername() {
        return username;
    }

    public void setUsername(int username) {
        this.username = username;
    }

    private  int gid;
}
