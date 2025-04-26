package model;

public class userdao {
    private int username;
    private int points;

    public userdao(int username, int points) {
        this.username = username;
        this.points = points;
    }

    public int getUsername() {
        return username;
    }

    public void setUsername(int username) {
        this.username = username;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
