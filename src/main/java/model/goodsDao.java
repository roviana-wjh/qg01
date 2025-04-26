package model;

public class goodsDao {
    int id;
    String name;
    float price;
    int sellerId;
    String imageUrl;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public goodsDao(int id, String name, float price, int sellerId, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.sellerId = sellerId;
        this.imageUrl = imageUrl;
    }
}
