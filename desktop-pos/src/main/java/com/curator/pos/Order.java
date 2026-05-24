package com.curator.pos;

public class Order {
    private int id;
    private String customerName;
    private double totalAmount;
    private String status;
    private String createdAt;
    private String paymentMethod;
    // Extended fields for Website Orders tab
    private String email;
    private String phone;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private boolean paid;
    private String transactionId;

    // Original constructor (used by Sales Reports)
    public Order(int id, String customerName, double totalAmount, String status, String createdAt, String paymentMethod) {
        this.id = id;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.paymentMethod = paymentMethod;
    }

    // Full constructor (used by Website Orders tab)
    public Order(int id, String customerName, double totalAmount, String status, String createdAt, String paymentMethod,
                 String email, String phone, String address, String city, String postalCode, String country,
                 boolean paid, String transactionId) {
        this.id = id;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.paymentMethod = paymentMethod;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.paid = paid;
        this.transactionId = transactionId;
    }

    public int getId() { return id; }
    public String getCustomerName() { return customerName; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getEmail() { return email != null ? email : ""; }
    public String getPhone() { return phone != null ? phone : ""; }
    public String getAddress() { return address != null ? address : ""; }
    public String getCity() { return city != null ? city : ""; }
    public String getPostalCode() { return postalCode != null ? postalCode : ""; }
    public String getCountry() { return country != null ? country : ""; }
    public boolean isPaid() { return paid; }
    public String getPaid() { return paid ? "✅ PAID" : "❌ UNPAID"; }
    public String getTransactionId() { return transactionId != null ? transactionId : ""; }
    public void setStatus(String status) { this.status = status; }
    public void setPaid(boolean paid) { this.paid = paid; }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (address != null && !address.isEmpty()) sb.append(address);
        if (city != null && !city.isEmpty()) sb.append(", ").append(city);
        if (postalCode != null && !postalCode.isEmpty()) sb.append(" ").append(postalCode);
        if (country != null && !country.isEmpty()) sb.append(", ").append(country);
        return sb.toString();
    }

    /** Human-readable payment method name */
    public String getPaymentLabel() {
        if (paymentMethod == null) return "Unknown";
        switch (paymentMethod) {
            case "cod": return "Cash on Delivery";
            case "card": return "Credit/Debit Card";
            case "esewa": return "eSewa";
            case "paypal": return "PayPal";
            case "whatsapp": return "WhatsApp Inquiry";
            default: return paymentMethod;
        }
    }
}
