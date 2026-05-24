package com.curator.pos;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class DashboardController {

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colStock;

    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> cartColName;
    @FXML private TableColumn<CartItem, Integer> cartColQty;
    @FXML private TableColumn<CartItem, Double> cartColPrice;
    
    @FXML private ListView<String> orderListView;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;
    @FXML private ComboBox<String> paymentMethodBox;
    @FXML private ListView<String> categoryListView;
    @FXML private TextField searchField;
    
    // Multi-page UI elements
    @FXML private VBox inventoryPage;
    @FXML private HBox checkoutPage;
    @FXML private TableView<CartItem> cartTableCheckout;
    @FXML private TableColumn<CartItem, String> cartColNameCheckout;
    @FXML private TableColumn<CartItem, Integer> cartColQtyCheckout;
    @FXML private TableColumn<CartItem, Double> cartColPriceCheckout;
    @FXML private ListView<String> paymentSidebarList;
    @FXML private Label subtotalLabelCheckout;
    @FXML private Label taxLabelCheckout;
    @FXML private Label totalLabelCheckout;
    @FXML private Label totalLabelSidebar;

    // Reporting & Expense Elements
    @FXML private TableView<Order> salesReportTable;
    @FXML private TableColumn<Order, Integer> repColId;
    @FXML private TableColumn<Order, String> repColDate;
    @FXML private TableColumn<Order, String> repColCustomer;
    @FXML private TableColumn<Order, String> repColMethod;
    @FXML private TableColumn<Order, Double> repColAmount;
    @FXML private TableColumn<Order, String> repColStatus;
    @FXML private Label reportTotalRevenue;
    @FXML private Label reportTotalOrders;
    @FXML private Label reportTotalProducts;
    @FXML private Label reportNetProfit;

    @FXML private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense, Integer> expColId;
    @FXML private TableColumn<Expense, String> expColDate;
    @FXML private TableColumn<Expense, String> expColTitle;
    @FXML private TableColumn<Expense, String> expColCat;
    @FXML private TableColumn<Expense, Double> expColAmount;
    @FXML private TextField expTitleField;
    @FXML private TextField expAmountField;
    @FXML private ComboBox<String> expCategoryBox;
    @FXML private TextArea expDescField;
    @FXML private Label totalExpensesLabel;
    @FXML private DatePicker expDatePicker;
    @FXML private TextField expBillField;
    @FXML private DatePicker reportDatePicker;
    @FXML private CheckBox showAllHistoryCheck;
    @FXML private TextField expBillNoField;
    @FXML private DatePicker expFilterDatePicker;
    @FXML private CheckBox expShowAllCheck;
    @FXML private TableColumn<Expense, String> expColBillNo;
    
    // Product Manager Tab Elements
    @FXML private TableView<Product> managerProductTable;
    @FXML private TableColumn<Product, String> mColName;
    @FXML private TableColumn<Product, Double> mColPrice;
    @FXML private TableColumn<Product, Integer> mColStock;
    @FXML private TableColumn<Product, String> mColCategory;
    @FXML private TextField managerSearchField;
    @FXML private Label userNameLabel;

    // Website Orders Tab Elements
    @FXML private TableView<Order> webOrderTable;
    @FXML private TableColumn<Order, Integer> webColId;
    @FXML private TableColumn<Order, String> webColDate;
    @FXML private TableColumn<Order, String> webColCustomer;
    @FXML private TableColumn<Order, String> webColEmail;
    @FXML private TableColumn<Order, String> webColMethod;
    @FXML private TableColumn<Order, String> webColPaid;
    @FXML private TableColumn<Order, Double> webColAmount;
    @FXML private TableColumn<Order, String> webColStatus;
    @FXML private ComboBox<String> webOrderStatusFilter;
    @FXML private ComboBox<String> webOrderPaymentFilter;
    @FXML private TextField webOrderSearchField;
    @FXML private ComboBox<String> webStatusUpdateBox;
    @FXML private Label webTotalOrdersLabel;
    @FXML private Label webPendingLabel;
    @FXML private Label webPaidCountLabel;
    @FXML private Label webRevenueLabel;
    @FXML private Label webDetailOrderId;
    @FXML private Label webDetailCustomerName;
    @FXML private Label webDetailEmail;
    @FXML private Label webDetailPhone;
    @FXML private Label webDetailAddress;
    @FXML private Label webDetailPayment;
    @FXML private Label webDetailPaidStatus;
    @FXML private Label webDetailTransId;
    @FXML private Label webDetailAmount;
    @FXML private ListView<String> webDetailItemsList;
    @FXML private VBox webOrderDetailPanel;

    // WhatsApp Orders Tab Elements
    @FXML private TableView<Order> waOrderTable;
    @FXML private TableColumn<Order, Integer> waColId;
    @FXML private TableColumn<Order, String> waColDate;
    @FXML private TableColumn<Order, String> waColCustomer;
    @FXML private TableColumn<Order, String> waColEmail;
    @FXML private TableColumn<Order, Double> waColAmount;
    @FXML private TableColumn<Order, String> waColStatus;
    @FXML private TextField waSearchField;
    @FXML private ComboBox<String> waStatusUpdateBox;
    @FXML private Label waTotalOrdersLabel;
    @FXML private Label waPendingLabel;
    @FXML private Label waRevenueLabel;
    @FXML private Label waDetailOrderId;
    @FXML private Label waDetailCustomerName;
    @FXML private Label waDetailEmail;
    @FXML private Label waDetailPhone;
    @FXML private Label waDetailTotal;
    @FXML private ListView<String> waDetailItemsList;
    @FXML private VBox waOrderDetailPanel;

    @FXML private TabPane mainTabPane;
    @FXML private Tab usersLogsTab;

    // User Management Tab Elements
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> colUserId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colUserRole;
    @FXML private TextField userUsernameField;
    @FXML private PasswordField userPasswordField;
    @FXML private ComboBox<String> userRoleBox;

    @FXML private DatePicker logDatePicker;
    @FXML private TableView<StaffTimesheet> userLogTable;
    @FXML private TableColumn<StaffTimesheet, Integer> colLogId;
    @FXML private TableColumn<StaffTimesheet, String> colLogUser;
    @FXML private TableColumn<StaffTimesheet, String> colLogDate;
    @FXML private TableColumn<StaffTimesheet, String> colLogPunchIn;
    @FXML private TableColumn<StaffTimesheet, String> colLogPunchOut;
    @FXML private TableColumn<StaffTimesheet, Void> colLogAction;

    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private ObservableList<Order> salesList = FXCollections.observableArrayList();
    private ObservableList<Order> webOrderList = FXCollections.observableArrayList();
    private ObservableList<Order> whatsappOrderList = FXCollections.observableArrayList();
    private ObservableList<Expense> expenseList = FXCollections.observableArrayList();
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private ObservableList<StaffTimesheet> userLogList = FXCollections.observableArrayList();
    private FilteredList<Product> filteredProducts;
    private FilteredList<Order> filteredWebOrders;
    private FilteredList<Order> filteredWhatsAppOrders;
    private Map<Integer, CartItem> cartMap = new LinkedHashMap<>();
    private Map<String, Integer> catIdMap = new HashMap<>();
    private Product selectedProduct = null;
    private Order selectedWebOrder = null;
    private Order selectedWhatsAppOrder = null;
    private double currentTotal = 0.0;
    private double totalExpenseAmount = 0.0;
    private double totalRevenueAmount = 0.0;

    // Printer Management
    private static PrintService selectedPrinterService = null;
    private static String selectedPrinterName = null;

    @FXML
    public void initialize() {
        loadPrinterConfig();
        DatabaseManager.initDatabase();
        if (userNameLabel != null && SessionManager.isLoggedIn()) {
            userNameLabel.setText("Logged in as: " + SessionManager.getLoggedInUser());
        }
        
        if (mainTabPane != null && usersLogsTab != null && !SessionManager.isAdmin()) {
            mainTabPane.getTabs().remove(usersLogsTab);
        }

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        formatColumnAsCurrency(colPrice);
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedProduct = newSelection;
            }
        });

        if (cartTable != null) {
            cartColName.setCellValueFactory(new PropertyValueFactory<>("name"));
            cartColQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
            cartColPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
            formatColumnAsCurrency(cartColPrice);
        }
        
        if (userTable != null) {
            colUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
            colUserRole.setCellValueFactory(new PropertyValueFactory<>("role"));
            userTable.setItems(userList);
            
            userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                if (newSel != null) {
                    userUsernameField.setText(newSel.getUsername());
                    userRoleBox.getSelectionModel().select(newSel.getRole());
                }
            });
            
            userRoleBox.setItems(FXCollections.observableArrayList("Admin", "Staff"));
            userRoleBox.getSelectionModel().selectFirst();
            
            loadUsers();
        }

        if (userLogTable != null) {
            colLogId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colLogUser.setCellValueFactory(new PropertyValueFactory<>("username"));
            colLogDate.setCellValueFactory(new PropertyValueFactory<>("date"));
            colLogPunchIn.setCellValueFactory(new PropertyValueFactory<>("punchInTime"));
            colLogPunchOut.setCellValueFactory(new PropertyValueFactory<>("punchOutTime"));

            colLogAction.setCellFactory(param -> new javafx.scene.control.TableCell<StaffTimesheet, Void>() {
                private final Button btn = new Button("Punch Out");
                {
                    btn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");
                    btn.setOnAction(event -> {
                        StaffTimesheet ts = getTableView().getItems().get(getIndex());
                        punchOutTimesheet(ts.getId(), ts.getUsername());
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        StaffTimesheet ts = getTableView().getItems().get(getIndex());
                        if (ts.getPunchOutTime() == null || ts.getPunchOutTime().isEmpty()) {
                            setGraphic(btn);
                        } else {
                            setGraphic(null);
                        }
                    }
                }
            });

            userLogTable.setItems(userLogList);
            
            if (logDatePicker != null) {
                logDatePicker.setValue(java.time.LocalDate.now());
            }
            loadUserLogs();
        }

        
        if (cartTableCheckout != null) {
            cartColNameCheckout.setCellValueFactory(new PropertyValueFactory<>("name"));
            cartColQtyCheckout.setCellValueFactory(new PropertyValueFactory<>("qty"));
            cartColPriceCheckout.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
            formatColumnAsCurrency(cartColPriceCheckout);
        }

        if (paymentSidebarList != null) {
            paymentSidebarList.setItems(FXCollections.observableArrayList("Cash", "Credit Card", "eSewa", "PayPal"));
            paymentSidebarList.setCellFactory(lv -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                        setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-padding: 10; -fx-background-color: transparent;");
                        
                        // Add icons based on the word
                        String icon = "";
                        if (item.equals("Cash")) icon = "💵  ";
                        else if (item.equals("Credit Card")) icon = "💳  ";
                        else if (item.equals("eSewa")) icon = "📱  ";
                        else if (item.equals("PayPal")) icon = "🅿️  ";
                        
                        setText(icon + item);
                        if (isSelected()) {
                            setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-padding: 10; -fx-background-color: #3b82f6; -fx-background-radius: 5;");
                        } else {
                            setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-padding: 10; -fx-background-color: transparent;");
                        }
                    }
                }
            });
            paymentSidebarList.getSelectionModel().selectFirst();
        }

        // Initialize Reports Table
        if (salesReportTable != null) {
            repColId.setCellValueFactory(new PropertyValueFactory<>("id"));
            repColDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            repColCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            repColMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
            repColAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
            formatColumnAsCurrency(repColAmount);
            repColStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            salesReportTable.setItems(salesList);
        }

        // Initialize Expenses Table
        if (expenseTable != null) {
            expColId.setCellValueFactory(new PropertyValueFactory<>("id"));
            expColDate.setCellValueFactory(new PropertyValueFactory<>("date"));
            expColTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            expColCat.setCellValueFactory(new PropertyValueFactory<>("category"));
            expColAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
            formatColumnAsCurrency(expColAmount);
            expColBillNo.setCellValueFactory(new PropertyValueFactory<>("billNo"));
            expenseTable.setItems(expenseList);
        }

        if (expCategoryBox != null) {
            expCategoryBox.setItems(FXCollections.observableArrayList("Inventory", "Rent", "Utilities", "Salary", "Marketing", "Other"));
            expCategoryBox.getSelectionModel().selectFirst();
        }

        if (expDatePicker != null) {
            expDatePicker.setValue(java.time.LocalDate.now());
        }

        if (reportDatePicker != null) {
            reportDatePicker.setValue(java.time.LocalDate.now());
            reportDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> refreshData());
        }

        if (showAllHistoryCheck != null) {
            showAllHistoryCheck.selectedProperty().addListener((obs, oldVal, newVal) -> refreshData());
        }

        if (expFilterDatePicker != null) {
            expFilterDatePicker.setValue(java.time.LocalDate.now());
            expFilterDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> loadExpenses());
        }

        if (expShowAllCheck != null) {
            expShowAllCheck.selectedProperty().addListener((obs, oldVal, newVal) -> loadExpenses());
        }

        filteredProducts = new FilteredList<>(productList, p -> true);
        productTable.setItems(filteredProducts);

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterProducts());
        }
        if (categoryListView != null) {
            categoryListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> filterProducts());
        }

        if (managerSearchField != null) {
            managerSearchField.textProperty().addListener((obs, oldVal, newVal) -> filterProducts());
        }

        loadCategories();
        loadProducts();
        loadOrders();
        loadExpenses();
        loadFullSalesReport();
        
        // Initialize Product Manager Table
        if (managerProductTable != null) {
            mColName.setCellValueFactory(new PropertyValueFactory<>("name"));
            mColPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
            formatColumnAsCurrency(mColPrice);
            mColStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
            mColCategory.setCellValueFactory(cellData -> {
                int catId = cellData.getValue().getCategoryId();
                String catName = "Unknown";
                for (Map.Entry<String, Integer> entry : catIdMap.entrySet()) {
                    if (entry.getValue() == catId) {
                        catName = entry.getKey();
                        break;
                    }
                }
                return new javafx.beans.property.SimpleStringProperty(catName);
            });
            managerProductTable.setItems(filteredProducts);
        }

        // Initialize Website Orders Tab
        if (webOrderTable != null) {
            webColId.setCellValueFactory(new PropertyValueFactory<>("id"));
            webColDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            webColCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            webColEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            webColMethod.setCellValueFactory(new PropertyValueFactory<>("paymentLabel"));
            webColPaid.setCellValueFactory(new PropertyValueFactory<>("paid"));
            webColAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
            formatColumnAsCurrency(webColAmount);
            webColStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            filteredWebOrders = new FilteredList<>(webOrderList, o -> true);
            webOrderTable.setItems(filteredWebOrders);

            // Selection listener -> populate detail sidebar
            webOrderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                selectedWebOrder = newSel;
                populateWebOrderDetail(newSel);
            });
            
            // Hide detail panel by default
            if (webOrderDetailPanel != null) {
                webOrderDetailPanel.setVisible(false);
                webOrderDetailPanel.setManaged(false);
            }
        }

        // Setup filter combos for website orders
        if (webOrderStatusFilter != null) {
            webOrderStatusFilter.setItems(FXCollections.observableArrayList("All Status", "pending", "processing", "shipped", "delivered", "cancelled"));
            webOrderStatusFilter.getSelectionModel().selectFirst();
            webOrderStatusFilter.valueProperty().addListener((obs, o, n) -> applyWebOrderFilters());
        }
        if (webOrderPaymentFilter != null) {
            webOrderPaymentFilter.setItems(FXCollections.observableArrayList("All Payments", "cod", "esewa", "paypal", "card"));
            webOrderPaymentFilter.getSelectionModel().selectFirst();
            webOrderPaymentFilter.valueProperty().addListener((obs, o, n) -> applyWebOrderFilters());
        }
        if (webOrderSearchField != null) {
            webOrderSearchField.textProperty().addListener((obs, o, n) -> applyWebOrderFilters());
        }
        if (webStatusUpdateBox != null) {
            webStatusUpdateBox.setItems(FXCollections.observableArrayList("pending", "processing", "shipped", "delivered", "cancelled"));
        }

        // Initialize WhatsApp Orders Tab
        if (waOrderTable != null) {
            waColId.setCellValueFactory(new PropertyValueFactory<>("id"));
            waColDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            waColCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            waColEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            waColAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
            formatColumnAsCurrency(waColAmount);
            waColStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            filteredWhatsAppOrders = new FilteredList<>(whatsappOrderList, o -> true);
            waOrderTable.setItems(filteredWhatsAppOrders);

            waOrderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                selectedWhatsAppOrder = newSel;
                populateWhatsAppOrderDetail(newSel);
            });

            if (waOrderDetailPanel != null) {
                waOrderDetailPanel.setVisible(false);
                waOrderDetailPanel.setManaged(false);
            }
        }

        if (waSearchField != null) {
            waSearchField.textProperty().addListener((obs, o, n) -> applyWhatsAppFilters());
        }
        if (waStatusUpdateBox != null) {
            waStatusUpdateBox.setItems(FXCollections.observableArrayList("pending", "contacted", "processing", "sold", "cancelled"));
        }

        loadWebsiteOrders();
        loadWhatsAppOrders();

        // Setup Auto-Refresh (Every 10 seconds)
        Timeline autoRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            refreshData();
        }));
        autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimeline.play();
    }

    private void filterProducts() {
        String search = searchField != null && searchField.getText() != null ? searchField.getText().toLowerCase() : "";
        String mSearch = managerSearchField != null && managerSearchField.getText() != null ? managerSearchField.getText().toLowerCase() : "";
        String cat = categoryListView != null ? categoryListView.getSelectionModel().getSelectedItem() : null;
        
        filteredProducts.setPredicate(p -> {
            String combinedSearch = search + mSearch;
            boolean matchesSearch = combinedSearch.isEmpty() || p.getName().toLowerCase().contains(search) || p.getName().toLowerCase().contains(mSearch);
            boolean matchesCat = cat == null || cat.equals("All Categories") || 
                                 (catIdMap.containsKey(cat) && p.getCategoryId() == catIdMap.get(cat));
            return matchesSearch && matchesCat;
        });
    }

    private void loadCategories() {
        if (categoryListView == null) return;
        ObservableList<String> cats = FXCollections.observableArrayList("All Categories");
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM shop_category")) {
            while (rs.next()) {
                cats.add(rs.getString("name"));
                catIdMap.put(rs.getString("name"), rs.getInt("id"));
            }
            categoryListView.setItems(cats);
            categoryListView.getSelectionModel().selectFirst();
        } catch (SQLException e) {}
    }

    private void loadProducts() {
        productList.clear();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM shop_product")) {
            
            while (rs.next()) {
                productList.add(new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("slug"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getString("image"),
                    rs.getInt("category_id"),
                    rs.getInt("is_new_arrival"),
                    rs.getInt("is_top_selling"),
                    rs.getInt("stock"),
                    rs.getString("shipping_info"),
                    rs.getString("warranty_info")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Connection Error", "Cannot connect to the Django Database! Is db.sqlite3 accessible?");
        }
    }

    private void loadOrders() {
        ObservableList<String> orders = FXCollections.observableArrayList();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, first_name, last_name, total_amount, status FROM shop_order WHERE payment_method != 'whatsapp' ORDER BY id DESC LIMIT 25")) {
            
            while (rs.next()) {
                orders.add(String.format("Order #%05d | %s %s | $%.2f [%s]", 
                    rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name"), 
                    rs.getDouble("total_amount"), rs.getString("status").toUpperCase()));
            }
            orderListView.setItems(orders);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddToCart() {
        if (selectedProduct != null && selectedProduct.getStock() > 0) {
            CartItem item = cartMap.getOrDefault(selectedProduct.getId(), new CartItem(selectedProduct));
            if (cartMap.containsKey(selectedProduct.getId())) {
                item.incrementQty();
            }
            cartMap.put(selectedProduct.getId(), item);
            updateCartUI();
        } else {
            showAlert("Selection Error", "Please select a product that is in stock.");
        }
    }

    private void updateCartUI() {
        ObservableList<CartItem> dispList = FXCollections.observableArrayList();
        double subtotal = 0.0;
        
        for (CartItem item : cartMap.values()) {
            dispList.add(item);
            subtotal += item.getTotalPrice();
        }
        
        if (cartTable != null) {
            cartTable.setItems(dispList);
            cartTable.refresh();
        }

        if (cartTableCheckout != null) {
            cartTableCheckout.setItems(dispList);
            cartTableCheckout.refresh();
        }
        
        double tax = subtotal * 0.13;
        currentTotal = subtotal + tax; // Grand Total
        
        if (subtotalLabel != null) subtotalLabel.setText(String.format("Subtotal: $%.2f", subtotal));
        if (taxLabel != null) taxLabel.setText(String.format("Tax (13%%): $%.2f", tax));
        if (totalLabel != null) totalLabel.setText(String.format("Total: $%.2f", currentTotal));

        if (subtotalLabelCheckout != null) subtotalLabelCheckout.setText(String.format("Subtotal: $%.2f", subtotal));
        if (taxLabelCheckout != null) taxLabelCheckout.setText(String.format("Tax (13%%): $%.2f", tax));
        if (totalLabelCheckout != null) totalLabelCheckout.setText(String.format("GRAND TOTAL: $%.2f", currentTotal));
        if (totalLabelSidebar != null) totalLabelSidebar.setText(String.format("$%.2f", currentTotal));
    }

    @FXML
    public void switchToCheckout() {
        if (cartMap.isEmpty()) {
            showAlert("Empty Cart", "Add items to the local cart before checking out.");
            return;
        }
        inventoryPage.setVisible(false);
        checkoutPage.setVisible(true);
        updateCartUI(); // Refresh state for checkout table
    }

    @FXML
    public void switchToInventory() {
        checkoutPage.setVisible(false);
        inventoryPage.setVisible(true);
    }

    @FXML
    public void handleFinalCheckout() {
        String paymentStr = paymentSidebarList != null ? paymentSidebarList.getSelectionModel().getSelectedItem() : "Cash";
        if (paymentStr == null) paymentStr = "Cash";
        
        String paymentCode = "cod";
        if (paymentStr.equals("Credit Card")) paymentCode = "card";
        else if (paymentStr.equals("eSewa")) paymentCode = "esewa";
        else if (paymentStr.equals("PayPal")) paymentCode = "paypal";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Insert Local Sale into Django's shop_order table
                String insertOrder = "INSERT INTO shop_order (first_name, last_name, email, phone, address, city, postal_code, country, payment_method, status, created_at, updated_at, paid, total_amount, transaction_id) " +
                                     "VALUES ('DeskPOS', 'Customer', 'pos@curator.com', 'N/A', 'Local Store', 'Local', 'N/A', 'N/A', ?, 'delivered', ?, ?, 1, ?, ?)";
                
                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                
                PreparedStatement pstmtOrder = conn.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS);
                pstmtOrder.setString(1, paymentCode);
                pstmtOrder.setString(2, currentTime);
                pstmtOrder.setString(3, currentTime);
                pstmtOrder.setDouble(4, currentTotal);
                pstmtOrder.setString(5, "POS-" + paymentStr.toUpperCase().replaceAll("\\s+", ""));
                pstmtOrder.executeUpdate();
                
                // Update stock globally
                for (CartItem item : cartMap.values()) {
                    String updateStock = "UPDATE shop_product SET stock = stock - ? WHERE id = ?";
                    PreparedStatement pstmtStock = conn.prepareStatement(updateStock);
                    pstmtStock.setInt(1, item.getQty());
                    pstmtStock.setInt(2, item.getProduct().getId());
                    pstmtStock.executeUpdate();
                }

                conn.commit();
                
                // Print receipt BEFORE clearing the cart
                generateReceipt(paymentStr);
                
                cartMap.clear();
                updateCartUI();
                loadProducts(); 
                loadOrders();   
                loadFullSalesReport(); // Refresh analytics
                
                switchToInventory(); // Return to store
                showAlert("Success", "Bill Printed! Sale Processed via " + paymentStr + ".");

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                showAlert("Database Error", "Failed to process local sale.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCheckout() {
        switchToCheckout();
    }

    private void generateReceipt(String paymentMethod) {
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("           CURATOR ELECTRONICS            \n");
        sb.append("          Premium Tech & Gadgets          \n");
        sb.append("==========================================\n");
        sb.append("Date: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n");
        sb.append("Payment: ").append(paymentMethod).append("\n");
        sb.append("------------------------------------------\n");
        sb.append(String.format("%-25s %-5s %-10s\n", "Item", "Qty", "Price"));

        for (CartItem item : cartMap.values()) {
            sb.append(String.format("%-25.25s %-5d $%-10.2f\n",
                item.getName(), item.getQty(), item.getTotalPrice()));
        }

        sb.append("------------------------------------------\n");
        double subtotal = 0;
        for (CartItem item : cartMap.values()) subtotal += item.getTotalPrice();
        double tax = subtotal * 0.13;

        sb.append(String.format("%-31s $%-10.2f\n", "Subtotal:", subtotal));
        sb.append(String.format("%-31s $%-10.2f\n", "Tax (13%):", tax));
        sb.append("------------------------------------------\n");
        sb.append(String.format("%-31s $%-10.2f\n", "GRAND TOTAL:", currentTotal));
        sb.append("==========================================\n");
        sb.append("        THANK YOU FOR YOUR PURCHASE       \n");
        sb.append("==========================================\n");

        String receiptText = sb.toString();

        // Save to file as backup
        try (PrintWriter writer = new PrintWriter(new File("last_receipt.txt"))) {
            writer.print(receiptText);
        } catch (Exception e) { e.printStackTrace(); }

        // Print to physical printer
        printReceiptToPrinter(receiptText);
    }

    // ================================
    // PRINTER MANAGEMENT
    // ================================

    /**
     * Shows the printer selection dialog. Returns true if a printer was selected.
     */
    private boolean selectPrinter() {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        if (services.length == 0) {
            showAlert("No Printers", "No printers found on this system.");
            return false;
        }

        List<String> printerNames = new ArrayList<>();
        String defaultPrinter = services[0].getName();
        boolean foundReal = false;

        for (PrintService s : services) {
            String name = s.getName();
            printerNames.add(name);
            
            // Prioritize physical printers (avoid OneNote, PDF, XPS, etc.)
            if (!foundReal) {
                String lower = name.toLowerCase();
                if (!lower.contains("pdf") && !lower.contains("onenote") && 
                    !lower.contains("xps") && !lower.contains("fax") && 
                    !lower.contains("microsoft") && !lower.contains("root")) {
                    defaultPrinter = name;
                    foundReal = true;
                }
            }
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(defaultPrinter, printerNames);
        dialog.setTitle("Select Printer");
        dialog.setHeaderText("Choose a printer for bill printing");
        dialog.setContentText("Printer:");

        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            selectedPrinterName = result.get();
            for (PrintService s : services) {
                if (s.getName().equals(selectedPrinterName)) {
                    selectedPrinterService = s;
                    break;
                }
            }
            savePrinterConfig(selectedPrinterName);
            showAlert("Printer Selected", "Bills will now auto-print to:\n" + selectedPrinterName);
            return true;
        }
        return false;
    }

    private void loadPrinterConfig() {
        File configFile = new File("printer_config.txt");
        if (configFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                String name = reader.readLine();
                if (name != null && !name.isEmpty()) {
                    PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
                    for (PrintService s : services) {
                        if (s.getName().equals(name)) {
                            selectedPrinterService = s;
                            selectedPrinterName = name;
                            break;
                        }
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void savePrinterConfig(String name) {
        try (PrintWriter writer = new PrintWriter(new File("printer_config.txt"))) {
            writer.print(name);
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Prints receipt text directly to the selected printer.
     * If no printer is selected yet, shows the printer selection dialog first.
     */
    private void printReceiptToPrinter(String receiptText) {
        // If no printer selected yet, ask user to choose one
        if (selectedPrinterService == null) {
            if (!selectPrinter()) {
                // User cancelled - fall back to Notepad
                try {
                    new ProcessBuilder("notepad.exe", "last_receipt.txt").start();
                } catch (Exception ex) { ex.printStackTrace(); }
                return;
            }
        }

        try {
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            printerJob.setPrintService(selectedPrinterService);

            final String[] lines = receiptText.split("\n");

            printerJob.setPrintable((Graphics graphics, PageFormat pageFormat, int pageIndex) -> {
                if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                // Use a monospaced font for receipt alignment
                Font receiptFont = new Font("Consolas", Font.PLAIN, 9);
                g2d.setFont(receiptFont);

                int y = 15;
                int lineHeight = 12;
                for (String line : lines) {
                    g2d.drawString(line, 5, y);
                    y += lineHeight;
                }

                return Printable.PAGE_EXISTS;
            });

            // Print without showing dialog (auto-print)
            printerJob.print();

        } catch (PrinterException e) {
            e.printStackTrace();
            // If printer fails, offer to re-select
            showAlert("Print Failed",
                "Could not print to: " + selectedPrinterName + "\n" +
                "Error: " + e.getMessage() + "\n\n" +
                "The receipt was saved to last_receipt.txt.\n" +
                "Click 'Select Printer' to choose a different printer.");
            selectedPrinterService = null;
            selectedPrinterName = null;
        }
    }

    /**
     * Lets the user change the active printer at any time.
     */
    @FXML
    public void handleChangePrinter() {
        selectPrinter();
    }

    @FXML
    public void handleReloadApp() {
        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) userNameLabel.getScene().getWindow();
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("dashboard.fxml"));
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to reload the application: " + e.getMessage());
        }
    }

    private void loadFullSalesReport() {
        salesList.clear();
        totalRevenueAmount = 0.0;
        
        boolean showAll = showAllHistoryCheck != null && showAllHistoryCheck.isSelected();
        String selectedDate = reportDatePicker != null && reportDatePicker.getValue() != null ? reportDatePicker.getValue().toString() : "";

        String query = showAll ? 
            "SELECT * FROM shop_order WHERE status = 'delivered' ORDER BY id DESC" : 
            "SELECT * FROM shop_order WHERE date(created_at) = ? AND status = 'delivered' ORDER BY id DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            if (!showAll) {
                pstmt.setString(1, selectedDate);
            }
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                boolean isPaid = rs.getInt("paid") == 1;
                Order order = new Order(
                    rs.getInt("id"),
                    rs.getString("first_name") + " " + rs.getString("last_name"),
                    rs.getDouble("total_amount"),
                    rs.getString("status"),
                    rs.getString("created_at"),
                    rs.getString("payment_method"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("city"),
                    rs.getString("postal_code"),
                    rs.getString("country"),
                    isPaid,
                    rs.getString("transaction_id")
                );
                salesList.add(order);
                totalRevenueAmount += order.getTotalAmount();
            }
            updateFinancialSummary();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadExpenses() {
        expenseList.clear();
        totalExpenseAmount = 0.0;
        
        boolean showAll = expShowAllCheck != null && expShowAllCheck.isSelected();
        String selectedDate = expFilterDatePicker != null && expFilterDatePicker.getValue() != null ? expFilterDatePicker.getValue().toString() : "";

        String query = showAll ? 
            "SELECT * FROM pos_expenses ORDER BY date DESC" : 
            "SELECT * FROM pos_expenses WHERE date = ? ORDER BY date DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            if (!showAll) {
                pstmt.setString(1, selectedDate);
            }
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Expense exp = new Expense(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getDouble("amount"),
                    rs.getString("category"),
                    rs.getString("date"),
                    rs.getString("description"),
                    rs.getString("bill_image"),
                    rs.getString("bill_no")
                );
                expenseList.add(exp);
                totalExpenseAmount += exp.getAmount();
            }
            if (totalExpensesLabel != null) totalExpensesLabel.setText(String.format("Total Expenses: $%.2f", totalExpenseAmount));
            updateFinancialSummary();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSaveExpense() {
        if (expTitleField.getText().isEmpty() || expAmountField.getText().isEmpty()) {
            showAlert("Input Error", "Please provide a title and amount for the expense.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            String insertExp = "INSERT INTO pos_expenses (title, amount, category, date, description, bill_image, bill_no) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertExp);
            pstmt.setString(1, expTitleField.getText());
            pstmt.setDouble(2, Double.parseDouble(expAmountField.getText()));
            pstmt.setString(3, expCategoryBox.getValue());
            
            String dateStr = expDatePicker.getValue() != null ? expDatePicker.getValue().toString() : new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            pstmt.setString(4, dateStr);
            pstmt.setString(5, expDescField.getText());
            pstmt.setString(6, expBillField.getText());
            pstmt.setString(7, expBillNoField.getText());
            pstmt.executeUpdate();

            expTitleField.clear();
            expAmountField.clear();
            expDescField.clear();
            expBillField.clear();
            expBillNoField.clear();
            expDatePicker.setValue(java.time.LocalDate.now());
            
            loadExpenses();
            showAlert("Success", "Expense logged successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save expense. Ensure amount is a number.");
        }
    }

    @FXML
    public void handleBrowseExpenseBill() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Bill/Invoice Image");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.pdf"));
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            try {
                File destDir = new File("../media/expenses");
                if (!destDir.exists()) destDir.mkdirs();
                File destFile = new File(destDir, System.currentTimeMillis() + "_" + file.getName().replaceAll("[^a-zA-Z0-9.-]", "_"));
                Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                expBillField.setText("expenses/" + destFile.getName());
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Copy Error", "Failed to copy bill image to media folder.");
            }
        }
    }

    private void updateFinancialSummary() {
        if (reportTotalRevenue != null) reportTotalRevenue.setText(String.format("$%.2f", totalRevenueAmount));
        if (reportTotalOrders != null) reportTotalOrders.setText(String.valueOf(salesList.size()));
        if (reportNetProfit != null) reportNetProfit.setText(String.format("$%.2f", totalRevenueAmount - totalExpenseAmount));
        
        int totalProducts = 0;
        boolean showAll = showAllHistoryCheck != null && showAllHistoryCheck.isSelected();
        String selectedDate = reportDatePicker != null && reportDatePicker.getValue() != null ? reportDatePicker.getValue().toString() : "";
        
        String query = showAll ? 
            "SELECT SUM(quantity) FROM shop_orderitem oi JOIN shop_order o ON oi.order_id = o.id WHERE o.status = 'delivered'" : 
            "SELECT SUM(quantity) FROM shop_orderitem oi JOIN shop_order o ON oi.order_id = o.id WHERE date(o.created_at) = ? AND o.status = 'delivered'";
            
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            if (!showAll) pstmt.setString(1, selectedDate);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                totalProducts = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        if (reportTotalProducts != null) reportTotalProducts.setText(String.valueOf(totalProducts));
    }

    @FXML
    public void handleAddProduct() {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Add New Product");
        dialog.setHeaderText("Create a new product directly into the Database");

        ButtonType saveButtonType = new ButtonType("Save to Database", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        TextField nameField = new TextField(); nameField.setPromptText("Awesome Headphones");
        TextField priceField = new TextField(); priceField.setPromptText("99.99");
        TextField stockField = new TextField(); stockField.setPromptText("50");
        
        TextField imageField = new TextField(); imageField.setPromptText("Main image path...");
        imageField.setPrefWidth(200);
        imageField.setEditable(false);
        
        List<File> selectedFiles = new ArrayList<>();
        Button browseBtn = new Button("Browse Images");
        browseBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select Product Images");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.webp"));
            List<File> files = chooser.showOpenMultipleDialog(null);
            if (files != null && !files.isEmpty()) {
                selectedFiles.clear();
                selectedFiles.addAll(files);
                imageField.setText(files.size() + " images selected (First: " + files.get(0).getName() + ")");
            }
        });
        HBox imageBox = new HBox(5, imageField, browseBtn);
        
        TextArea descField = new TextArea(); descField.setPromptText("Description...");
        ComboBox<String> categoryBox = new ComboBox<>();
        
        Map<String, Integer> categoryMap = new HashMap<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM shop_category")) {
            while (rs.next()) {
                categoryMap.put(rs.getString("name"), rs.getInt("id"));
                categoryBox.getItems().add(rs.getString("name"));
            }
            if (!categoryBox.getItems().isEmpty()) categoryBox.getSelectionModel().selectFirst();
        } catch (SQLException e) { }

        grid.add(new Label("Product Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Price ($):"), 0, 1); grid.add(priceField, 1, 1);
        grid.add(new Label("Stock Qty:"), 0, 2); grid.add(stockField, 1, 2);
        grid.add(new Label("Imgs (e.g. products/item.jpg):"), 0, 3); grid.add(imageBox, 1, 3);
        grid.add(new Label("Category:"), 0, 4); grid.add(categoryBox, 1, 4);
        grid.add(new Label("Description:"), 0, 5); grid.add(descField, 1, 5);
        
        TextField shipField = new TextField("Free Fast Delivery");
        TextField warrantyField = new TextField("2 Year Warranty");
        grid.add(new Label("Shipping:"), 0, 6); grid.add(shipField, 1, 6);
        grid.add(new Label("Warranty:"), 0, 7); grid.add(warrantyField, 1, 7);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try (Connection conn = DatabaseManager.getConnection()) {
                    String insertProd = "INSERT INTO shop_product (name, slug, description, price, image, category_id, is_new_arrival, is_top_selling, stock, created_at, shipping_info, warranty_info) " +
                                        "VALUES (?, ?, ?, ?, ?, ?, 1, 0, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(insertProd, Statement.RETURN_GENERATED_KEYS);
                    pstmt.setString(1, nameField.getText());
                    String timestamp = new SimpleDateFormat("ss").format(new Date()); 
                    pstmt.setString(2, nameField.getText().toLowerCase().replaceAll("[^a-z0-9]+", "-") + "-" + timestamp);
                    pstmt.setString(3, descField.getText());
                    pstmt.setDouble(4, Double.parseDouble(priceField.getText()));
                    
                    // Handle Main Image Copying
                    String mainImgPath = "products/default.jpg";
                    if (!selectedFiles.isEmpty()) {
                        File firstFile = selectedFiles.get(0);
                        try {
                            File destDir = new File("../media/products");
                            if (!destDir.exists()) destDir.mkdirs();
                            File destFile = new File(destDir, firstFile.getName().replaceAll("[^a-zA-Z0-9.-]", "_"));
                            Files.copy(firstFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            mainImgPath = "products/" + destFile.getName();
                        } catch (Exception ex) { ex.printStackTrace(); }
                    }
                    pstmt.setString(5, mainImgPath);
                    
                    pstmt.setInt(6, categoryMap.getOrDefault(categoryBox.getValue(), 1));
                    pstmt.setInt(7, Integer.parseInt(stockField.getText()));
                    pstmt.setString(8, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    pstmt.setString(9, shipField.getText());
                    pstmt.setString(10, warrantyField.getText());
                    pstmt.executeUpdate();

                    // Get the generated product ID
                    ResultSet rsKeys = pstmt.getGeneratedKeys();
                    if (rsKeys.next()) {
                        int productId = rsKeys.getInt(1);
                        
                        // Handle Additional Images (Gallery)
                        if (selectedFiles.size() > 1) {
                            String insertGallery = "INSERT INTO shop_productimage (product_id, image) VALUES (?, ?)";
                            PreparedStatement pstmtGallery = conn.prepareStatement(insertGallery);
                            
                            File galleryDir = new File("../media/products/gallery");
                            if (!galleryDir.exists()) galleryDir.mkdirs();

                            for (int i = 1; i < selectedFiles.size(); i++) {
                                File file = selectedFiles.get(i);
                                try {
                                    File destFile = new File(galleryDir, System.currentTimeMillis() + "_" + file.getName().replaceAll("[^a-zA-Z0-9.-]", "_"));
                                    Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    
                                    pstmtGallery.setInt(1, productId);
                                    pstmtGallery.setString(2, "products/gallery/" + destFile.getName());
                                    pstmtGallery.addBatch();
                                } catch (Exception ex) { ex.printStackTrace(); }
                            }
                            pstmtGallery.executeBatch();
                        }
                    }
                    
                    return new Product(-1, nameField.getText(), Double.parseDouble(priceField.getText()), Integer.parseInt(stockField.getText()), categoryMap.getOrDefault(categoryBox.getValue(), 1));
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Insert Error", "Failed to insert product. Check fields.");
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            loadProducts();
            showAlert("Success", "Product added and synced successfully!");
        });
    }

    @FXML
    public void handleDeleteProduct() {
        Product selected = managerProductTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a product to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Product: " + selected.getName());
        confirm.setContentText("Are you sure you want to permanently delete this product from the database?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = DatabaseManager.getConnection()) {
                    String sql = "DELETE FROM shop_product WHERE id = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, selected.getId());
                    pstmt.executeUpdate();
                    
                    loadProducts();
                    showAlert("Success", "Product deleted successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Database Error", "Failed to delete product.");
                }
            }
        });
    }

    @FXML
    public void handleEditProduct() {
        Product selected = managerProductTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a product to edit.");
            return;
        }

        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Edit Product");
        dialog.setHeaderText("Update Product Details");

        ButtonType saveButtonType = new ButtonType("Update Database", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        
        TextField nameField = new TextField(selected.getName());
        TextField priceField = new TextField(String.valueOf(selected.getPrice()));
        TextField stockField = new TextField(String.valueOf(selected.getStock()));
        TextField imageField = new TextField(selected.getImage());
        imageField.setPrefWidth(120);
        Button browseBtn = new Button("Browse");
        browseBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select Product Image");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.webp"));
            File file = chooser.showOpenDialog(null);
            if (file != null) {
                try {
                    File destDir = new File("../media/products");
                    if (!destDir.exists()) destDir.mkdirs();
                    File destFile = new File(destDir, file.getName().replaceAll("[^a-zA-Z0-9.-]", "_"));
                    Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    imageField.setText("products/" + destFile.getName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert("Copy Error", "Failed to copy image.");
                }
            }
        });
        HBox imageBox = new HBox(5, imageField, browseBtn);
        TextArea descField = new TextArea(selected.getDescription()); descField.setPrefHeight(80);
        ComboBox<String> categoryBox = new ComboBox<>();
        
        String currentCatName = "Unknown";
        for (Map.Entry<String, Integer> entry : catIdMap.entrySet()) {
            categoryBox.getItems().add(entry.getKey());
            if (entry.getValue() == selected.getCategoryId()) {
                currentCatName = entry.getKey();
            }
        }
        categoryBox.getSelectionModel().select(currentCatName);

        grid.add(new Label("Product Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Price ($):"), 0, 1); grid.add(priceField, 1, 1);
        grid.add(new Label("Stock Qty:"), 0, 2); grid.add(stockField, 1, 2);
        grid.add(new Label("Image Path:"), 0, 3); grid.add(imageBox, 1, 3);
        grid.add(new Label("Category:"), 0, 4); grid.add(categoryBox, 1, 4);
        grid.add(new Label("Description:"), 0, 5); grid.add(descField, 1, 5);
        
        TextField shipField = new TextField(selected.getShippingInfo());
        TextField warrantyField = new TextField(selected.getWarrantyInfo());
        grid.add(new Label("Shipping:"), 0, 6); grid.add(shipField, 1, 6);
        grid.add(new Label("Warranty:"), 0, 7); grid.add(warrantyField, 1, 7);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try (Connection conn = DatabaseManager.getConnection()) {
                    String sql = "UPDATE shop_product SET name = ?, price = ?, stock = ?, image = ?, category_id = ?, description = ?, shipping_info = ?, warranty_info = ? WHERE id = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, nameField.getText());
                    pstmt.setDouble(2, Double.parseDouble(priceField.getText()));
                    pstmt.setInt(3, Integer.parseInt(stockField.getText()));
                    pstmt.setString(4, imageField.getText());
                    pstmt.setInt(5, catIdMap.getOrDefault(categoryBox.getValue(), selected.getCategoryId()));
                    pstmt.setString(6, descField.getText());
                    pstmt.setString(7, shipField.getText());
                    pstmt.setString(8, warrantyField.getText());
                    pstmt.setInt(9, selected.getId());
                    pstmt.executeUpdate();
                    return selected;
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Update Error", "Failed to update product: " + e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            loadProducts();
            showAlert("Success", "Product updated successfully!");
        });
    }


    @FXML
    public void handleExportSales() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Sales Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("Sales_Report_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".csv");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                writer.println("Order ID,Date,Customer,Payment Method,Amount,Status");
                for (Order order : salesList) {
                    writer.println(String.format("%d,%s,\"%s\",%s,%.2f,%s",
                        order.getId(), order.getCreatedAt(), order.getCustomerName(),
                        order.getPaymentMethod(), order.getTotalAmount(), order.getStatus()));
                }
                showAlert("Export Success", "Sales report exported to: " + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Export Error", "Failed to save the sales report.");
            }
        }
    }

    @FXML
    public void handleExportExpenses() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Expense Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("Expense_Report_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".csv");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                writer.println("ID,Date,Title,Category,Amount,Description");
                for (Expense exp : expenseList) {
                    writer.println(String.format("%d,%s,\"%s\",%s,%.2f,\"%s\"",
                        exp.getId(), exp.getDate(), exp.getTitle(),
                        exp.getCategory(), exp.getAmount(), exp.getDescription().replace("\"", "\"\"")));
                }
                showAlert("Export Success", "Expense report exported to: " + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Export Error", "Failed to save the expense report.");
            }
        }
    }

    // ================================
    // SALES REPORT EDIT & DELETE
    // ================================

    @FXML
    public void handleEditSalesOrder() {
        Order selected = salesReportTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an order to edit.");
            return;
        }

        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Edit Order #" + selected.getId());
        dialog.setHeaderText("Modify order from Sales Reports");

        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefWidth(600);

        // --- Fields ---
        TextField firstNameField = new TextField(selected.getCustomerName().contains(" ") ? selected.getCustomerName().split(" ")[0] : selected.getCustomerName());
        TextField lastNameField = new TextField(selected.getCustomerName().contains(" ") ? selected.getCustomerName().substring(selected.getCustomerName().indexOf(' ') + 1) : "");
        TextField emailField = new TextField(selected.getEmail());
        TextField phoneField = new TextField(selected.getPhone());
        TextField addressField = new TextField(selected.getAddress());
        TextField cityField = new TextField(selected.getCity());
        TextField postalField = new TextField(selected.getPostalCode());
        TextField countryField = new TextField(selected.getCountry());
        TextField amountField = new TextField(String.format("%.2f", selected.getTotalAmount()));

        ComboBox<String> statusBox = new ComboBox<>(FXCollections.observableArrayList("pending", "processing", "shipped", "delivered", "cancelled"));
        statusBox.getSelectionModel().select(selected.getStatus());

        ComboBox<String> paymentBox = new ComboBox<>(FXCollections.observableArrayList("cod", "esewa", "paypal", "card"));
        paymentBox.getSelectionModel().select(selected.getPaymentMethod());

        CheckBox paidCheck = new CheckBox("Mark as Paid");
        paidCheck.setSelected(selected.isPaid());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.add(new Label("First Name:"), 0, 0); grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 2, 0); grid.add(lastNameField, 3, 0);
        grid.add(new Label("Email:"), 0, 1); grid.add(emailField, 1, 1);
        grid.add(new Label("Phone:"), 2, 1); grid.add(phoneField, 3, 1);
        grid.add(new Label("Address:"), 0, 2); grid.add(addressField, 1, 2, 3, 1);
        grid.add(new Label("City:"), 0, 3); grid.add(cityField, 1, 3);
        grid.add(new Label("Postal:"), 2, 3); grid.add(postalField, 3, 3);
        grid.add(new Label("Country:"), 0, 4); grid.add(countryField, 1, 4);
        grid.add(new Label("Status:"), 0, 5); grid.add(statusBox, 1, 5);
        grid.add(new Label("Payment:"), 2, 5); grid.add(paymentBox, 3, 5);
        grid.add(paidCheck, 1, 6);
        grid.add(new Label("Total Amount ($):"), 0, 7); grid.add(amountField, 1, 7);

        VBox layout = new VBox(12, new Label("ORDER DETAILS"), grid);
        layout.setStyle("-fx-padding: 10;");

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        dialog.getDialogPane().setContent(scrollPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try (Connection conn = DatabaseManager.getConnection()) {
                    String sql = "UPDATE shop_order SET first_name=?, last_name=?, email=?, phone=?, " +
                        "address=?, city=?, postal_code=?, country=?, status=?, payment_method=?, paid=?, " +
                        "total_amount=?, updated_at=? WHERE id=?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, firstNameField.getText());
                    pstmt.setString(2, lastNameField.getText());
                    pstmt.setString(3, emailField.getText());
                    pstmt.setString(4, phoneField.getText());
                    pstmt.setString(5, addressField.getText());
                    pstmt.setString(6, cityField.getText());
                    pstmt.setString(7, postalField.getText());
                    pstmt.setString(8, countryField.getText());
                    pstmt.setString(9, statusBox.getValue());
                    pstmt.setString(10, paymentBox.getValue());
                    pstmt.setInt(11, paidCheck.isSelected() ? 1 : 0);
                    pstmt.setDouble(12, Double.parseDouble(amountField.getText()));
                    pstmt.setString(13, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    pstmt.setInt(14, selected.getId());
                    pstmt.executeUpdate();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Save Error", "Failed to update order: " + e.getMessage());
                }
            }
            return false;
        });

        dialog.showAndWait().ifPresent(saved -> {
            if (saved) {
                refreshData();
                showAlert("Success", "Order #" + selected.getId() + " updated successfully!");
            }
        });
    }

    @FXML
    public void handleDeleteSalesOrder() {
        Order selected = salesReportTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an order to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Order");
        confirm.setHeaderText("Delete Order #" + selected.getId() + "?");
        confirm.setContentText(
            "Customer: " + selected.getCustomerName() + "\n" +
            "Amount: $" + String.format("%.2f", selected.getTotalAmount()) + "\n" +
            "Status: " + selected.getStatus().toUpperCase() + "\n\n" +
            "This will permanently delete this order and restore product stock.\n" +
            "This action cannot be undone!"
        );

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = DatabaseManager.getConnection()) {
                    conn.setAutoCommit(false);
                    try {
                        // 1. Restore stock
                        PreparedStatement getItems = conn.prepareStatement(
                            "SELECT product_id, quantity FROM shop_orderitem WHERE order_id = ?");
                        getItems.setInt(1, selected.getId());
                        ResultSet rs = getItems.executeQuery();
                        while (rs.next()) {
                            PreparedStatement restoreStock = conn.prepareStatement(
                                "UPDATE shop_product SET stock = stock + ? WHERE id = ?");
                            restoreStock.setInt(1, rs.getInt("quantity"));
                            restoreStock.setInt(2, rs.getInt("product_id"));
                            restoreStock.executeUpdate();
                        }

                        // 2. Delete order items
                        PreparedStatement delItems = conn.prepareStatement(
                            "DELETE FROM shop_orderitem WHERE order_id = ?");
                        delItems.setInt(1, selected.getId());
                        delItems.executeUpdate();

                        // 3. Delete the order
                        PreparedStatement delOrder = conn.prepareStatement(
                            "DELETE FROM shop_order WHERE id = ?");
                        delOrder.setInt(1, selected.getId());
                        delOrder.executeUpdate();

                        conn.commit();
                        refreshData();
                        showAlert("Deleted", "Order #" + selected.getId() + " has been permanently deleted and stock restored.");

                    } catch (SQLException ex) {
                        conn.rollback();
                        ex.printStackTrace();
                        showAlert("Delete Error", "Failed to delete order: " + ex.getMessage());
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert("Database Error", "Could not connect to database.");
                }
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void handleLogout() {
        DatabaseManager.logUserAction(SessionManager.getLoggedInUser(), "LOGOUT");
        SessionManager.cleanSession();
        try {
            Stage stage = (Stage) managerProductTable.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            stage.setScene(new Scene(root, 1000, 700));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleFormPunchIn() {
        String username = userUsernameField.getText();
        String password = userPasswordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Validation Error", "Please enter both Username and Password to Punch In.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            // Verify password
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT password FROM auth_user WHERE username = ?")) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (PasswordUtil.verifyDjangoPassword(password, storedHash)) {
                        try (PreparedStatement insertStmt = conn.prepareStatement(
                                "INSERT INTO pos_staff_timesheet (username, date, punch_in_time) VALUES (?, date('now', 'localtime'), datetime('now', 'localtime'))")) {
                            insertStmt.setString(1, username);
                            insertStmt.executeUpdate();
                            showAlert("Punched In", username + "'s shift start time has been logged.");
                            userUsernameField.clear();
                            userPasswordField.clear();
                            loadUserLogs();
                        }
                    } else {
                        showAlert("Auth Error", "Invalid password.");
                    }
                } else {
                    showAlert("Auth Error", "Username not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to punch in: " + e.getMessage());
        }
    }

    private void punchOutTimesheet(int timesheetId, String username) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE pos_staff_timesheet SET punch_out_time = datetime('now', 'localtime') WHERE id = ?")) {
            pstmt.setInt(1, timesheetId);
            pstmt.executeUpdate();
            showAlert("Punched Out", username + " has been punched out.");
            loadUserLogs();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to punch out: " + e.getMessage());
        }
    }

    @FXML
    public void refreshData() {
        loadProducts();
        loadOrders();
        loadExpenses();
        loadFullSalesReport();
        loadWebsiteOrders();
        loadWhatsAppOrders();
    }

    // ========================
    // WEBSITE ORDERS TAB LOGIC
    // ========================

    private void loadWebsiteOrders() {
        if (webOrderTable == null) return;
        
        // Save current selection ID
        int selectedId = (selectedWebOrder != null) ? selectedWebOrder.getId() : -1;
        
        webOrderList.clear();
        int paidCount = 0, unpaidCount = 0, pendingCount = 0;
        double revenue = 0.0;

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT * FROM shop_order WHERE payment_method != 'whatsapp' ORDER BY id DESC")) {

            while (rs.next()) {
                boolean isPaid = rs.getInt("paid") == 1;
                String status = rs.getString("status");
                Order order = new Order(
                    rs.getInt("id"),
                    rs.getString("first_name") + " " + rs.getString("last_name"),
                    rs.getDouble("total_amount"),
                    status,
                    rs.getString("created_at"),
                    rs.getString("payment_method"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("city"),
                    rs.getString("postal_code"),
                    rs.getString("country"),
                    isPaid,
                    rs.getString("transaction_id")
                );
                webOrderList.add(order);
                if (isPaid) paidCount++; else unpaidCount++;
                if ("pending".equalsIgnoreCase(status)) pendingCount++;
                revenue += order.getTotalAmount();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Restore selection
        if (selectedId != -1) {
            for (Order o : webOrderList) {
                if (o.getId() == selectedId) {
                    webOrderTable.getSelectionModel().select(o);
                    break;
                }
            }
        }

        // Update summary cards
        if (webTotalOrdersLabel != null) webTotalOrdersLabel.setText(String.valueOf(webOrderList.size()));
        if (webPendingLabel != null) webPendingLabel.setText(String.valueOf(pendingCount));
        if (webPaidCountLabel != null) webPaidCountLabel.setText(paidCount + " / " + unpaidCount);
        if (webRevenueLabel != null) webRevenueLabel.setText(String.format("$%.2f", revenue));
    }

    private void applyWebOrderFilters() {
        if (filteredWebOrders == null) return;
        String statusFilter = webOrderStatusFilter != null ? webOrderStatusFilter.getValue() : "All Status";
        String paymentFilter = webOrderPaymentFilter != null ? webOrderPaymentFilter.getValue() : "All Payments";
        String search = webOrderSearchField != null && webOrderSearchField.getText() != null ? webOrderSearchField.getText().toLowerCase() : "";

        filteredWebOrders.setPredicate(order -> {
            boolean matchesStatus = statusFilter == null || "All Status".equals(statusFilter) || statusFilter.equalsIgnoreCase(order.getStatus());
            boolean matchesPayment = paymentFilter == null || "All Payments".equals(paymentFilter) || paymentFilter.equalsIgnoreCase(order.getPaymentMethod());
            boolean matchesSearch = search.isEmpty() ||
                order.getCustomerName().toLowerCase().contains(search) ||
                order.getEmail().toLowerCase().contains(search) ||
                String.valueOf(order.getId()).contains(search);
            return matchesStatus && matchesPayment && matchesSearch;
        });
    }

    private void populateWebOrderDetail(Order order) {
        if (order == null) {
            if (webOrderDetailPanel != null) {
                webOrderDetailPanel.setVisible(false);
                webOrderDetailPanel.setManaged(false);
            }
            return;
        }
        
        if (webOrderDetailPanel != null) {
            webOrderDetailPanel.setVisible(true);
            webOrderDetailPanel.setManaged(true);
        }
        if (webDetailOrderId != null) webDetailOrderId.setText("Order #" + String.format("%05d", order.getId()) + "  •  " + order.getStatus().toUpperCase());
        if (webDetailCustomerName != null) webDetailCustomerName.setText(order.getCustomerName());
        if (webDetailEmail != null) webDetailEmail.setText(order.getEmail());
        if (webDetailPhone != null) webDetailPhone.setText("📞 " + order.getPhone());
        if (webDetailAddress != null) webDetailAddress.setText(order.getFullAddress());
        if (webDetailPayment != null) webDetailPayment.setText(order.getPaymentLabel());
        if (webDetailPaidStatus != null) {
            webDetailPaidStatus.setText(order.getPaid());
            webDetailPaidStatus.setStyle(order.isPaid() ? "-fx-text-fill: #10b981; -fx-font-weight: bold; -fx-font-size: 13px;" : "-fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 13px;");
        }
        if (webDetailTransId != null) webDetailTransId.setText("TXN: " + order.getTransactionId());
        if (webDetailAmount != null) webDetailAmount.setText(String.format("$%.2f", order.getTotalAmount()));

        // Load order items
        if (webDetailItemsList != null) {
            ObservableList<String> items = FXCollections.observableArrayList();
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT oi.quantity, oi.price, p.name FROM shop_orderitem oi " +
                     "LEFT JOIN shop_product p ON oi.product_id = p.id " +
                     "WHERE oi.order_id = ?")) {
                pstmt.setInt(1, order.getId());
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    items.add(String.format("%dx  %-20s  $%.2f",
                        rs.getInt("quantity"),
                        rs.getString("name") != null ? rs.getString("name") : "(deleted)",
                        rs.getDouble("price") * rs.getInt("quantity")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (items.isEmpty()) items.add("(No items — POS sale or items unavailable)");
            webDetailItemsList.setItems(items);
        }

        // Pre-select the current status in the update combo
        if (webStatusUpdateBox != null) {
            webStatusUpdateBox.getSelectionModel().select(order.getStatus());
        }
    }

    @FXML
    public void handleCopyAddress() {
        if (selectedWebOrder == null) {
            selectedWebOrder = webOrderTable.getSelectionModel().getSelectedItem();
        }
        if (selectedWebOrder == null) {
            showAlert("No Selection", "Please select an order first.");
            return;
        }
        String address = selectedWebOrder.getFullAddress();
        if (address == null || address.trim().isEmpty()) {
            showAlert("No Address", "This order has no shipping address.");
            return;
        }
        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(address);
        clipboard.setContent(content);
        showAlert("Copied!", "Address copied to clipboard:\n" + address);
    }

    @FXML
    public void handleOpenGoogleMaps() {
        if (selectedWebOrder == null) {
            selectedWebOrder = webOrderTable.getSelectionModel().getSelectedItem();
        }
        if (selectedWebOrder == null) {
            showAlert("No Selection", "Please select an order first.");
            return;
        }
        String address = selectedWebOrder.getFullAddress();
        if (address == null || address.trim().isEmpty()) {
            showAlert("No Address", "This order has no shipping address to locate.");
            return;
        }
        try {
            String encoded = java.net.URLEncoder.encode(address, "UTF-8");
            String url = "https://www.google.com/maps/search/?api=1&query=" + encoded;
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not open Google Maps: " + e.getMessage());
        }
    }

    @FXML
    public void handleRefreshWebOrders() {
        loadWebsiteOrders();
    }

    @FXML
    public void handleUpdateOrderStatus() {
        if (selectedWebOrder == null) {
            selectedWebOrder = webOrderTable.getSelectionModel().getSelectedItem();
        }
        
        if (selectedWebOrder == null) {
            showAlert("No Selection", "Please select an order to update.");
            return;
        }
        String newStatus = webStatusUpdateBox != null ? webStatusUpdateBox.getValue() : null;
        if (newStatus == null || newStatus.isEmpty()) {
            showAlert("No Status", "Please select a status from the dropdown.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "UPDATE shop_order SET status = ?, updated_at = ? WHERE id = ?")) {
            pstmt.setString(1, newStatus);
            pstmt.setString(2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            pstmt.setInt(3, selectedWebOrder.getId());
            pstmt.executeUpdate();

            showAlert("Status Updated", "Order #" + selectedWebOrder.getId() + " → " + newStatus.toUpperCase());
            loadWebsiteOrders();
            loadFullSalesReport();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to update order status.");
        }
    }

    @FXML
    public void handlePrintWebOrderBill() {
        if (selectedWebOrder == null) {
            selectedWebOrder = webOrderTable.getSelectionModel().getSelectedItem();
        }
        if (selectedWebOrder == null) {
            showAlert("No Selection", "Please select an order to print.");
            return;
        }

        Order order = selectedWebOrder;
        StringBuilder sb = new StringBuilder();
        sb.append("==============================================\n");
        sb.append("            CURATOR ELECTRONICS               \n");
        sb.append("           Premium Tech & Gadgets             \n");
        sb.append("==============================================\n");
        sb.append("  ORDER #").append(String.format("%05d", order.getId())).append("\n");
        sb.append("----------------------------------------------\n");
        sb.append("Date    : ").append(order.getCreatedAt()).append("\n");
        sb.append("Status  : ").append(order.getStatus().toUpperCase()).append("\n");
        sb.append("Payment : ").append(order.getPaymentLabel()).append("\n");
        sb.append("Paid    : ").append(order.isPaid() ? "YES" : "NO").append("\n");
        if (!order.getTransactionId().isEmpty()) {
            sb.append("TXN ID  : ").append(order.getTransactionId()).append("\n");
        }
        sb.append("----------------------------------------------\n");
        sb.append("CUSTOMER DETAILS\n");
        sb.append("  Name  : ").append(order.getCustomerName()).append("\n");
        sb.append("  Email : ").append(order.getEmail()).append("\n");
        sb.append("  Phone : ").append(order.getPhone()).append("\n");
        sb.append("----------------------------------------------\n");
        sb.append("SHIPPING ADDRESS\n");
        sb.append("  ").append(order.getFullAddress()).append("\n");
        sb.append("----------------------------------------------\n");
        sb.append(String.format("  %-28s %-5s %-10s\n", "Item", "Qty", "Price"));
        sb.append("----------------------------------------------\n");

        double itemsTotal = 0;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT oi.quantity, oi.price, p.name FROM shop_orderitem oi " +
                 "LEFT JOIN shop_product p ON oi.product_id = p.id " +
                 "WHERE oi.order_id = ?")) {
            pstmt.setInt(1, order.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String pName = rs.getString("name") != null ? rs.getString("name") : "(deleted)";
                int qty = rs.getInt("quantity");
                double price = rs.getDouble("price") * qty;
                itemsTotal += price;
                sb.append(String.format("  %-28.28s %-5d $%.2f\n", pName, qty, price));
            }
        } catch (SQLException e) {
            sb.append("  (Could not load order items)\n");
        }

        sb.append("----------------------------------------------\n");
        if (itemsTotal > 0) {
            double tax = itemsTotal * 0.13;
            sb.append(String.format("  %-34s $%.2f\n", "Subtotal:", itemsTotal));
            sb.append(String.format("  %-34s $%.2f\n", "Tax (13%):", tax));
        }
        sb.append("----------------------------------------------\n");
        sb.append(String.format("  %-34s $%.2f\n", "GRAND TOTAL:", order.getTotalAmount()));
        sb.append("==============================================\n");
        sb.append("         THANK YOU FOR YOUR PURCHASE          \n");
        sb.append("==============================================\n");

        String receiptText = sb.toString();

        // Save to file as backup
        try (PrintWriter writer = new PrintWriter(new File("last_receipt.txt"))) {
            writer.print(receiptText);
        } catch (Exception e) { e.printStackTrace(); }

        // Print to physical printer
        printReceiptToPrinter(receiptText);
        showAlert("Printing", "Bill for Order #" + order.getId() + " sent to printer" +
            (selectedPrinterName != null ? ": " + selectedPrinterName : "") + ".");
    }

    @FXML
    public void handleEditWebOrder() {
        if (selectedWebOrder == null) {
            selectedWebOrder = webOrderTable.getSelectionModel().getSelectedItem();
        }
        if (selectedWebOrder == null) {
            showAlert("No Selection", "Please select an order to edit.");
            return;
        }

        Order order = selectedWebOrder;

        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Edit Order #" + order.getId());
        dialog.setHeaderText("Modify order details and items");

        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefWidth(700);

        // --- Customer fields ---
        TextField firstNameField = new TextField(order.getCustomerName().contains(" ") ? order.getCustomerName().split(" ")[0] : order.getCustomerName());
        TextField lastNameField = new TextField(order.getCustomerName().contains(" ") ? order.getCustomerName().substring(order.getCustomerName().indexOf(' ') + 1) : "");
        TextField emailField = new TextField(order.getEmail());
        TextField phoneField = new TextField(order.getPhone());
        TextField addressField = new TextField(order.getAddress());
        TextField cityField = new TextField(order.getCity());
        TextField postalField = new TextField(order.getPostalCode());
        TextField countryField = new TextField(order.getCountry());

        ComboBox<String> statusBox = new ComboBox<>(FXCollections.observableArrayList("pending", "processing", "shipped", "delivered", "cancelled"));
        statusBox.getSelectionModel().select(order.getStatus());

        ComboBox<String> paymentBox = new ComboBox<>(FXCollections.observableArrayList("cod", "esewa", "paypal", "card"));
        paymentBox.getSelectionModel().select(order.getPaymentMethod());

        CheckBox paidCheck = new CheckBox("Mark as Paid");
        paidCheck.setSelected(order.isPaid());

        GridPane customerGrid = new GridPane();
        customerGrid.setHgap(10);
        customerGrid.setVgap(8);
        customerGrid.add(new Label("First Name:"), 0, 0); customerGrid.add(firstNameField, 1, 0);
        customerGrid.add(new Label("Last Name:"), 2, 0); customerGrid.add(lastNameField, 3, 0);
        customerGrid.add(new Label("Email:"), 0, 1); customerGrid.add(emailField, 1, 1);
        customerGrid.add(new Label("Phone:"), 2, 1); customerGrid.add(phoneField, 3, 1);
        customerGrid.add(new Label("Address:"), 0, 2); customerGrid.add(addressField, 1, 2, 3, 1);
        customerGrid.add(new Label("City:"), 0, 3); customerGrid.add(cityField, 1, 3);
        customerGrid.add(new Label("Postal:"), 2, 3); customerGrid.add(postalField, 3, 3);
        customerGrid.add(new Label("Country:"), 0, 4); customerGrid.add(countryField, 1, 4);
        customerGrid.add(new Label("Status:"), 0, 5); customerGrid.add(statusBox, 1, 5);
        customerGrid.add(new Label("Payment:"), 2, 5); customerGrid.add(paymentBox, 3, 5);
        customerGrid.add(paidCheck, 1, 6);

        // --- Order items table ---
        TableView<Map<String, Object>> itemsEditTable = new TableView<>();
        ObservableList<Map<String, Object>> itemsData = FXCollections.observableArrayList();

        TableColumn<Map<String, Object>, String> ieColName = new TableColumn<>("Product");
        ieColName.setPrefWidth(200);
        ieColName.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty((String) cd.getValue().get("name")));

        TableColumn<Map<String, Object>, String> ieColQty = new TableColumn<>("Qty");
        ieColQty.setPrefWidth(60);
        ieColQty.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cd.getValue().get("quantity"))));

        TableColumn<Map<String, Object>, String> ieColPrice = new TableColumn<>("Unit Price");
        ieColPrice.setPrefWidth(80);
        ieColPrice.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(String.format("$%.2f", cd.getValue().get("price"))));

        TableColumn<Map<String, Object>, String> ieColTotal = new TableColumn<>("Total");
        ieColTotal.setPrefWidth(80);
        ieColTotal.setCellValueFactory(cd -> {
            int qty = (int) cd.getValue().get("quantity");
            double price = (double) cd.getValue().get("price");
            return new javafx.beans.property.SimpleStringProperty(String.format("$%.2f", qty * price));
        });

        itemsEditTable.getColumns().addAll(ieColName, ieColQty, ieColPrice, ieColTotal);
        itemsEditTable.setPrefHeight(180);

        // Load items
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT oi.id, oi.quantity, oi.price, oi.product_id, p.name FROM shop_orderitem oi " +
                 "LEFT JOIN shop_product p ON oi.product_id = p.id WHERE oi.order_id = ?")) {
            pstmt.setInt(1, order.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("item_id", rs.getInt("id"));
                row.put("product_id", rs.getInt("product_id"));
                row.put("name", rs.getString("name") != null ? rs.getString("name") : "(deleted product)");
                row.put("quantity", rs.getInt("quantity"));
                row.put("price", rs.getDouble("price"));
                itemsData.add(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }

        itemsEditTable.setItems(itemsData);

        Label orderTotalLabel = new Label(String.format("Order Total: $%.2f", order.getTotalAmount()));
        orderTotalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #10b981;");

        // --- Discount Feature ---
        ComboBox<String> discountTypeBox = new ComboBox<>(FXCollections.observableArrayList("% Percentage", "$ Flat Amount"));
        discountTypeBox.getSelectionModel().selectFirst();
        discountTypeBox.setPrefWidth(150);
        discountTypeBox.setStyle("-fx-font-size: 12px;");

        TextField discountField = new TextField("0");
        discountField.setPrefWidth(80);
        discountField.setPromptText("0");
        discountField.setStyle("-fx-font-size: 12px;");

        Label discountAmountLabel = new Label("Discount: -$0.00");
        discountAmountLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #ef4444;");

        Label subtotalLabel2 = new Label("");
        subtotalLabel2.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        Label taxLabel2 = new Label("");
        taxLabel2.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        // Helper to recalculate totals with discount
        Runnable recalculate = () -> {
            double subtotal = 0;
            for (Map<String, Object> item : itemsData) {
                subtotal += (int) item.get("quantity") * (double) item.get("price");
            }

            double discountVal = 0;
            try {
                discountVal = Double.parseDouble(discountField.getText());
            } catch (NumberFormatException ignored) {}

            double discountAmt;
            if (discountTypeBox.getValue() != null && discountTypeBox.getValue().startsWith("%")) {
                discountAmt = subtotal * (discountVal / 100.0);
            } else {
                discountAmt = discountVal;
            }
            if (discountAmt < 0) discountAmt = 0;
            if (discountAmt > subtotal) discountAmt = subtotal;

            double afterDiscount = subtotal - discountAmt;
            double tax = afterDiscount * 0.13;
            double grandTotal = afterDiscount + tax;

            subtotalLabel2.setText(String.format("Subtotal: $%.2f", subtotal));
            discountAmountLabel.setText(String.format("Discount: -$%.2f", discountAmt));
            taxLabel2.setText(String.format("Tax (13%%): $%.2f", tax));
            orderTotalLabel.setText(String.format("Order Total: $%.2f", grandTotal));
        };

        // Trigger recalculation on discount changes
        discountField.textProperty().addListener((obs, o, n) -> recalculate.run());
        discountTypeBox.valueProperty().addListener((obs, o, n) -> recalculate.run());

        // Initial calculation
        recalculate.run();

        HBox discountRow = new HBox(10, new Label("Discount:"), discountTypeBox, discountField);
        discountRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        discountRow.setStyle("-fx-padding: 8 0 0 0;");

        VBox totalBreakdown = new VBox(4, subtotalLabel2, discountAmountLabel, taxLabel2, orderTotalLabel);
        totalBreakdown.setStyle("-fx-background-color: #f0fdf4; -fx-padding: 12; -fx-background-radius: 10; -fx-border-color: #bbf7d0; -fx-border-radius: 10;");

        // Edit Qty button
        Button editQtyBtn = new Button("📝 Edit Qty");
        editQtyBtn.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
        editQtyBtn.setOnAction(e -> {
            Map<String, Object> sel = itemsEditTable.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("No Selection", "Select an item to edit."); return; }

            TextInputDialog qtyDialog = new TextInputDialog(String.valueOf(sel.get("quantity")));
            qtyDialog.setTitle("Edit Quantity");
            qtyDialog.setHeaderText("Change quantity for: " + sel.get("name"));
            qtyDialog.setContentText("New Qty:");
            qtyDialog.showAndWait().ifPresent(val -> {
                try {
                    int newQty = Integer.parseInt(val);
                    if (newQty <= 0) { showAlert("Invalid", "Quantity must be greater than 0. Use Remove to delete."); return; }
                    sel.put("quantity", newQty);
                    itemsEditTable.refresh();
                    recalculate.run();
                } catch (NumberFormatException ex) {
                    showAlert("Invalid Input", "Please enter a valid number.");
                }
            });
        });

        // Remove item button
        Button removeItemBtn = new Button("🗑 Remove Item");
        removeItemBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
        removeItemBtn.setOnAction(e -> {
            Map<String, Object> sel = itemsEditTable.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("No Selection", "Select an item to remove."); return; }
            itemsData.remove(sel);
            itemsEditTable.refresh();
            recalculate.run();
        });

        HBox itemButtons = new HBox(10, editQtyBtn, removeItemBtn);

        VBox layout = new VBox(12, new Label("CUSTOMER DETAILS"), customerGrid,
            new Separator(), new Label("ORDER ITEMS"), itemsEditTable, itemButtons,
            new Separator(), discountRow, totalBreakdown);
        layout.setStyle("-fx-padding: 10;");

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(600);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        dialog.getDialogPane().setContent(scrollPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try (Connection conn = DatabaseManager.getConnection()) {
                    conn.setAutoCommit(false);
                    try {
                        // 1. Update the order record
                        String updateOrder = "UPDATE shop_order SET first_name=?, last_name=?, email=?, phone=?, " +
                            "address=?, city=?, postal_code=?, country=?, status=?, payment_method=?, paid=?, " +
                            "total_amount=?, updated_at=? WHERE id=?";
                        PreparedStatement pstmt = conn.prepareStatement(updateOrder);
                        pstmt.setString(1, firstNameField.getText());
                        pstmt.setString(2, lastNameField.getText());
                        pstmt.setString(3, emailField.getText());
                        pstmt.setString(4, phoneField.getText());
                        pstmt.setString(5, addressField.getText());
                        pstmt.setString(6, cityField.getText());
                        pstmt.setString(7, postalField.getText());
                        pstmt.setString(8, countryField.getText());
                        pstmt.setString(9, statusBox.getValue());
                        pstmt.setString(10, paymentBox.getValue());
                        pstmt.setInt(11, paidCheck.isSelected() ? 1 : 0);

                        // Recalculate total from items with discount
                        double newTotal = 0;
                        for (Map<String, Object> item : itemsData) {
                            newTotal += (int) item.get("quantity") * (double) item.get("price");
                        }
                        // Apply discount
                        double dVal = 0;
                        try { dVal = Double.parseDouble(discountField.getText()); } catch (NumberFormatException ignored) {}
                        double dAmt;
                        if (discountTypeBox.getValue() != null && discountTypeBox.getValue().startsWith("%")) {
                            dAmt = newTotal * (dVal / 100.0);
                        } else {
                            dAmt = dVal;
                        }
                        if (dAmt < 0) dAmt = 0;
                        if (dAmt > newTotal) dAmt = newTotal;
                        newTotal = newTotal - dAmt;
                        newTotal += newTotal * 0.13; // Add tax after discount
                        pstmt.setDouble(12, newTotal);
                        pstmt.setString(13, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        pstmt.setInt(14, order.getId());
                        pstmt.executeUpdate();

                        // 2. Delete all existing items for this order
                        PreparedStatement delItems = conn.prepareStatement("DELETE FROM shop_orderitem WHERE order_id = ?");
                        delItems.setInt(1, order.getId());
                        delItems.executeUpdate();

                        // 3. Re-insert the (possibly modified) items
                        for (Map<String, Object> item : itemsData) {
                            PreparedStatement ins = conn.prepareStatement(
                                "INSERT INTO shop_orderitem (order_id, product_id, price, quantity) VALUES (?, ?, ?, ?)");
                            ins.setInt(1, order.getId());
                            ins.setInt(2, (int) item.get("product_id"));
                            ins.setDouble(3, (double) item.get("price"));
                            ins.setInt(4, (int) item.get("quantity"));
                            ins.executeUpdate();
                        }

                        conn.commit();
                        return true;
                    } catch (SQLException ex) {
                        conn.rollback();
                        ex.printStackTrace();
                        showAlert("Save Error", "Failed to save changes: " + ex.getMessage());
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        });

        dialog.showAndWait().ifPresent(saved -> {
            if (saved) {
                loadWebsiteOrders();
                loadFullSalesReport();
                loadProducts();
                showAlert("Success", "Order #" + order.getId() + " updated successfully!");
            }
        });
    }

    @FXML
    public void handleDeleteWebOrder() {
        if (selectedWebOrder == null) {
            selectedWebOrder = webOrderTable.getSelectionModel().getSelectedItem();
        }
        if (selectedWebOrder == null) {
            showAlert("No Selection", "Please select an order to delete.");
            return;
        }

        Order order = selectedWebOrder;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Order");
        confirm.setHeaderText("Delete Order #" + order.getId() + "?");
        confirm.setContentText(
            "Customer: " + order.getCustomerName() + "\n" +
            "Amount: $" + String.format("%.2f", order.getTotalAmount()) + "\n" +
            "Status: " + order.getStatus().toUpperCase() + "\n\n" +
            "This will permanently delete this order and restore product stock.\n" +
            "This action cannot be undone!"
        );

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = DatabaseManager.getConnection()) {
                    conn.setAutoCommit(false);
                    try {
                        // 1. Restore stock for each item
                        PreparedStatement getItems = conn.prepareStatement(
                            "SELECT product_id, quantity FROM shop_orderitem WHERE order_id = ?");
                        getItems.setInt(1, order.getId());
                        ResultSet rs = getItems.executeQuery();
                        while (rs.next()) {
                            PreparedStatement restoreStock = conn.prepareStatement(
                                "UPDATE shop_product SET stock = stock + ? WHERE id = ?");
                            restoreStock.setInt(1, rs.getInt("quantity"));
                            restoreStock.setInt(2, rs.getInt("product_id"));
                            restoreStock.executeUpdate();
                        }

                        // 2. Delete order items
                        PreparedStatement delItems = conn.prepareStatement(
                            "DELETE FROM shop_orderitem WHERE order_id = ?");
                        delItems.setInt(1, order.getId());
                        delItems.executeUpdate();

                        // 3. Delete the order itself
                        PreparedStatement delOrder = conn.prepareStatement(
                            "DELETE FROM shop_order WHERE id = ?");
                        delOrder.setInt(1, order.getId());
                        delOrder.executeUpdate();

                        conn.commit();
                        selectedWebOrder = null;
                        populateWebOrderDetail(null);
                        loadWebsiteOrders();
                        loadProducts();
                        loadFullSalesReport();
                        loadOrders();
                        showAlert("Deleted", "Order #" + order.getId() + " has been permanently deleted and stock restored.");

                    } catch (SQLException ex) {
                        conn.rollback();
                        ex.printStackTrace();
                        showAlert("Delete Error", "Failed to delete order: " + ex.getMessage());
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert("Database Error", "Could not connect to database.");
                }
            }
        });
    }

    // ========================
    // WHATSAPP ORDERS TAB LOGIC
    // ========================

    private void loadWhatsAppOrders() {
        if (waOrderTable == null) return;
        
        int selectedId = (selectedWhatsAppOrder != null) ? selectedWhatsAppOrder.getId() : -1;
        
        whatsappOrderList.clear();
        int pendingCount = 0;
        double potentialRevenue = 0.0;

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT * FROM shop_order WHERE payment_method = 'whatsapp' ORDER BY id DESC")) {

            while (rs.next()) {
                String status = rs.getString("status");
                Order order = new Order(
                    rs.getInt("id"),
                    rs.getString("first_name") + " " + rs.getString("last_name"),
                    rs.getDouble("total_amount"),
                    status,
                    rs.getString("created_at"),
                    rs.getString("payment_method"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("city"),
                    rs.getString("postal_code"),
                    rs.getString("country"),
                    rs.getInt("paid") == 1,
                    rs.getString("transaction_id")
                );
                whatsappOrderList.add(order);
                if ("pending".equalsIgnoreCase(status)) pendingCount++;
                potentialRevenue += order.getTotalAmount();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (selectedId != -1) {
            for (Order o : whatsappOrderList) {
                if (o.getId() == selectedId) {
                    waOrderTable.getSelectionModel().select(o);
                    break;
                }
            }
        }

        if (waTotalOrdersLabel != null) waTotalOrdersLabel.setText(String.valueOf(whatsappOrderList.size()));
        if (waPendingLabel != null) waPendingLabel.setText(String.valueOf(pendingCount));
        if (waRevenueLabel != null) waRevenueLabel.setText(String.format("$%.2f", potentialRevenue));
    }

    private void applyWhatsAppFilters() {
        if (filteredWhatsAppOrders == null) return;
        String search = waSearchField != null && waSearchField.getText() != null ? waSearchField.getText().toLowerCase() : "";

        filteredWhatsAppOrders.setPredicate(order -> {
            if (search.isEmpty()) return true;
            String lowerSearch = search.toLowerCase();
            return order.getCustomerName().toLowerCase().contains(lowerSearch) ||
                   (order.getEmail() != null && order.getEmail().toLowerCase().contains(lowerSearch)) ||
                   (order.getPhone() != null && order.getPhone().contains(lowerSearch));
        });
    }

    private void populateWhatsAppOrderDetail(Order order) {
        if (order == null) {
            if (waOrderDetailPanel != null) {
                waOrderDetailPanel.setVisible(false);
                waOrderDetailPanel.setManaged(false);
            }
            return;
        }

        if (waOrderDetailPanel != null) {
            waOrderDetailPanel.setVisible(true);
            waOrderDetailPanel.setManaged(true);
        }

        if (waDetailOrderId != null) waDetailOrderId.setText("WhatsApp Lead #" + order.getId());
        if (waDetailCustomerName != null) waDetailCustomerName.setText(order.getCustomerName());
        if (waDetailEmail != null) waDetailEmail.setText(order.getEmail());
        if (waDetailPhone != null) waDetailPhone.setText(order.getPhone());
        if (waDetailTotal != null) waDetailTotal.setText(String.format("$%.2f", order.getTotalAmount()));

        if (waDetailItemsList != null) {
            ObservableList<String> items = FXCollections.observableArrayList();
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT oi.*, p.name FROM shop_orderitem oi JOIN shop_product p ON oi.product_id = p.id WHERE oi.order_id = ?")) {
                pstmt.setInt(1, order.getId());
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    items.add(String.format("%dx %s - $%.2f", 
                        rs.getInt("quantity"), 
                        rs.getString("name"), 
                        rs.getDouble("price") * rs.getInt("quantity")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (items.isEmpty()) items.add("(No items found)");
            waDetailItemsList.setItems(items);
        }

        if (waStatusUpdateBox != null) {
            waStatusUpdateBox.getSelectionModel().select(order.getStatus());
        }
    }

    @FXML
    public void handleRefreshWhatsAppOrders() {
        loadWhatsAppOrders();
    }

    @FXML
    public void handleUpdateWhatsAppStatus() {
        if (selectedWhatsAppOrder == null) {
            selectedWhatsAppOrder = waOrderTable.getSelectionModel().getSelectedItem();
        }
        if (selectedWhatsAppOrder == null) {
            showAlert("No Selection", "Please select a lead to update.");
            return;
        }
        String newStatus = waStatusUpdateBox != null ? waStatusUpdateBox.getValue() : null;
        if (newStatus == null || newStatus.isEmpty()) {
            showAlert("No Status", "Please select a status.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "UPDATE shop_order SET status = ?, updated_at = ? WHERE id = ?")) {
            pstmt.setString(1, newStatus);
            pstmt.setString(2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            pstmt.setInt(3, selectedWhatsAppOrder.getId());
            pstmt.executeUpdate();

            showAlert("Status Updated", "WhatsApp Lead #" + selectedWhatsAppOrder.getId() + " → " + newStatus.toUpperCase());
            loadWhatsAppOrders();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to update lead status.");
        }
    }

    @FXML
    public void handlePrintWhatsAppOrderBill() {
        if (selectedWhatsAppOrder == null) {
            selectedWhatsAppOrder = waOrderTable.getSelectionModel().getSelectedItem();
        }
        if (selectedWhatsAppOrder == null) {
            showAlert("No Selection", "Please select an inquiry to print.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("          WHATSAPP QUOTE / LEAD           \n");
        sb.append("==========================================\n");
        sb.append("Order #: ").append(selectedWhatsAppOrder.getId()).append("\n");
        sb.append("Date:    ").append(selectedWhatsAppOrder.getCreatedAt()).append("\n");
        sb.append("Customer: ").append(selectedWhatsAppOrder.getCustomerName()).append("\n");
        sb.append("Phone:    ").append(selectedWhatsAppOrder.getPhone()).append("\n");
        sb.append("------------------------------------------\n");
        sb.append(String.format("%-25s %-5s %-10s\n", "ITEM", "QTY", "PRICE"));
        sb.append("------------------------------------------\n");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT oi.*, p.name FROM shop_orderitem oi JOIN shop_product p ON oi.product_id = p.id WHERE oi.order_id = ?")) {
            pstmt.setInt(1, selectedWhatsAppOrder.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                if (name.length() > 24) name = name.substring(0, 21) + "...";
                sb.append(String.format("%-25s %-5d $%-10.2f\n", 
                    name, rs.getInt("quantity"), rs.getDouble("price")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        sb.append("------------------------------------------\n");
        sb.append(String.format("%-31s $%-10.2f\n", "POTENTIAL TOTAL:", selectedWhatsAppOrder.getTotalAmount()));
        sb.append("==========================================\n");
        sb.append("       PROVISIONAL QUOTE ONLY             \n");
        sb.append("==========================================\n");

        printReceiptToPrinter(sb.toString());
    }

    // --- USER MANAGEMENT ---

    @FXML
    public void loadUsers() {
        if (!SessionManager.isAdmin()) return; // Optional security layer
        userList.clear();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, username, is_superuser, is_staff FROM auth_user WHERE is_superuser = 1 OR is_staff = 1")) {
            while (rs.next()) {
                String role = (rs.getInt("is_superuser") == 1) ? "Admin" : "Staff";
                userList.add(new User(rs.getInt("id"), rs.getString("username"), role));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadUserLogs() {
        if (!SessionManager.isAdmin()) return;
        userLogList.clear();
        String dateFilter = (logDatePicker != null && logDatePicker.getValue() != null) 
                            ? logDatePicker.getValue().toString() 
                            : java.time.LocalDate.now().toString();
                            
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM pos_staff_timesheet WHERE date = ? ORDER BY id DESC")) {
            pstmt.setString(1, dateFilter);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                userLogList.add(new StaffTimesheet(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("date"),
                    rs.getString("punch_in_time") == null ? "" : rs.getString("punch_in_time"),
                    rs.getString("punch_out_time") == null ? "" : rs.getString("punch_out_time")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleExportTimesheetCSV() {
        if (!SessionManager.isAdmin()) return;
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Timesheet CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        
        String dateStr = logDatePicker != null && logDatePicker.getValue() != null ? logDatePicker.getValue().toString() : java.time.LocalDate.now().toString();
        fileChooser.setInitialFileName("Timesheet_" + dateStr + ".csv");
        
        File file = fileChooser.showSaveDialog(mainTabPane.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("ID,Username,Date,Punch In Time,Punch Out Time");
                for (StaffTimesheet ts : userLogList) {
                    writer.printf("%d,%s,%s,%s,%s\n", 
                        ts.getId(), 
                        ts.getUsername(), 
                        ts.getDate(), 
                        ts.getPunchInTime() != null ? ts.getPunchInTime() : "", 
                        ts.getPunchOutTime() != null ? ts.getPunchOutTime() : "");
                }
                showAlert("Success", "Timesheet exported successfully to:\n" + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Export Error", "Failed to save CSV file: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleDeleteTimesheetRecord() {
        if (!SessionManager.isAdmin()) {
            showAlert("Access Denied", "Only administrators can delete timesheet records.");
            return;
        }
        StaffTimesheet selected = userLogTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a timesheet record to delete.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM pos_staff_timesheet WHERE id=?")) {
            pstmt.setInt(1, selected.getId());
            pstmt.executeUpdate();
            showAlert("Success", "Timesheet record deleted successfully!");
            loadUserLogs();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to delete timesheet record: " + e.getMessage());
        }
    }

    @FXML
    public void handleCreateUser() {
        if (!SessionManager.isAdmin()) {
            showAlert("Access Denied", "Only administrators can create users.");
            return;
        }
        String username = userUsernameField.getText();
        String password = userPasswordField.getText();
        String role = userRoleBox.getValue();
        if (username.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Validation Error", "All fields are required.");
            return;
        }
        String hash = PasswordUtil.makeDjangoPassword(password);
        if (hash == null) {
            showAlert("Error", "Failed to hash password.");
            return;
        }
        int isStaff = 1; // Both Staff and Admin have is_staff = 1
        int isSuperuser = role.equals("Admin") ? 1 : 0;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO auth_user (password, is_superuser, username, first_name, last_name, email, is_staff, is_active, date_joined) " +
                 "VALUES (?, ?, ?, '', '', '', ?, 1, ?)")) {
            pstmt.setString(1, hash);
            pstmt.setInt(2, isSuperuser);
            pstmt.setString(3, username);
            pstmt.setInt(4, isStaff);
            pstmt.setString(5, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            pstmt.executeUpdate();
            showAlert("Success", "User created successfully!");
            userUsernameField.clear();
            userPasswordField.clear();
            loadUsers();
            DatabaseManager.logUserAction(SessionManager.getLoggedInUser(), "CREATED USER: " + username);
            loadUserLogs();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to create user. Username might exist.");
        }
    }

    @FXML
    public void handleUpdateUser() {
        if (!SessionManager.isAdmin()) {
            showAlert("Access Denied", "Only administrators can update users.");
            return;
        }
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a user to update.");
            return;
        }
        String username = userUsernameField.getText();
        String password = userPasswordField.getText();
        String role = userRoleBox.getValue();
        int isStaff = 1; // Both Staff and Admin have is_staff = 1
        int isSuperuser = role.equals("Admin") ? 1 : 0;
        
        try (Connection conn = DatabaseManager.getConnection()) {
            if (!password.isEmpty()) {
                String hash = PasswordUtil.makeDjangoPassword(password);
                try (PreparedStatement pstmt = conn.prepareStatement("UPDATE auth_user SET username=?, password=?, is_superuser=?, is_staff=? WHERE id=?")) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, hash);
                    pstmt.setInt(3, isSuperuser);
                    pstmt.setInt(4, isStaff);
                    pstmt.setInt(5, selected.getId());
                    pstmt.executeUpdate();
                }
            } else {
                try (PreparedStatement pstmt = conn.prepareStatement("UPDATE auth_user SET username=?, is_superuser=?, is_staff=? WHERE id=?")) {
                    pstmt.setString(1, username);
                    pstmt.setInt(2, isSuperuser);
                    pstmt.setInt(3, isStaff);
                    pstmt.setInt(4, selected.getId());
                    pstmt.executeUpdate();
                }
            }
            showAlert("Success", "User updated successfully!");
            userUsernameField.clear();
            userPasswordField.clear();
            loadUsers();
            DatabaseManager.logUserAction(SessionManager.getLoggedInUser(), "UPDATED USER: " + username);
            loadUserLogs();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to update user.");
        }
    }

    @FXML
    public void handleDeleteUser() {
        if (!SessionManager.isAdmin()) {
            showAlert("Access Denied", "Only administrators can delete users.");
            return;
        }
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a user to delete from the table.");
            return;
        }
        deleteUserFromTable(selected);
    }
    
    private void deleteUserFromTable(User selected) {
        if (!SessionManager.isAdmin()) {
            showAlert("Access Denied", "Only administrators can delete users.");
            return;
        }
        if (selected.getUsername().equals(SessionManager.getLoggedInUser())) {
            showAlert("Action Denied", "You cannot delete yourself.");
            return;
        }
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM auth_user WHERE id=?")) {
            pstmt.setInt(1, selected.getId());
            pstmt.executeUpdate();
            showAlert("Success", "User deleted successfully!");
            userUsernameField.clear();
            userPasswordField.clear();
            loadUsers();
            DatabaseManager.logUserAction(SessionManager.getLoggedInUser(), "DELETED USER: " + selected.getUsername());
            loadUserLogs();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to delete user.");
        }
    }
    private <T> void formatColumnAsCurrency(TableColumn<T, Double> col) {
        col.setCellFactory(tc -> new TableCell<T, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", amount));
                }
            }
        });
    }
}
