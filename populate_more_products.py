import os
import django
import requests
from django.core.files.base import ContentFile
import random

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'core.settings')
django.setup()

from shop.models import Category, Product, ProductImage

# Categories to create
CATEGORIES = [
    {"name": "Gaming Devices", "icon": "gamepad-2"},
    {"name": "Books", "icon": "book-open"},
    {"name": "Smart Home", "icon": "home"},
    {"name": "Cameras", "icon": "camera"},
]

NEW_PRODUCTS = [
    {
        "name": "PlayStation 5 Pro",
        "cat": "Gaming Devices",
        "price": 599.99,
        "desc": "The ultimate gaming console with lightning-fast loading, 4K graphics, and immersive haptic feedback.",
        "images": [
            "https://images.unsplash.com/photo-1622286342621-4bd786c2447c?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1606144042851-ae1f3532cf2b?auto=format&fit=crop&q=80&w=800"
        ]
    },
    {
        "name": "Xbox Series X",
        "cat": "Gaming Devices",
        "price": 499.00,
        "desc": "Power your dreams with the fastest, most powerful Xbox ever.",
        "images": [
            "https://images.unsplash.com/photo-1621259182978-fbf93132d53d?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1605901309584-818e25960b8f?auto=format&fit=crop&q=80&w=800"
        ]
    },
    {
        "name": "Nintendo Switch OLED",
        "cat": "Gaming Devices",
        "price": 349.99,
        "desc": "Play at home or on the go with a vibrant 7-inch OLED screen.",
        "images": [
            "https://images.unsplash.com/photo-1612255394593-3edc7fc239c4?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1578303512597-81e6cc155b3e?auto=format&fit=crop&q=80&w=800"
        ]
    },
    {
        "name": "The Pragmatic Programmer",
        "cat": "Books",
        "price": 39.99,
        "desc": "Journey to mastery. Your ultimate guide to writing cleaner, more robust code.",
        "images": [
            "https://images.unsplash.com/photo-1589998059171-989d887dda19?auto=format&fit=crop&q=80&w=800",
        ]
    },
    {
        "name": "Clean Code",
        "cat": "Books",
        "price": 42.50,
        "desc": "A handbook of agile software craftsmanship for any developer.",
        "images": [
            "https://images.unsplash.com/photo-1555099962-4199c345e5dd?auto=format&fit=crop&q=80&w=800",
        ]
    },
    {
        "name": "Smart Thermostat V2",
        "cat": "Smart Home",
        "price": 249.00,
        "desc": "Save energy and stay comfortable. Learns your habits and adjusts intelligently.",
        "images": [
            "https://images.unsplash.com/photo-1563461660947-507ef49e9c47?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1585433604077-d5fa8614668b?auto=format&fit=crop&q=80&w=800"
        ]
    },
    {
        "name": "Wi-Fi Security Camera",
        "cat": "Smart Home",
        "price": 129.99,
        "desc": "Crystal clear 4K 24/7 recording directly to your smartphone.",
        "images": [
            "https://images.unsplash.com/photo-1557324232-b8917d3c3dcb?auto=format&fit=crop&q=80&w=800",
        ]
    },
    {
        "name": "Smart Light Bulb Pack",
        "cat": "Smart Home",
        "price": 89.00,
        "desc": "16 million colors to customize your room's mood. Voice controlled.",
        "images": [
            "https://images.unsplash.com/photo-1550989460-0adf9ea622e2?auto=format&fit=crop&q=80&w=800",
        ]
    },
    {
        "name": "Alpha 7 IV Mirrorless",
        "cat": "Cameras",
        "price": 2499.00,
        "desc": "Unmatched 33MP sensor and 4K 60p recording.",
        "images": [
            "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1502920917128-1aa500764cbd?auto=format&fit=crop&q=80&w=800"
        ]
    },
    {
        "name": "Action Cam Pro 9",
        "cat": "Cameras",
        "price": 399.00,
        "desc": "Waterproof up to 10m. Stabilized 5K video built for the extremes.",
        "images": [
            "https://images.unsplash.com/photo-1563298723-dcfebaa392e3?auto=format&fit=crop&q=80&w=800",
        ]
    }
]

def download_image(url):
    response = requests.get(url)
    if response.status_code == 200:
        return response.content
    return None

print("Starting expansion script...")

# 1. Create missing categories
cat_map = {}
for c in CATEGORIES:
    cat, created = Category.objects.get_or_create(name=c["name"])
    if created:
        cat.icon = c["icon"]
        cat.save()
        print(f"Created category: {cat.name}")
    cat_map[c["name"]] = cat

# 2. Add products
for p_data in NEW_PRODUCTS:
    # check if exists
    if Product.objects.filter(name=p_data["name"]).exists():
        print(f"Skipping {p_data['name']}, already exists.")
        continue

    print(f"Adding new product: {p_data['name']}...")
    prod = Product.objects.create(
        name=p_data["name"],
        category=cat_map[p_data["cat"]],
        description=p_data["desc"],
        price=p_data["price"],
        is_new_arrival=True,
        is_top_selling=random.choice([True, False]),
        stock=random.randint(5, 50)
    )

    urls = p_data["images"]
    if urls:
        # main image
        main_img = download_image(urls[0])
        if main_img:
            prod.image.save(f"{prod.slug}_main.jpg", ContentFile(main_img), save=True)

        # gallery images
        for i, url in enumerate(urls[1:]):
            gal_img = download_image(url)
            if gal_img:
                img_obj = ProductImage(product=prod)
                img_obj.image.save(f"{prod.slug}_gallery_{i}.jpg", ContentFile(gal_img), save=True)

print("Expansion script complete! 10 new products added.")
