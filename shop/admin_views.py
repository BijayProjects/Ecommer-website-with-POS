from django.shortcuts import render, get_object_or_404, redirect
from django.contrib.admin.views.decorators import staff_member_required
from django.utils.decorators import method_decorator
from django.views.generic import ListView, DetailView, CreateView, UpdateView, DeleteView, TemplateView
from django.urls import reverse_lazy
from django.db.models import Sum, Count, Q
from django.utils import timezone
from datetime import timedelta
from .models import Order, Product, Category, OrderItem, NewsletterSubscriber

# Mixin for staff-only access
class StaffRequiredMixin:
    @method_decorator(staff_member_required)
    def dispatch(self, *args, **kwargs):
        return super().dispatch(*args, **kwargs)

class AdminDashboardView(StaffRequiredMixin, TemplateView):
    template_name = 'shop/admin/dashboard.html'

    def get_context_data(self, **kwargs):
        context = super().get_context_data(**kwargs)
        
        # Stats
        context['total_revenue'] = Order.objects.filter(paid=True).aggregate(Sum('total_amount'))['total_amount__sum'] or 0
        context['total_orders'] = Order.objects.count()
        context['pending_orders'] = Order.objects.filter(status='pending').count()
        context['total_products'] = Product.objects.count()
        context['total_customers'] = Order.objects.values('email').distinct().count() or NewsletterSubscriber.objects.count()
        
        # Recent Orders
        context['recent_orders'] = Order.objects.all().order_by('-created_at')[:8]
        
        # Low Stock Products
        context['low_stock_products'] = Product.objects.filter(stock__lte=5).order_by('stock')[:5]
        
        # Sales Data for Chart (Last 7 days)
        last_7_days = []
        sales_data = []
        labels = []
        for i in range(6, -1, -1):
            date = timezone.now().date() - timedelta(days=i)
            labels.append(date.strftime('%b %d'))
            amount = Order.objects.filter(created_at__date=date, paid=True).aggregate(Sum('total_amount'))['total_amount__sum'] or 0
            sales_data.append(float(amount))
        
        context['chart_labels'] = labels
        context['chart_data'] = sales_data
        
        return context

# Order Management
class AdminOrderListView(StaffRequiredMixin, ListView):
    model = Order
    template_name = 'shop/admin/order_list.html'
    context_object_name = 'orders'
    paginate_by = 15

    def get_queryset(self):
        queryset = super().get_queryset()
        status = self.request.GET.get('status')
        if status:
            queryset = queryset.filter(status=status)
        
        search = self.request.GET.get('q')
        if search:
            queryset = queryset.filter(
                Q(id__icontains=search) | 
                Q(first_name__icontains=search) | 
                Q(last_name__icontains=search) |
                Q(email__icontains=search)
            )
        return queryset

class AdminOrderDetailView(StaffRequiredMixin, DetailView):
    model = Order
    template_name = 'shop/admin/order_detail.html'
    context_object_name = 'order'

class AdminOrderUpdateStatusView(StaffRequiredMixin, UpdateView):
    model = Order
    fields = ['status']
    
    def post(self, request, *args, **kwargs):
        order = self.get_object()
        status = request.POST.get('status')
        if status in dict(Order.STATUS_CHOICES):
            order.status = status
            order.save()
        return redirect('shop:admin_order_detail', pk=order.pk)

# Product Management
class AdminProductListView(StaffRequiredMixin, ListView):
    model = Product
    template_name = 'shop/admin/product_list.html'
    context_object_name = 'products'
    paginate_by = 15

class AdminProductCreateView(StaffRequiredMixin, CreateView):
    model = Product
    template_name = 'shop/admin/product_form.html'
    fields = ['name', 'category', 'description', 'price', 'image', 'stock', 'is_new_arrival', 'is_top_selling']
    success_url = reverse_lazy('shop:admin_product_list')

class AdminProductUpdateView(StaffRequiredMixin, UpdateView):
    model = Product
    template_name = 'shop/admin/product_form.html'
    fields = ['name', 'category', 'description', 'price', 'image', 'stock', 'is_new_arrival', 'is_top_selling']
    success_url = reverse_lazy('shop:admin_product_list')

class AdminProductDeleteView(StaffRequiredMixin, DeleteView):
    model = Product
    success_url = reverse_lazy('shop:admin_product_list')

# Category Management
class AdminCategoryListView(StaffRequiredMixin, ListView):
    model = Category
    template_name = 'shop/admin/category_list.html'
    context_object_name = 'categories'

class AdminCategoryCreateView(StaffRequiredMixin, CreateView):
    model = Category
    template_name = 'shop/admin/category_form.html'
    fields = ['name', 'icon']
    success_url = reverse_lazy('shop:admin_category_list')

class AdminCategoryUpdateView(StaffRequiredMixin, UpdateView):
    model = Category
    template_name = 'shop/admin/category_form.html'
    fields = ['name', 'icon']
    success_url = reverse_lazy('shop:admin_category_list')
