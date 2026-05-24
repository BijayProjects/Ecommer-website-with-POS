#!/bin/bash

# Install dependencies
echo "Installing dependencies..."
pip install -r requirements.txt

# Run migrations (Optional: Vercel DB migrations)
# echo "Running migrations..."
# python3.12 manage.py migrate --noinput

# Collect Static Files
echo "Collecting static files..."
python3.12 manage.py collectstatic --noinput --clear

echo "Build process completed!"
