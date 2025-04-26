package model;

public class pregoods {
    int gid;
    int username;
    float price;
    String picture;
    String goodsname;
    String type;

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public int getUsername() {
        return username;
    }

    public void setUsername(int username) {
        this.username = username;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getGoodsname() {
        return goodsname;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public pregoods(int gid, int username, float price, String picture, String goodsname, String type) {
        this.gid = gid;
        this.username = username;
        this.price = price;
        this.picture = picture;
        this.goodsname = goodsname;
        this.type = type;
    }
}
