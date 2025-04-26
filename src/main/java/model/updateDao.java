package model;

public class updateDao {
    public int getSellerusername() {
        return sellerusername;
    }

    public void setSellerusername(int sellerusername) {
        this.sellerusername = sellerusername;
    }

    public int getBuyusername() {
        return buyusername;
    }

    public void setBuyusername(int buyusername) {
        this.buyusername = buyusername;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public updateDao(int sellerusername, int buyusername, int gid, String status,float price) {
        this.sellerusername = sellerusername;
        this.buyusername = buyusername;
        this.gid = gid;
        this.status = status;
        this.price = price;
    }

    int sellerusername;
    int buyusername;
    int gid;
    String status;
    float price;

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}