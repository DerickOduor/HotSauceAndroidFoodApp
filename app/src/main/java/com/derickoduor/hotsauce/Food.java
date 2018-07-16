package com.derickoduor.hotsauce;

/**
 * Created by Derick Oduor on 3/27/2018.
 */

public class Food {

    private int id,menuId,qty;
    private String name,description,image;
    private double price,discount;

    public Food() {
    }

    public Food(int id, int menuId, String name, String description, String image, double price, double discount) {
        this.id = id;
        this.menuId = menuId;
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
        this.discount = discount;
    }

    public Food(int id, int menuId, int qty, String name, String description, String image, double price, double discount) {
        this.id = id;
        this.menuId = menuId;
        this.qty = qty;
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
        this.discount = discount;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    @Override
    public String toString(){
        return this.getName()+" "+this.getPrice()+" "+this.getDescription()+" "+this.getImage();
    }
}

