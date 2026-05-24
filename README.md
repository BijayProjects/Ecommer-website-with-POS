# E-Commerce Website + POS System

Built with Python

## Overview

This project is a complete E-Commerce Website integrated with a POS (Point of Sale) System built using Python.
The platform allows online product management, customer orders, inventory handling, and local shop billing through the POS software.

The system includes:

* E-Commerce Website
* Admin Dashboard
* POS Desktop Software
* Inventory Management
* Sales Tracking
* Order Management
* Customer Management
* Reports & Analytics

---

# Features

## Website Features

* User Registration & Login
* Product Listing
* Product Categories
* Shopping Cart
* Checkout System
* Order Tracking
* Online Payment Integration
* Responsive Design

## Admin Dashboard Features

* Manage Products
* Manage Orders
* Manage Customers
* Inventory Control
* Sales Analytics
* Download POS Software
* Staff Management
* Payment Management

## POS Software Features

* Fast Billing System
* Barcode Support
* Invoice Printing
* Inventory Sync
* Offline/Online Support
* Daily Sales Reports
* Expense Tracking
* Customer Records

---

# Technology Stack

## Backend

* Python
* Django

## Frontend

* HTML
* CSS
* JavaScript
* Tailwind CSS

## Database

* SQLite / PostgreSQL

## POS Desktop App

* Python Desktop Application

---

# Project Structure

```bash
project/
│
├── ecommerce/          # Main Django Project
├── website/            # Website Application
├── dashboard/          # Admin Dashboard
├── pos/                # POS Software
├── media/              # Uploaded Files
├── static/             # Static Files
├── templates/          # HTML Templates
├── requirements.txt
└── manage.py
```

---

# Installation Guide

## 1. Clone the Repository

```bash
git clone https://github.com/BijayProjects/Ecommer-website-with-POS.git
cd Ecommer-website-with-POS
```

---

# Website Setup (Local Development)

## 2. Create Virtual Environment

### Windows

```bash
python -m venv venv
venv\Scripts\activate
```

### Mac/Linux

```bash
python3 -m venv venv
source venv/bin/activate
```

---

## 3. Install Requirements

```bash
pip install -r requirements.txt
```

---

## 4. Run Database Migrations

```bash
python manage.py migrate
```

---

## 5. Create Superuser

```bash
python manage.py createsuperuser
```

---

## 6. Run Development Server

```bash
python manage.py runserver
```

Website URL:

```bash
http://127.0.0.1:8000/
```

Admin Dashboard URL:

```bash
http://127.0.0.1:8000/admin-dashboard/
```

---

# POS Software Setup

## Run POS Software

Go to POS folder:

```bash
cd desktop-pos
```

Run the POS application:
```bash
$env:JAVA_HOME="C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.4\jbr"; .\apache-maven-3.9.6\bin\mvn.cmd javafx:run
```

```bash
python app.py
```

OR

```bash
python main.py
```

(depending on your POS structure)

---

# How POS Connects with Website

The POS software connects with the main website database/API.

Features synced between Website and POS:

* Products
* Inventory
* Orders
* Billing
* Customers
* Sales Reports

When a sale happens in POS:

* Inventory updates automatically
* Sales records sync with dashboard
* Reports update in real-time

---

# Production / Hosting Setup

After hosting the website on a server:

## Admin Dashboard Access

Admin can log in to:

```bash
https://yourdomain.com/admin-dashboard/
```

From the dashboard admin can:

* Manage products
* Manage orders
* Track sales
* Download latest POS software
* Upload POS updates
* Manage employees

---

# Download POS Software

Inside the admin dashboard there is a section:

```bash
POS Software Download
```

Shop owners or staff can:

1. Login to dashboard
2. Download POS installer/software
3. Install on local computer
4. Connect with hosted website system

---

# POS Local Machine Requirements

## Windows

* Python 3.10+
* Internet Connection
* Printer (Optional)

## Recommended

* Barcode Scanner
* Receipt Printer

---

# Environment Variables

Create `.env` file:

```env
DEBUG=True

SECRET_KEY=your-secret-key

DATABASE_URL=your-database-url

API_URL=http://127.0.0.1:8000/api/
```

---

# Security Notes

* Change default admin password
* Use HTTPS in production
* Keep API keys secure
* Regularly backup database

---

# Future Improvements

* Mobile App Integration
* QR Payment Support
* Multi-Store Management
* AI Sales Analytics
* Cloud Backup System

---

# License

This project is licensed under the MIT License.

---

# Developer

Developed by: **[Your Name]**

Python Based E-Commerce + POS Integrated System.
