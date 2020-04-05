package edu.upenn.cis350.myapplication;

public class Item {
    private String name;
    private String category;
    private String avail;
    private String imgURL;

    public Item(String name, String category, String avail, String imgURL) {
        this.category = category;
        this.name = name;
        this.avail = avail;
        this.imgURL = imgURL;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvail() {
        return avail;
    }

    public void setAvail(String avail) {
        this.avail = avail;
    }
}