# Use the official lightweight Python image.
# https://hub.docker.com/_/python
FROM python:3.12-slim

# Allow statements and log messages to immediately appear in the Knative logs
ENV PYTHONUNBUFFERED=True
ENV PORT=8080

# Set up working directory
WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    gcc \
    libpq-dev \
    && rm -rf /var/lib/apt/lists/*

# Install python dependencies first to leverage Docker cache
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

# Copy local code to the container image.
COPY . ./

# Collect static files
RUN python manage.py collectstatic --noinput

# Run the web service on container startup.
CMD exec gunicorn --bind 0.0.0.0:$PORT --workers 1 --threads 8 --timeout 0 core.wsgi:application
