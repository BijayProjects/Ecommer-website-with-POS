from django.urls import path, include
from .views import HomeView, CategoryProductListView, DealsListView, ProductSearchView, cart_view, add_to_cart, checkout_view, process_payment, order_success, register_view, login_view, logout_view, toggle_favorite, favorites_view, ProductDetailView, NewArrivalsListView, AllProductsListView, receipt_view, receipt_pdf_view, esewa_verify, paypal_complete, user_dashboard, update_email, add_review, create_whatsapp_order
from .admin_views import (
    AdminDashboardView, AdminPOSDownloadView, AdminOrderListView, AdminOrderDetailView, AdminOrderUpdateStatusView,
    AdminProductListView, AdminProductCreateView, AdminProductUpdateView, AdminProductDeleteView,
    AdminCategoryListView, AdminCategoryCreateView, AdminCategoryUpdateView,
    AdminExpenseListView, AdminExpenseCreateView, AdminExpenseDeleteView,
    AdminCashClosingHistoryView,
    admin_cash_closing_view, admin_cash_closing_print,
)


app_name = 'shop'

urlpatterns = [
    path('', HomeView.as_view(), name='home'),
    path('product/<slug:slug>/', ProductDetailView.as_view(), name='product_detail'),
    path('new-arrivals/', NewArrivalsListView.as_view(), name='new_arrivals'),
    path('products/all/', AllProductsListView.as_view(), name='all_products'),
    path('category/<slug:slug>/', CategoryProductListView.as_view(), name='category_products'),
    path('deals/', DealsListView.as_view(), name='deals'),
    path('search/', ProductSearchView.as_view(), name='search'),
    path('cart/', cart_view, name='cart'),
    path('cart/add/<int:product_id>/', add_to_cart, name='add_to_cart'),
    path('checkout/', checkout_view, name='checkout'),
    path('checkout/process/', process_payment, name='process_payment'),
    path('checkout/esewa/verify/', esewa_verify, name='esewa_verify'),
    path('checkout/paypal/complete/', paypal_complete, name='paypal_complete'),
    path('order-success/', order_success, name='order_success'),
    path('my-orders/', user_dashboard, name='user_dashboard'),
    path('update-email/', update_email, name='update_email'),
    path('accounts/login/', login_view, name='login'),
    path('accounts/register/', register_view, name='register'),
    path('accounts/logout/', logout_view, name='logout'),
    path('favorites/', favorites_view, name='favorites'),
    path('favorites/toggle/<int:product_id>/', toggle_favorite, name='toggle_favorite'),
    path('order/receipt/<int:order_id>/', receipt_view, name='receipt'),
    path('order/receipt/<int:order_id>/pdf/', receipt_pdf_view, name='receipt_pdf'),
    path('product/review/add/<int:product_id>/', add_review, name='add_review'),
    
    # BACKOFFICE / ADMIN DASHBOARD
    path('admin-dashboard/', include([
        path('', AdminDashboardView.as_view(), name='admin_dashboard'),
        
        # POS Downloads
        path('pos-download/', AdminPOSDownloadView.as_view(), name='admin_pos_download'),
        
        # Orders
        path('orders/', AdminOrderListView.as_view(), name='admin_order_list'),
        path('orders/<int:pk>/', AdminOrderDetailView.as_view(), name='admin_order_detail'),
        path('orders/<int:pk>/status/', AdminOrderUpdateStatusView.as_view(), name='admin_order_status_update'),
        
        # Products
        path('products/', AdminProductListView.as_view(), name='admin_product_list'),
        path('products/add/', AdminProductCreateView.as_view(), name='admin_product_create'),
        path('products/<int:pk>/edit/', AdminProductUpdateView.as_view(), name='admin_product_update'),
        path('products/<int:pk>/delete/', AdminProductDeleteView.as_view(), name='admin_product_delete'),
        
        # Categories
        path('categories/', AdminCategoryListView.as_view(), name='admin_category_list'),
        path('categories/add/', AdminCategoryCreateView.as_view(), name='admin_category_create'),
        path('categories/<int:pk>/edit/', AdminCategoryUpdateView.as_view(), name='admin_category_update'),

        # Expenses
        path('expenses/', AdminExpenseListView.as_view(), name='admin_expense_list'),
        path('expenses/add/', AdminExpenseCreateView.as_view(), name='admin_expense_create'),
        path('expenses/<int:pk>/delete/', AdminExpenseDeleteView.as_view(), name='admin_expense_delete'),

        # Cash Closing
        path('cash-closing/', admin_cash_closing_view, name='admin_cash_closing'),
        path('cash-closing/history/', AdminCashClosingHistoryView.as_view(), name='admin_cash_closing_history'),
        path('cash-closing/<int:pk>/print/', admin_cash_closing_print, name='admin_cash_closing_print'),
    ])),
    path('whatsapp-order/<int:product_id>/', create_whatsapp_order, name='create_whatsapp_order'),
]
