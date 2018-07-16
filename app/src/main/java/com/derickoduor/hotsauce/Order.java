package com.derickoduor.hotsauce;

/**
 * Created by Derick Oduor on 3/29/2018.
 */

public class Order {
    private int id,qty;
    private String name;
    private double price,discount;
    private int phone;
    private String address,status,orderId;

    public Order(int id, int qty, double price, double discount, String name) {
        this.id = id;
        this.qty = qty;
        this.name = name;
        this.price = price;
        this.discount = discount;
    }

    public Order(int id, int qty, String name, double price, double discount, int phone, String address, String status,String orderId) {
        this.id = id;
        this.qty = qty;
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.phone = phone;
        this.address = address;
        this.status = status;
        this.orderId=orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
