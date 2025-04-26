package model;

public class trade {
   private int sellusername;
   private int buyusername;
   private int gid;
   private float price;
   private String status;
   private String goodsname;
   private String type;

    public trade(int sellusername, int buyuername, int gid, float price, String status, String goodsname, String type) {
        this.sellusername = sellusername;
        this.buyusername = buyuername;
        this.gid = gid;
        this.price = price;
        this.status = status;
        this.goodsname = goodsname;
        this.type = type;
    }

    public int getSellusername() {
        return sellusername;
    }

    public void setSellusername(int sellusername) {
        this.sellusername = sellusername;
    }

    public int getBuyuername() {
        return buyusername;
    }

    public void setBuyuername(int buyuername) {
        this.buyusername = buyuername;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}

