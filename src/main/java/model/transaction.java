package model;

public class transaction {
    private int id;
    private String name;
    float price;
    String buyer;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String type;
    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public float getPrice() {
        return price;
    }

    public transaction(int id, String name, float price,String type) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.type = type;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getType() {
        return name;
    }

    public void setType(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
