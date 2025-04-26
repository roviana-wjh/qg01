package model;

public class denouce {
    int denoucer;
    int denoucee;
    String status;
    String statement;

    public int getDenoucer() {
        return denoucer;
    }

    public void setDenoucer(int denoucer) {
        this.denoucer = denoucer;
    }

    public int getDenocee() {
        return denoucee;
    }

    public void setDenocee(int denocee) {
        this.denoucee = denocee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public denouce(int denoucer, int denocee, String status, String statement) {
        this.denoucer = denoucer;
        this.denoucee = denocee;
        this.status = status;
        this.statement = statement;
    }

}
