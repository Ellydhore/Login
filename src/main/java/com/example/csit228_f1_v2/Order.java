package com.example.csit228_f1_v2;

public class Order {
    private int orderId;
    private String variety;
    private int quantity;

    public Order(int orderId, String variety, int quantity) {
        this.orderId = orderId;
        this.variety = variety;
        this.quantity = quantity;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
