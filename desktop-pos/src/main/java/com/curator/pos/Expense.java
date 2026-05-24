package com.curator.pos;

import javafx.beans.property.*;

public class Expense {
    private final IntegerProperty id;
    private final StringProperty title;
    private final DoubleProperty amount;
    private final StringProperty category;
    private final StringProperty date;
    private final StringProperty description;
    private final StringProperty billImage;
    private final StringProperty billNo;

    public Expense(int id, String title, double amount, String category, String date, String description, String billImage, String billNo) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.amount = new SimpleDoubleProperty(amount);
        this.category = new SimpleStringProperty(category);
        this.date = new SimpleStringProperty(date);
        this.description = new SimpleStringProperty(description);
        this.billImage = new SimpleStringProperty(billImage);
        this.billNo = new SimpleStringProperty(billNo);
    }

    public int getId() { return id.get(); }
    public String getTitle() { return title.get(); }
    public double getAmount() { return amount.get(); }
    public String getCategory() { return category.get(); }
    public String getDate() { return date.get(); }
    public String getDescription() { return description.get(); }
    public String getBillImage() { return billImage.get(); }
    public String getBillNo() { return billNo.get(); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public DoubleProperty amountProperty() { return amount; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty dateProperty() { return date; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty billImageProperty() { return billImage; }
    public StringProperty billNoProperty() { return billNo; }
}
