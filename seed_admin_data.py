import os
import django
import random
from datetime import timedelta
from django.utils import timezone
from decimal import Decimal

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'core.settings')
django.setup()

from django.contrib.auth.models import User
from shop.models import Product, Order, OrderItem, Category, NewsletterSubscriber

def seed_admin_dashboard():
    print("Seeding admin dashboard data...")

    # 1. Ensure a staff user exists
    if not User.objects.filter(is_staff=True).exists():
        User.objects.create_superuser('admin', 'admin@example.com', 'admin123')
        print("Created superuser: admin / admin123")
    else:
        print("Admin user already exists.")

    # 2. Get some products
    products = list(Product.objects.all())
    if not products:
        print("No products found. Please run individual product population scripts first.")
        return

    # 3. Create Newsletter subscribers if none
    if not NewsletterSubscriber.objects.exists():
        emails = ['sarah@example.com', 'mike@tech.io', 'julia@design.com', 'alex@dev.net', 'emma@gadgets.org']
        for email in emails:
            NewsletterSubscriber.objects.get_or_create(email=email)
        print(f"Created {len(emails)} newsletter subscribers.")

    # 4. Create dummy orders for the last 14 days
    status_choices = ['pending', 'processing', 'shipped', 'delivered', 'cancelled']
    payment_methods = ['cod', 'esewa', 'paypal', 'card']
    
    first_names = ['John', 'Jane', 'Alice', 'Bob', 'Charlie', 'Diana', 'Edward', 'Fiona']
    last_names = ['Doe', 'Smith', 'Johnson', 'Brown', 'Davis', 'Wilson', 'Moore', 'Taylor']
    cities = ['New York', 'Los Angeles', 'Chicago', 'Houston', 'Phoenix', 'Philadelphia', 'San Antonio', 'San Diego']

    orders_to_create = 25
    created_count = 0

    for _ in range(orders_to_create):
        # Random date in the last 14 days
        days_ago = random.randint(0, 13)
        hours_ago = random.randint(0, 23)
        order_date = timezone.now() - timedelta(days=days_ago, hours=hours_ago)
        
        status = random.choice(status_choices)
        # If delivered, it should be paid
        paid = True if status == 'delivered' else random.choice([True, False])
        
        order = Order.objects.create(
            first_name=random.choice(first_names),
            last_name=random.choice(last_names),
            email=f"user{random.randint(100, 999)}@example.com",
            phone=f"+1 {random.randint(200, 999)}-{random.randint(1000, 9999)}",
            address=f"{random.randint(100, 999)} Main St",
            city=random.choice(cities),
            postal_code=f"{random.randint(10000, 99999)}",
            country="United States",
            payment_method=random.choice(payment_methods),
            paid=paid,
            status=status,
            total_amount=0 # Will update after adding items
        )
        # Set the created_at to the random date (auto_now_add is tricky to override)
        Order.objects.filter(id=order.id).update(created_at=order_date)
        
        # Add 1-3 random items
        order_total = Decimal('0.00')
        for i in range(random.randint(1, 3)):
            product = random.choice(products)
            qty = random.randint(1, 2)
            OrderItem.objects.create(
                order=order,
                product=product,
                price=product.price,
                quantity=qty
            )
            order_total += product.price * qty
        
        order.total_amount = order_total
        order.save()
        created_count += 1

    print(f"Successfully created {created_count} dummy orders.")

if __name__ == "__main__":
    seed_admin_dashboard()
