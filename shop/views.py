from django.views.generic import TemplateView, ListView, DetailView
from django.shortcuts import get_object_or_404, redirect, render
from django.db.models import Q
from .models import Product, Category, Promotion, Cart, CartItem, Favorite, Order, OrderItem, Review
from django.utils import timezone
from datetime import timedelta
from django.contrib import messages
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.forms import AuthenticationForm, UserCreationForm
from django.contrib.auth.decorators import login_required
import hmac
import hashlib
import base64
import json
from django.http import JsonResponse, HttpResponse
from django.views.decorators.csrf import csrf_exempt
from django.template.loader import get_template
from xhtml2pdf import pisa
import io
import urllib.parse

class HomeView(TemplateView):
    template_name = 'shop/home.html'

    def get_context_data(self, **kwargs):
        context = super().get_context_data(**kwargs)
        
        # New Arrivals (Newest first)
        context['new_arrivals'] = Product.objects.filter(is_new_arrival=True).order_by('-created_at')[:14]
        
        # Top Selling
        context['top_selling'] = Product.objects.filter(is_top_selling=True)[:4]
        
        # Promotions (Special Offers)
        all_promotions = list(Promotion.objects.filter(is_active=True).order_by('end_date'))
        
        if not all_promotions:
            # Fallback dummy promotions if none exists in DB
            all_promotions = [{
                'title': 'Hot Deal This Week',
                'subtitle': 'Save up to 45% Off',
                'end_date': timezone.now() + timedelta(days=2, hours=14, minutes=45),
                'image_url': 'https://images.unsplash.com/photo-1544244015-0cd4b3ff2000?auto=format&fit=crop&q=80&w=800',
            }]
        
        # Split promotions: Top 2 for Hero, rest for Offers
        context['hero_promotions'] = all_promotions[:2]
        context['promotions'] = all_promotions[2:] if len(all_promotions) > 2 else all_promotions
        
        # If there are few promotions, ensure they don't overlap awkwardly
        if len(all_promotions) == 1:
            context['promotions'] = all_promotions
            
        # Hot Deal Section Promotion
        context['promotion'] = all_promotions[0] if all_promotions else None
        
        # Featured Categories (Studio Audio, Digital Optics)
        context['featured_categories'] = [
            {
                'title': 'Studio Audio',
                'subtitle': 'Experience Pure Sonic Precision',
                'image': 'https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?auto=format&fit=crop&q=80&w=800',
                'size': 'large'
            },
            {
                'title': 'Digital Optics',
                'subtitle': 'Capture Every Moment',
                'image': 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&q=80&w=800',
                'size': 'small'
            }
        ]
        
        return context

class CategoryProductListView(ListView):
    model = Product
    template_name = 'shop/product_list.html'
    context_object_name = 'products'
    paginate_by = 12

    def get_queryset(self):
        self.category = get_object_or_404(Category, slug=self.kwargs['slug'])
        return Product.objects.filter(category=self.category).order_by('-created_at')

    def get_context_data(self, **kwargs):
        context = super().get_context_data(**kwargs)
        context['category'] = self.category
        context['title'] = self.category.name
        return context

class NewArrivalsListView(ListView):
    model = Product
    template_name = 'shop/product_list.html'
    context_object_name = 'products'
    paginate_by = 12

    def get_queryset(self):
        return Product.objects.filter(is_new_arrival=True).order_by('-created_at')

    def get_context_data(self, **kwargs):
        context = super().get_context_data(**kwargs)
        context['title'] = 'New Arrivals'
        return context

class DealsListView(ListView):
    model = Product
    template_name = 'shop/product_list.html'
    context_object_name = 'products'
    paginate_by = 12

    def get_queryset(self):
        # Items either in an active promotion or marked as top selling
        return Product.objects.filter(is_top_selling=True).order_by('-created_at')

    def get_context_data(self, **kwargs):
        context = super().get_context_data(**kwargs)
        context['title'] = 'Hot Deals'
        context['is_deals_page'] = True
        return context

class AllProductsListView(ListView):
    model = Product
    template_name = 'shop/product_list.html'
    context_object_name = 'products'
    paginate_by = 12

    def get_queryset(self):
        return Product.objects.all().order_by('-created_at')

    def get_context_data(self, **kwargs):
        context = super().get_context_data(**kwargs)
        context['title'] = 'All Products'
        return context

class ProductSearchView(ListView):
    model = Product
    template_name = 'shop/product_list.html'
    context_object_name = 'products'
    paginate_by = 12

    def get_queryset(self):
        query = self.request.GET.get('q', '')
        if query:
            return Product.objects.filter(
                Q(name__icontains=query) | 
                Q(description__icontains=query) | 
                Q(category__name__icontains=query)
            ).distinct()
        return Product.objects.none()

    def get_context_data(self, **kwargs):
        context = super().get_context_data(**kwargs)
        query = self.request.GET.get('q', '')
        context['title'] = f"Search Results for '{query}'" if query else "Search Products"
        context['search_query'] = query
        context['is_search_page'] = True
        return context

class ProductDetailView(DetailView):
    model = Product
    template_name = 'shop/product_detail.html'
    context_object_name = 'product'

    def get_context_data(self, **kwargs):
        context = super().get_context_data(**kwargs)
        context['title'] = self.object.name
        # Fetch some related or popular products to show at the bottom
        context['related_products'] = Product.objects.filter(category=self.object.category).exclude(id=self.object.id)[:4]
        
        # Reviews
        reviews = self.object.reviews.all()
        context['reviews'] = reviews
        context['reviews_count'] = reviews.count()
        
        # Average rating
        if context['reviews_count'] > 0:
            avg_rating = sum(r.rating for r in reviews) / context['reviews_count']
            context['avg_rating'] = round(avg_rating, 1)
            context['full_stars'] = range(int(avg_rating))
            context['empty_stars'] = range(5 - int(avg_rating))
        else:
            context['avg_rating'] = 0
            context['full_stars'] = []
            context['empty_stars'] = range(5)

        # Check if user can review (must be logged in and have purchased the product)
        can_review = False
        if self.request.user.is_authenticated:
            # Check if they already reviewed
            already_reviewed = Review.objects.filter(product=self.object, user=self.request.user).exists()
            if not already_reviewed:
                # Check if they bought it
                purchased = OrderItem.objects.filter(
                    order__user=self.request.user, 
                    product=self.object,
                    order__status='delivered' # Usually only delivered products can be reviewed
                ).exists()
                
                # Fallback check for email if order.user wasn't set (for older orders)
                if not purchased and self.request.user.email:
                    purchased = OrderItem.objects.filter(
                        order__email__iexact=self.request.user.email,
                        product=self.object,
                        order__status='delivered'
                    ).exists()
                
                can_review = purchased
        
        context['can_review'] = can_review
        return context

# CART VIEWS ----------------------------------------------------

def _get_or_create_cart(request):
    if not request.session.session_key:
        request.session.create()
    session_key = request.session.session_key
    cart, created = Cart.objects.get_or_create(session_key=session_key)
    return cart

@login_required
def add_to_cart(request, product_id):
    if request.method == 'POST':
        product = get_object_or_404(Product, id=product_id)
        cart = _get_or_create_cart(request)
        
        cart_item, created = CartItem.objects.get_or_create(cart=cart, product=product)
        if not created:
            cart_item.quantity += 1
            cart_item.save()
            messages.success(request, f'Increased quantity of {product.name} in your cart!')
        else:
            messages.success(request, f'Added {product.name} to your cart!')
            
    # Redirect back to where they came from
    return redirect(request.META.get('HTTP_REFERER', '/'))

def cart_view(request):
    cart = _get_or_create_cart(request)
    context = {
        'cart': cart,
        'title': 'Your Cart'
    }
    return render(request, 'shop/cart.html', context)

# PAYMENT HELPERS -----------------------------------------------

def generate_esewa_signature(secret_key, total_amount, transaction_uuid, product_code):
    message = f"total_amount={total_amount},transaction_uuid={transaction_uuid},product_code={product_code}"
    key = secret_key.encode('utf-8')
    msg = message.encode('utf-8')
    hmac_sha256 = hmac.new(key, msg, hashlib.sha256)
    digest = hmac_sha256.digest()
    signature = base64.b64encode(digest).decode('utf-8')
    return signature

# CHECKOUT VIEWS ------------------------------------------------

def checkout_view(request):
    cart = _get_or_create_cart(request)
    if cart.get_total == 0:
        messages.error(request, 'Your cart is empty.')
        return redirect('shop:cart')
        
    context = {
        'cart': cart,
        'title': 'Secure Checkout'
    }
    return render(request, 'shop/checkout.html', context)

def process_payment(request):
    if request.method == 'POST':
        cart = _get_or_create_cart(request)
        if cart.get_total == 0:
            return redirect('shop:cart')
            
        # Retrieve form data
        first_name = request.POST.get('first_name')
        last_name = request.POST.get('last_name')
        email = request.POST.get('email')
        phone = request.POST.get('phone')
        address = request.POST.get('address')
        city = request.POST.get('city')
        postal_code = request.POST.get('postal_code')
        country = request.POST.get('country')
        payment_method = request.POST.get('payment_method', 'cod')
        
        # 1. Create the Order (Initially unpaid)
        from .models import Order, OrderItem
        order = Order.objects.create(
            user=request.user if request.user.is_authenticated else None,
            first_name=first_name,
            last_name=last_name,
            email=email,
            phone=phone,
            address=address,
            city=city,
            postal_code=postal_code,
            country=country,
            payment_method=payment_method,
            paid=False,
            total_amount=cart.get_total
        )
        
        # 2. Move Cart Items to Order Items
        for item in cart.items.all():
            OrderItem.objects.create(
                order=order,
                product=item.product,
                price=item.product.price,
                quantity=item.quantity
            )
            
        # 3. Handle Flow based on Payment Method
        if payment_method == 'esewa':
            secret_key = "8gBm/:&EnhH.1/q"
            transaction_uuid = f"{order.id}-{int(timezone.now().timestamp())}"
            signature = generate_esewa_signature(secret_key, order.total_amount, transaction_uuid, "EPAYTEST")
            
            context = {
                'order': order,
                'amount': order.total_amount,
                'transaction_uuid': transaction_uuid,
                'product_code': 'EPAYTEST',
                'signature': signature,
                'success_url': request.build_absolute_uri('/checkout/esewa/verify/'),
                'failure_url': request.build_absolute_uri('/checkout/'),
            }
            # Note: We don't delete cart yet, we do it after successful validation
            return render(request, 'shop/esewa_redirect.html', context)
            
        elif payment_method == 'paypal':
            # For PayPal, we use a robust server-side redirection to bypass client-side blocks
            context = {
                'order': order,
                'amount': order.total_amount,
                'business_email': 'merchant@curator-electronics.com', # Placeholder for sandbox
                'item_name': f'Order #{order.id} from Curator Electronics',
                'return_url': request.build_absolute_uri('/order-success/'),
                'cancel_url': request.build_absolute_uri('/checkout/'),
            }
            return render(request, 'shop/paypal_redirect.html', context)

        elif payment_method == 'whatsapp':
            # Construct message for WhatsApp
            items_str = ""
            for item in order.items.all():
                items_str += f"- {item.product.name} x {item.quantity} (${item.get_cost()})\n"
            
            message = (
                f"Hello Curator Electronics! 👋\n\n"
                f"I'd like to place an order via WhatsApp.\n\n"
                f"🛍️ *Order ID:* #{order.id}\n"
                f"👤 *Customer:* {order.first_name} {order.last_name}\n"
                f"📞 *Phone:* {order.phone}\n"
                f"📍 *Address:* {order.address}, {order.city}\n"
                f"💰 *Total Amount:* ${order.total_amount}\n\n"
                f"*Items:*\n{items_str}\n"
                f"Please confirm my order. Thank you!"
            )
            
            encoded_message = urllib.parse.quote(message)
            whatsapp_url = f"https://wa.me/9764634072?text={encoded_message}"
            
            # Clear cart as the order is now recorded as a WhatsApp inquiry
            cart.delete()
            request.session['order_id'] = order.id
            return redirect(whatsapp_url)

        # Cash on Delivery or simulated Success
        cart.delete()
        request.session['order_id'] = order.id
        messages.success(request, 'Your order has been placed successfully!')
        return redirect('shop:order_success')
            
    return redirect('shop:checkout')

def esewa_verify(request):
    encoded_data = request.GET.get('data')
    if not encoded_data:
        return redirect('shop:home')
    
    try:
        decoded_data_str = base64.b64decode(encoded_data).decode('utf-8')
        decoded_data = json.loads(decoded_data_str)
        
        if decoded_data.get('status') == 'COMPLETE':
            order_id_full = decoded_data.get('transaction_uuid')
            order_id = order_id_full.split('-')[0]
            order = get_object_or_404(Order, id=order_id)
            
            order.paid = True
            order.transaction_id = decoded_data.get('transaction_code')
            order.save()
            
            # Clear cart on success
            cart = _get_or_create_cart(request)
            cart.delete()
            
            request.session['order_id'] = order.id
            messages.success(request, 'Payment successful via eSewa!')
            return redirect('shop:order_success')
    except Exception as e:
        messages.error(request, f'Error verifying payment: {str(e)}')
    
    messages.error(request, 'eSewa payment failed or was cancelled.')
    return redirect('shop:checkout')

@csrf_exempt
def paypal_complete(request):
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
            order_id = data.get('order_id')
            transaction_id = data.get('transaction_id')
            
            order = get_object_or_404(Order, id=order_id)
            order.paid = True
            order.transaction_id = transaction_id
            order.save()
            
            # Clear cart
            cart = _get_or_create_cart(request)
            cart.delete()
            
            request.session['order_id'] = order.id
            return JsonResponse({'status': 'success'})
        except Exception as e:
            return JsonResponse({'status': 'error', 'message': str(e)}, status=400)
    return JsonResponse({'status': 'error'}, status=400)

def order_success(request):
    order_id = request.session.get('order_id')
    if not order_id:
        return redirect('shop:home')
        
    from .models import Order
    order = get_object_or_404(Order, id=order_id)
    
    context = {
        'order': order,
        'title': 'Order Successful'
    }
    return render(request, 'shop/order_success.html', context)

# AUTH VIEWS ---------------------------------------------------

def register_view(request):
    if request.user.is_authenticated:
        return redirect('shop:home')
    if request.method == 'POST':
        form = UserCreationForm(request.POST)
        if form.is_valid():
            user = form.save()
            login(request, user)
            messages.success(request, f'Welcome, {user.username}! Your account has been created.')
            return redirect('shop:home')
    else:
        form = UserCreationForm()
    return render(request, 'registration/register.html', {'form': form})

def login_view(request):
    if request.user.is_authenticated:
        return redirect('shop:home')
    if request.method == 'POST':
        form = AuthenticationForm(request, data=request.POST)
        if form.is_valid():
            user = form.get_user()
            login(request, user)
            messages.success(request, f'Welcome back, {user.username}!')
            next_url = request.GET.get('next') or request.POST.get('next') or '/'
            return redirect(next_url)
        else:
            messages.error(request, 'Invalid username or password. Please try again.')
    else:
        form = AuthenticationForm()
    return render(request, 'registration/login.html', {'form': form, 'next': request.GET.get('next', '/')})

def logout_view(request):
    logout(request)
    return redirect('shop:home')

# FAVORITES VIEWS ----------------------------------------------

@login_required
def toggle_favorite(request, product_id):
    if request.method == 'POST':
        product = get_object_or_404(Product, id=product_id)
        favorite, created = Favorite.objects.get_or_create(user=request.user, product=product)
        
        if not created:
            favorite.delete()
            messages.success(request, f'Removed {product.name} from your favorites.')
        else:
            messages.success(request, f'Added {product.name} to your favorites!')
            
    return redirect(request.META.get('HTTP_REFERER', '/'))

@login_required
def favorites_view(request):
    favorites = Favorite.objects.filter(user=request.user).select_related('product')
    products = [fav.product for fav in favorites]
    context = {
        'products': products,
        'title': 'Your Favorites',
        'is_favorites_page': True,
    }
    return render(request, 'shop/favorites.html', context)

@login_required
def user_dashboard(request):
    # Build a broad query to find orders belonging to this user
    # Strategy: match by email OR by username in customer name fields
    user = request.user
    query = Q()
    
    # Match by email if user has one set
    if user.email:
        query |= Q(email__iexact=user.email)
    
    # Match by first + last name if set
    if user.first_name and user.last_name:
        query |= Q(first_name__iexact=user.first_name, last_name__iexact=user.last_name)
    
    # Match by username appearing in first_name or last_name
    if user.username:
        query |= Q(first_name__iexact=user.username)
        query |= Q(last_name__iexact=user.username)
    
    if query:
        orders = Order.objects.filter(query).distinct().order_by('-created_at')
    else:
        orders = Order.objects.none()
    
    # Stats
    total_orders = orders.count()
    pending_orders = orders.filter(status='pending').count()
    delivered_orders = orders.filter(status='delivered').count()
    
    # Find products from delivered orders that haven't been reviewed
    delivered_orders_query = orders.filter(status='delivered')
    reviewed_product_ids = Review.objects.filter(user=user).values_list('product_id', flat=True)
    
    pending_review_product = None
    # We look for the most recent unreviewed product from delivered orders
    for order in delivered_orders_query:
        for item in order.items.all():
            if item.product.id not in reviewed_product_ids:
                pending_review_product = item.product
                break
        if pending_review_product:
            break
            
    context = {
        'orders': orders,
        'total_orders': total_orders,
        'pending_orders': pending_orders,
        'delivered_orders': delivered_orders,
        'pending_review_product': pending_review_product,
        'title': 'My Dashboard',
    }
    return render(request, 'shop/user_dashboard.html', context)

@login_required
def update_email(request):
    if request.method == 'POST':
        email = request.POST.get('email', '').strip()
        if email:
            request.user.email = email
            request.user.save()
            messages.success(request, f'Email updated to {email}!')
        else:
            messages.error(request, 'Please enter a valid email address.')
    return redirect('shop:user_dashboard')

def receipt_view(request, order_id):
    # Retrieve order or return 404
    order = get_object_or_404(Order, id=order_id)
    
    context = {
        'order': order,
        'title': f'Receipt - Order #{order.id:05d}',
    }
    return render(request, 'shop/receipt.html', context)

def receipt_pdf_view(request, order_id):
    order = get_object_or_404(Order, id=order_id)
    template_path = 'shop/receipt_pdf.html'
    context = {'order': order}
    
    # Create a Django response object, and specify content_type as pdf
    response = HttpResponse(content_type='application/pdf')
    response['Content-Disposition'] = f'attachment; filename="receipt_{order.id:05d}.pdf"'
    
    # find the template and render it.
    template = get_template(template_path)
    html = template.render(context)

    # create a pdf
    pisa_status = pisa.CreatePDF(html, dest=response)
    
    # if error then show some funny view
    if pisa_status.err:
       return HttpResponse('We had some errors <pre>' + html + '</pre>')
    return response
@login_required
def add_review(request, product_id):
    if request.method == 'POST':
        product = get_object_or_404(Product, id=product_id)
        
        # Security check: did they actually buy it?
        purchased = OrderItem.objects.filter(
            order__user=request.user, 
            product=product,
            order__status='delivered'
        ).exists()
        
        if not purchased and request.user.email:
            purchased = OrderItem.objects.filter(
                order__email__iexact=request.user.email,
                product=product,
                order__status='delivered'
            ).exists()
            
        if not purchased:
            messages.error(request, "You can only review products you have purchased and received.")
            return redirect(request.META.get('HTTP_REFERER', '/'))
            
        # Check for existing review
        if Review.objects.filter(product=product, user=request.user).exists():
            messages.error(request, "You have already reviewed this product.")
            return redirect(request.META.get('HTTP_REFERER', '/'))
            
        rating = request.POST.get('rating')
        comment = request.POST.get('comment')
        
        if rating and comment:
            Review.objects.create(
                product=product,
                user=request.user,
                rating=rating,
                comment=comment
            )
            messages.success(request, "Thank you for your review!")
        else:
            messages.error(request, "Please provide both a rating and a comment.")
            
    return redirect(request.META.get('HTTP_REFERER', '/'))

@csrf_exempt
def create_whatsapp_order(request, product_id):
    if request.method == 'POST':
        product = get_object_or_404(Product, id=product_id)
        
        # Create a placeholder order for POS tracking
        order = Order.objects.create(
            first_name="WhatsApp",
            last_name="Customer",
            email="whatsapp@pos.com",
            phone="WhatsApp",
            address="WhatsApp Chat Inquiry",
            city="Online",
            payment_method='whatsapp',
            status='pending',
            total_amount=product.price
        )
        
        # Create OrderItem
        OrderItem.objects.create(
            order=order,
            product=product,
            price=product.price,
            quantity=1
        )
        
        return JsonResponse({'status': 'success', 'order_id': order.id})
    return JsonResponse({'status': 'error', 'message': 'Invalid request'}, status=400)
