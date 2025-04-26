package model;

public class recievedTrade {
    int gid;
    int buyusername;
    String goodsname;
    float price;
    String comment;

    public recievedTrade(int gid, int buyusername, String goodsname, float price, String comment) {
        this.gid = gid;
        this.buyusername = buyusername;
        this.goodsname = goodsname;
        this.price = price;
        this.comment = comment;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public int getBuyusername() {
        return buyusername;
    }

    public void setBuyusername(int buyusername) {
        this.buyusername = buyusername;
    }

    public String getGoodsname() {
        return goodsname;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
