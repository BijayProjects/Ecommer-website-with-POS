package com.curator.pos;

public class Product {
    private int id;
    private String name;
    private double price;
    private int stock;
    private int categoryId;
    private String slug;
    private String description;
    private String image;
    private int isNewArrival;
    private int isTopSelling;
    private String shippingInfo;
    private String warrantyInfo;

    public Product(int id, String name, double price, int stock, int categoryId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
    }

    public Product(int id, String name, String slug, String description, double price, String image, int categoryId, int isNewArrival, int isTopSelling, int stock, String shippingInfo, String warrantyInfo) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.price = price;
        this.image = image;
        this.categoryId = categoryId;
        this.isNewArrival = isNewArrival;
        this.isTopSelling = isTopSelling;
        this.stock = stock;
        this.shippingInfo = shippingInfo;
        this.warrantyInfo = warrantyInfo;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public int getCategoryId() { return categoryId; }
    public void setStock(int stock) { this.stock = stock; }
    
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public String getImage() { return image; }
    public int getIsNewArrival() { return isNewArrival; }
    public int getIsTopSelling() { return isTopSelling; }
    public String getShippingInfo() { return shippingInfo; }
    public String getWarrantyInfo() { return warrantyInfo; }

    @Override
    public String toString() {
        return name;
    }
}
