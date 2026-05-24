from .models import Category, Cart

def categories_processor(request):
    return {
        'nav_categories': Category.objects.all()
    }

def cart_processor(request):
    cart_item_count = 0
    if request.session.session_key:
        cart = Cart.objects.filter(session_key=request.session.session_key).first()
        if cart:
            cart_item_count = sum(item.quantity for item in cart.items.all())
    return {
        'cart_item_count': cart_item_count
    }

def favorite_processor(request):
    favorite_product_ids = []
    if request.user.is_authenticated:
        favorite_product_ids = list(request.user.favorites.values_list('product_id', flat=True))
    return {
        'favorite_product_ids': favorite_product_ids
    }
