package com.example.wasteawayapplication.Model;

public class Cart {

    private String pid, name, location, quantity;

    public Cart() {
//        required empty constructor
    }

    public Cart(String pid, String name, String quantity) {
        this.pid = pid;
        this.name = name;
        this.location = location;
        this.quantity = quantity;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
