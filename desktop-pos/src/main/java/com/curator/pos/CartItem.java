package com.curator.pos;

public class CartItem {
    private Product product;
    private int qty;

    public CartItem(Product p) {
        this.product = p;
        this.qty = 1;
    }

    public String getName() {
        return product.getName();
    }

    public int getQty() {
        return qty;
    }

    public double getTotalPrice() {
        return product.getPrice() * qty;
    }

    public Product getProduct() {
        return product;
    }

    public void incrementQty() {
        qty++;
    }
}
