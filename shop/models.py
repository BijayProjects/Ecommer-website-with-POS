from django.db import models
from django.utils.text import slugify

class Category(models.Model):
    name = models.CharField(max_length=100)
    slug = models.SlugField(unique=True, blank=True)
    icon = models.CharField(max_length=50, blank=True, help_text="Lucide icon name or image URL")

    def save(self, *args, **kwargs):
        if not self.slug:
            self.slug = slugify(self.name)
        super().save(*args, **kwargs)

    def __str__(self):
        return self.name

    class Meta:
        verbose_name_plural = "Categories"

class Product(models.Model):
    name = models.CharField(max_length=200)
    slug = models.SlugField(unique=True, blank=True)
    description = models.TextField()
    price = models.DecimalField(max_digits=10, decimal_places=2)
    image = models.ImageField(upload_to='products/')
    category = models.ForeignKey(Category, related_name='products', on_delete=models.CASCADE)
    is_new_arrival = models.BooleanField(default=False)
    is_top_selling = models.BooleanField(default=False)
    stock = models.IntegerField(default=10)
    shipping_info = models.CharField(max_length=200, default='Free Fast Delivery')
    warranty_info = models.CharField(max_length=200, default='2 Year Warranty')
    created_at = models.DateTimeField(auto_now_add=True)

    def save(self, *args, **kwargs):
        if not self.slug:
            self.slug = slugify(self.name)
        super().save(*args, **kwargs)

    def __str__(self):
        return self.name

    @property
    def avg_rating(self):
        reviews = self.reviews.all()
        if reviews.exists():
            from django.db.models import Avg
            return reviews.aggregate(Avg('rating'))['rating__avg']
        return 0

    @property
    def review_count(self):
        return self.reviews.count()

    @property
    def full_stars(self):
        return range(int(self.avg_rating))

    @property
    def empty_stars(self):
        return range(5 - int(self.avg_rating))

class ProductImage(models.Model):
    product = models.ForeignKey(Product, related_name='images', on_delete=models.CASCADE)
    image = models.ImageField(upload_to='products/gallery/')
    
    def __str__(self):
        return f"Image for {self.product.name}"

class Promotion(models.Model):
    title = models.CharField(max_length=200)
    subtitle = models.CharField(max_length=200)
    deal_description = models.TextField(blank=True)
    image = models.ImageField(upload_to='promotions/', blank=True, null=True)
    image_url = models.URLField(max_length=500, blank=True, null=True, help_text="External image URL (fallback if no image uploaded)")
    end_date = models.DateTimeField()
    is_active = models.BooleanField(default=True)

    def __str__(self):
        return self.title

class NewsletterSubscriber(models.Model):
    email = models.EmailField(unique=True)
    subscribed_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.email

class Cart(models.Model):
    session_key = models.CharField(max_length=40, blank=True, null=True)
    # user = models.ForeignKey(User, on_delete=models.CASCADE, null=True, blank=True) # Will add if we do auth later
    created_at = models.DateTimeField(auto_now_add=True)
    
    def __str__(self):
        return f"Cart {self.id}"

    @property
    def get_total(self):
        return sum(item.get_cost for item in self.items.all())

class CartItem(models.Model):
    cart = models.ForeignKey(Cart, related_name='items', on_delete=models.CASCADE)
    product = models.ForeignKey(Product, on_delete=models.CASCADE)
    quantity = models.PositiveIntegerField(default=1)

    def __str__(self):
        return f"{self.quantity} x {self.product.name}"

    @property
    def get_cost(self):
        return self.product.price * self.quantity

class Order(models.Model):
    user = models.ForeignKey('auth.User', on_delete=models.SET_NULL, null=True, blank=True, related_name='orders')
    first_name = models.CharField(max_length=50)
    last_name = models.CharField(max_length=50)
    email = models.EmailField()
    phone = models.CharField(max_length=20)
    
    # Shipping Details
    address = models.CharField(max_length=250)
    city = models.CharField(max_length=100)
    postal_code = models.CharField(max_length=20)
    country = models.CharField(max_length=100, default='Nepal')
    
    # Payment Method
    PAYMENT_CHOICES = [
        ('cod', 'Cash on Delivery'),
        ('esewa', 'eSewa'),
        ('paypal', 'PayPal'),
        ('card', 'Credit/Debit Card'),
        ('whatsapp', 'WhatsApp Inquiry'),
    ]
    payment_method = models.CharField(max_length=20, choices=PAYMENT_CHOICES, default='cod')
    
    # Order Status
    STATUS_CHOICES = [
        ('pending', 'Pending'),
        ('processing', 'Processing'),
        ('shipped', 'Shipped'),
        ('delivered', 'Delivered'),
        ('cancelled', 'Cancelled'),
    ]
    status = models.CharField(max_length=20, choices=STATUS_CHOICES, default='pending')
    
    # Metadata
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    paid = models.BooleanField(default=False)
    total_amount = models.DecimalField(max_digits=10, decimal_places=2, default=0.00)
    transaction_id = models.CharField(max_length=100, blank=True, null=True)

    class Meta:
        ordering = ['-created_at']

    def __str__(self):
        return f"Order {self.id}"

class OrderItem(models.Model):
    order = models.ForeignKey(Order, related_name='items', on_delete=models.CASCADE)
    product = models.ForeignKey(Product, related_name='order_items', on_delete=models.CASCADE)
    price = models.DecimalField(max_digits=10, decimal_places=2)
    quantity = models.PositiveIntegerField(default=1)

    def __str__(self):
        return str(self.id)

    def get_cost(self):
        return self.price * self.quantity

class Favorite(models.Model):
    # Added string referencing User model correctly to avoid circular imports.
    # We should import User from django.contrib.auth.models at the top.
    user = models.ForeignKey('auth.User', on_delete=models.CASCADE, related_name='favorites')
    product = models.ForeignKey(Product, on_delete=models.CASCADE, related_name='favorited_by')
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        unique_together = ('user', 'product')
        ordering = ['-created_at']

    def __str__(self):
        return f"{self.user.username} - {self.product.name}"

class Review(models.Model):
    product = models.ForeignKey(Product, on_delete=models.CASCADE, related_name='reviews')
    user = models.ForeignKey('auth.User', on_delete=models.CASCADE)
    rating = models.PositiveIntegerField(choices=[(i, str(i)) for i in range(1, 6)])
    comment = models.TextField()
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ['-created_at']
        unique_together = ('product', 'user')

    def __str__(self):
        return f"{self.user.username} - {self.product.name} - {self.rating}*"
