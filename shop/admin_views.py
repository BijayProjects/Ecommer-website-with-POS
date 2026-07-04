from django.shortcuts import render, get_object_or_404, redirect
from django.contrib.admin.views.decorators import staff_member_required
from django.utils.decorators import method_decorator
from django.views.generic import ListView, DetailView, CreateView, UpdateView, DeleteView, TemplateView
from django.urls import reverse_lazy
from django.db.models import Sum, Count, Q
from django.utils import timezone
from datetime import timedelta, date
from django.contrib import messages
from django.http import HttpResponse
from django.template.loader import get_template
from .models import Order, Product, Category, OrderItem, NewsletterSubscriber, Expense, CashClosing

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

class AdminPOSDownloadView(StaffRequiredMixin, TemplateView):
    template_name = 'shop/admin/pos_download.html'

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


# ─────────────────────────────────────────────────────────────
# EXPENSE MANAGEMENT
# ─────────────────────────────────────────────────────────────

class AdminExpenseListView(StaffRequiredMixin, ListView):
    model = Expense
    template_name = 'shop/admin/expense_list.html'
    context_object_name = 'expenses'
    paginate_by = 20

    def get_queryset(self):
        queryset = Expense.objects.all()
        # Filter by date range if provided
        date_from = self.request.GET.get('date_from')
        date_to = self.request.GET.get('date_to')
        category = self.request.GET.get('category')

        if date_from:
            queryset = queryset.filter(date__gte=date_from)
        if date_to:
            queryset = queryset.filter(date__lte=date_to)
        if category:
            queryset = queryset.filter(category=category)
        return queryset

    def get_context_data(self, **kwargs):
        context = super().get_context_data(**kwargs)
        qs = self.get_queryset()
        context['total_expenses'] = qs.aggregate(Sum('amount'))['amount__sum'] or 0
        context['category_choices'] = Expense.CATEGORY_CHOICES
        return context


class AdminExpenseCreateView(StaffRequiredMixin, CreateView):
    model = Expense
    template_name = 'shop/admin/expense_form.html'
    fields = ['description', 'amount', 'category', 'date', 'notes']
    success_url = reverse_lazy('shop:admin_expense_list')

    def form_valid(self, form):
        form.instance.created_by = self.request.user
        messages.success(self.request, 'Expense added successfully.')
        return super().form_valid(form)


class AdminExpenseDeleteView(StaffRequiredMixin, DeleteView):
    model = Expense
    success_url = reverse_lazy('shop:admin_expense_list')

    def delete(self, request, *args, **kwargs):
        messages.success(request, 'Expense deleted.')
        return super().delete(request, *args, **kwargs)

    # Handle GET for non-JS delete (fallback)
    def get(self, request, *args, **kwargs):
        return self.delete(request, *args, **kwargs)


# ─────────────────────────────────────────────────────────────
# CASH CLOSING SYSTEM
# ─────────────────────────────────────────────────────────────

@staff_member_required
def admin_cash_closing_view(request):
    """Main cash closing form — calculate totals and save a closing record."""
    today = timezone.now().date()

    # Default period = today
    period_type = request.POST.get('period_type', request.GET.get('period_type', 'daily'))
    
    if period_type == 'daily':
        period_start = today
        period_end = today
    elif period_type == 'weekly':
        # Monday to today
        period_start = today - timedelta(days=today.weekday())
        period_end = today
    else:
        # Custom
        period_start_str = request.POST.get('period_start') or request.GET.get('period_start')
        period_end_str = request.POST.get('period_end') or request.GET.get('period_end')
        try:
            from datetime import datetime
            period_start = datetime.strptime(period_start_str, '%Y-%m-%d').date() if period_start_str else today
            period_end = datetime.strptime(period_end_str, '%Y-%m-%d').date() if period_end_str else today
        except (ValueError, TypeError):
            period_start = today
            period_end = today

    # Calculate totals
    cash_orders = Order.objects.filter(
        payment_method='cod',
        created_at__date__gte=period_start,
        created_at__date__lte=period_end
    )
    total_cash_sales = cash_orders.aggregate(Sum('total_amount'))['total_amount__sum'] or 0

    expenses_qs = Expense.objects.filter(date__gte=period_start, date__lte=period_end)
    total_expenses = expenses_qs.aggregate(Sum('amount'))['amount__sum'] or 0

    # Order count breakdown
    cash_order_count = cash_orders.count()

    if request.method == 'POST' and 'save_closing' in request.POST:
        opening_cash = request.POST.get('opening_cash', '0') or '0'
        actual_closing_cash = request.POST.get('actual_closing_cash', '0') or '0'
        notes = request.POST.get('notes', '')

        try:
            opening_cash = round(float(opening_cash), 2)
            actual_closing_cash = round(float(actual_closing_cash), 2)
        except ValueError:
            opening_cash = 0.0
            actual_closing_cash = 0.0

        expected_closing = round(float(opening_cash) + float(total_cash_sales) - float(total_expenses), 2)
        discrepancy = round(float(actual_closing_cash) - float(expected_closing), 2)

        closing = CashClosing.objects.create(
            period_type=period_type,
            period_start=period_start,
            period_end=period_end,
            opening_cash=opening_cash,
            total_cash_sales=total_cash_sales,
            total_expenses=total_expenses,
            expected_closing_cash=expected_closing,
            actual_closing_cash=actual_closing_cash,
            discrepancy=discrepancy,
            notes=notes,
            closed_by=request.user,
        )
        messages.success(request, f'Cash closing saved for {period_start} – {period_end}.')
        return redirect('shop:admin_cash_closing_print', pk=closing.pk)

    context = {
        'title': 'Cash Closing',
        'period_type': period_type,
        'period_start': period_start,
        'period_end': period_end,
        'total_cash_sales': total_cash_sales,
        'total_expenses': total_expenses,
        'cash_order_count': cash_order_count,
        'cash_orders': cash_orders.order_by('-created_at')[:20],
        'expenses': expenses_qs.order_by('-date'),
    }
    return render(request, 'shop/admin/cash_closing.html', context)


class AdminCashClosingHistoryView(StaffRequiredMixin, ListView):
    model = CashClosing
    template_name = 'shop/admin/cash_closing_history.html'
    context_object_name = 'closings'
    paginate_by = 20

    def get_context_data(self, **kwargs):
        context = super().get_context_data(**kwargs)
        context['title'] = 'Closing History'
        return context


@staff_member_required
def admin_cash_closing_print(request, pk):
    """Printable / PDF-ready cash closing report."""
    closing = get_object_or_404(CashClosing, pk=pk)

    # Fetch related orders and expenses for this period
    cash_orders = Order.objects.filter(
        payment_method='cod',
        created_at__date__gte=closing.period_start,
        created_at__date__lte=closing.period_end
    ).order_by('created_at')

    expenses = Expense.objects.filter(
        date__gte=closing.period_start,
        date__lte=closing.period_end
    ).order_by('date')

    context = {
        'closing': closing,
        'cash_orders': cash_orders,
        'expenses': expenses,
        'title': f'Cash Closing Report — {closing.period_start}',
    }
    return render(request, 'shop/admin/cash_closing_print.html', context)


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

class AdminPOSDownloadView(StaffRequiredMixin, TemplateView):
    template_name = 'shop/admin/pos_download.html'

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
