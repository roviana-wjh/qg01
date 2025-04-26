package model;

public class goods {
    private int gid;
    private String name;
    private String description;
    private int username;

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    private float price;
    private String url;
    private String type;
    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public goods(int gid, String name, String description, int username, String url, String type,float price) {
        this.gid = gid;
        this.name = name;
        this.description = description;
        this.username = username;
        this.url = url;
        this.type = type;
        this.price = price;
    }

    public int getUsername() {
        return username;
    }

    public void setUsername(int username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }
}
