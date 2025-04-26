package model;

public class user {
    private int username;
    private String password;
    private String status;
    private int score;
    private float money;

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUsername() {
        return username;
    }

    public void setUsername(int username) {
        this.username = username;
    }

    public user(int username, String password, String status, int score, float money) {
        this.username = username;
        this.score = score;
        this.password = password;
        this.status = status;
        this.money = money;

    }
}
