import os
import django
import requests
from django.core.files.base import ContentFile

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'core.settings')
django.setup()

from shop.models import Product, ProductImage

IMAGE_MAP = {
    'Zenith Watch S4': [
        'https://images.unsplash.com/photo-1546868871-7041f2a55e12?auto=format&fit=crop&q=80&w=800',
        'https://images.unsplash.com/photo-1579586337278-3befd40fd17a?auto=format&fit=crop&q=80&w=800',
        'https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?auto=format&fit=crop&q=80&w=800',
    ],
    'Aero Buds Pro': [
        'https://images.unsplash.com/photo-1606220588913-b3db7e0f8216?auto=format&fit=crop&q=80&w=800',
        'https://images.unsplash.com/photo-1590658268037-6bf12165a8df?auto=format&fit=crop&q=80&w=800',
        'https://images.unsplash.com/photo-1588423771073-b8903fbb85b5?auto=format&fit=crop&q=80&w=800',
    ],
    'Tab Ultra Max': [
        'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?auto=format&fit=crop&q=80&w=800',
        'https://images.unsplash.com/photo-1561154464-82e9adf32764?auto=format&fit=crop&q=80&w=800',
        'https://images.unsplash.com/photo-1585790050230-5dd28404ccb9?auto=format&fit=crop&q=80&w=800',
    ],
    'Vocal Is Max': [
        'https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?auto=format&fit=crop&q=80&w=800',
        'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&q=80&w=800',
        'https://images.unsplash.com/photo-1583394838336-acd977736f90?auto=format&fit=crop&q=80&w=800',
    ],
    'MacBook Air M2': [
        'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&q=80&w=800',
        'https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?auto=format&fit=crop&q=80&w=800',
        'https://images.unsplash.com/photo-1515594191316-cce93617be34?auto=format&fit=crop&q=80&w=800',
    ],
    'Razer Blade 15': [
        'https://images.unsplash.com/photo-1593640408182-31c70c8268f5?auto=format&fit=crop&q=80&w=800',
        'https://images.unsplash.com/photo-1625842268584-8f3296236761?auto=format&fit=crop&q=80&w=800',
        'https://images.unsplash.com/photo-1587202372775-e229f172b9d7?auto=format&fit=crop&q=80&w=800',
    ],
    'SmartWatch Pro': [
        'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&q=80&w=800',
        'https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?auto=format&fit=crop&q=80&w=800',
        'https://images.unsplash.com/photo-1434493789847-2f02dc6ca35d?auto=format&fit=crop&q=80&w=800',
    ],
    'SoundBar Go': [
        'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&q=80&w=800',
        'https://images.unsplash.com/photo-1545454675-3531b543be5d?auto=format&fit=crop&q=80&w=800',
        'https://images.unsplash.com/photo-1583394838336-acd977736f90?auto=format&fit=crop&q=80&w=800', # fallback to speakers
    ]
}

def download_image(url):
    response = requests.get(url)
    if response.status_code == 200:
        return response.content
    return None

print("Starting to download and populate images...")

ProductImage.objects.all().delete()

for product in Product.objects.all():
    urls = IMAGE_MAP.get(product.name, [])
    if not urls:
        print(f"Skipping {product.name}, no mock URLs configured.")
        continue
        
    print(f"Processing {product.name}...")
    
    # 1. Main image
    main_img_content = download_image(urls[0])
    if main_img_content:
         filename = f"{product.slug}_main.jpg"
         product.image.save(filename, ContentFile(main_img_content), save=True)
         print(f"  - Downloaded main image")
    
    # 2. Gallery images
    for i, url in enumerate(urls[1:]):
        gallery_img_content = download_image(url)
        if gallery_img_content:
            filename = f"{product.slug}_gallery_{i}.jpg"
            img_obj = ProductImage(product=product)
            img_obj.image.save(filename, ContentFile(gallery_img_content), save=True)
            print(f"  - Downloaded gallery image {i+1}")

print("Successfully updated all images and gallery items!")
