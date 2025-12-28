# طراحی API‌های RESTful سیستم مدیریت کتابخانه دانشگاه

## مقدمه

این سند طراحی نسخه اولیه API‌های RESTful برای سیستم مدیریت کتابخانه دانشگاه را ارائه می‌دهد. تمام endpoint‌ها از مسیر پایه `/api` شروع می‌شوند.

---

## ۱. احراز هویت (Authentication)

### ۱.۱. ثبت‌نام دانشجو

**Endpoint:** `POST /api/auth/register`

**توضیحات:** ثبت‌نام دانشجوی جدید در سیستم

**بدنه درخواست (Request Body):**
```json
{
  "username": "string",
  "password": "string",
  "name": "string",
  "email": "string"
}
```

**پاسخ موفق (200 OK):**
```json
{
  "success": true,
  "message": "دانشجو با موفقیت ثبت‌نام شد",
  "data": {
    "username": "string",
    "name": "string",
    "email": "string"
  }
}
```

**پاسخ خطا (400 Bad Request):**
```json
{
  "success": false,
  "message": "نام کاربری یا ایمیل تکراری است",
  "errors": ["username already exists"]
}
```

---

### ۱.۲. ورود به سیستم

**Endpoint:** `POST /api/auth/login`

**توضیحات:** ورود به سیستم برای همه کاربران (دانشجو، کارمند، مدیر)

**بدنه درخواست (Request Body):**
```json
{
  "username": "string",
  "password": "string"
}
```

**پاسخ موفق (200 OK):**
```json
{
  "success": true,
  "message": "ورود موفق",
  "data": {
    "token": "jwt_token_string",
    "userType": "STUDENT|EMPLOYEE|ADMIN",
    "username": "string",
    "name": "string"
  }
}
```

**پاسخ خطا (401 Unauthorized):**
```json
{
  "success": false,
  "message": "نام کاربری یا رمز عبور اشتباه است"
}
```

---

### ۱.۳. تغییر رمز عبور

**Endpoint:** `POST /api/auth/change-password`

**توضیحات:** تغییر رمز عبور برای کارمند و مدیر (نیاز به احراز هویت)

**Headers:**
```
Authorization: Bearer {token}
```

**بدنه درخواست (Request Body):**
```json
{
  "currentPassword": "string",
  "newPassword": "string"
}
```

**پاسخ موفق (200 OK):**
```json
{
  "success": true,
  "message": "رمز عبور با موفقیت تغییر کرد"
}
```

**پاسخ خطا (400 Bad Request):**
```json
{
  "success": false,
  "message": "رمز عبور فعلی اشتباه است"
}
```

---

## ۲. کتاب‌ها (Books)

### ۲.۱. دریافت لیست کتاب‌ها

**Endpoint:** `GET /api/books`

**توضیحات:** دریافت لیست کتاب‌ها با قابلیت جستجو و فیلتر

**Query Parameters:**
- `title` (optional): جستجو بر اساس عنوان
- `author` (optional): فیلتر بر اساس نویسنده
- `year` (optional): فیلتر بر اساس سال انتشار
- `isbn` (optional): جستجو بر اساس ISBN

**مثال:** `/api/books?title=Java&author=Bloch`

**پاسخ موفق (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "isbn": "string",
      "title": "string",
      "author": "string",
      "year": 2020
    }
  ],
  "total": 10
}
```

---

### ۲.۲. دریافت جزئیات یک کتاب

**Endpoint:** `GET /api/books/{id}`

**توضیحات:** دریافت اطلاعات کامل یک کتاب بر اساس ISBN یا ID

**Path Parameters:**
- `id`: شناسه یا ISBN کتاب

**پاسخ موفق (200 OK):**
```json
{
  "success": true,
  "data": {
    "isbn": "string",
    "title": "string",
    "author": "string",
    "year": 2020
  }
}
```

**پاسخ خطا (404 Not Found):**
```json
{
  "success": false,
  "message": "کتاب یافت نشد"
}
```

---

### ۲.۳. ایجاد کتاب جدید

**Endpoint:** `POST /api/books`

**توضیحات:** ایجاد کتاب جدید (فقط کارمند)

**Headers:**
```
Authorization: Bearer {token}
```

**بدنه درخواست (Request Body):**
```json
{
  "isbn": "string",
  "title": "string",
  "author": "string",
  "year": 2020
}
```

**پاسخ موفق (201 Created):**
```json
{
  "success": true,
  "message": "کتاب با موفقیت اضافه شد",
  "data": {
    "isbn": "string",
    "title": "string",
    "author": "string",
    "year": 2020
  }
}
```

**پاسخ خطا (403 Forbidden):**
```json
{
  "success": false,
  "message": "شما دسترسی لازم برای این عملیات را ندارید"
}
```

---

### ۲.۴. به‌روزرسانی اطلاعات کتاب

**Endpoint:** `PUT /api/books/{id}`

**توضیحات:** به‌روزرسانی اطلاعات یک کتاب (فقط کارمند)

**Headers:**
```
Authorization: Bearer {token}
```

**Path Parameters:**
- `id`: شناسه یا ISBN کتاب

**بدنه درخواست (Request Body):**
```json
{
  "title": "string",
  "author": "string",
  "year": 2020
}
```

**پاسخ موفق (200 OK):**
```json
{
  "success": true,
  "message": "اطلاعات کتاب با موفقیت به‌روزرسانی شد",
  "data": {
    "isbn": "string",
    "title": "string",
    "author": "string",
    "year": 2020
  }
}
```

---

### ۲.۵. جستجوی پیشرفته کتاب

**Endpoint:** `GET /api/books/search`

**توضیحات:** جستجوی پیشرفته کتاب بر اساس عنوان، نویسنده و سال

**Query Parameters:**
- `title` (optional): جستجو در عنوان
- `author` (optional): جستجو در نام نویسنده
- `year` (optional): فیلتر بر اساس سال
- `yearFrom` (optional): فیلتر از سال
- `yearTo` (optional): فیلتر تا سال

**مثال:** `/api/books/search?title=Java&author=Bloch&yearFrom=2010&yearTo=2020`

**پاسخ موفق (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "isbn": "string",
      "title": "string",
      "author": "string",
      "year": 2020
    }
  ],
  "total": 5
}
```

---

## ۳. امانت کتاب (Borrowing)

### ۳.۱. ثبت درخواست امانت جدید

**Endpoint:** `POST /api/borrow/request`

**توضیحات:** ثبت درخواست امانت کتاب (فقط دانشجو)

**Headers:**
```
Authorization: Bearer {token}
```

**بدنه درخواست (Request Body):**
```json
{
  "bookIsbn": "string",
  "startDate": "2024-01-01",
  "endDate": "2024-01-15"
}
```

**پاسخ موفق (201 Created):**
```json
{
  "success": true,
  "message": "درخواست امانت با موفقیت ثبت شد",
  "data": {
    "requestId": "string",
    "book": {
      "isbn": "string",
      "title": "string"
    },
    "startDate": "2024-01-01",
    "endDate": "2024-01-15",
    "status": "PENDING"
  }
}
```

---

### ۳.۲. مشاهده درخواست‌های در انتظار تایید

**Endpoint:** `GET /api/borrow/requests/pending`

**توضیحات:** مشاهده لیست درخواست‌های در انتظار تایید (فقط کارمند)

**Headers:**
```
Authorization: Bearer {token}
```

**پاسخ موفق (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "requestId": "string",
      "student": {
        "username": "string",
        "name": "string"
      },
      "book": {
        "isbn": "string",
        "title": "string",
        "author": "string"
      },
      "startDate": "2024-01-01",
      "endDate": "2024-01-15"
    }
  ],
  "total": 5
}
```

---

### ۳.۳. تایید درخواست امانت

**Endpoint:** `PUT /api/borrow/requests/{id}/approve`

**توضیحات:** تایید یک درخواست امانت (فقط کارمند)

**Headers:**
```
Authorization: Bearer {token}
```

**Path Parameters:**
- `id`: شناسه درخواست

**پاسخ موفق (200 OK):**
```json
{
  "success": true,
  "message": "درخواست امانت تایید شد",
  "data": {
    "requestId": "string",
    "status": "APPROVED",
    "loanRecordId": "string"
  }
}
```

---

### ۳.۴. رد درخواست امانت

**Endpoint:** `PUT /api/borrow/requests/{id}/reject`

**توضیحات:** رد یک درخواست امانت (فقط کارمند)

**Headers:**
```
Authorization: Bearer {token}
```

**Path Parameters:**
- `id`: شناسه درخواست

**بدنه درخواست (Request Body - اختیاری):**
```json
{
  "reason": "string"
}
```

**پاسخ موفق (200 OK):**
```json
{
  "success": true,
  "message": "درخواست امانت رد شد",
  "data": {
    "requestId": "string",
    "status": "REJECTED"
  }
}
```

---

### ۳.۵. ثبت بازگرداندن کتاب

**Endpoint:** `PUT /api/borrow/{id}/return`

**توضیحات:** ثبت بازگرداندن کتاب (فقط کارمند)

**Headers:**
```
Authorization: Bearer {token}
```

**Path Parameters:**
- `id`: شناسه رکورد امانت (LoanRecord ID)

**پاسخ موفق (200 OK):**
```json
{
  "success": true,
  "message": "بازگرداندن کتاب ثبت شد",
  "data": {
    "loanRecordId": "string",
    "returnedAt": "2024-01-16",
    "delayed": false
  }
}
```

---

## ۴. مدیریت دانشجویان (Students)

### ۴.۱. دریافت پروفایل دانشجو

**Endpoint:** `GET /api/students/{id}`

**توضیحات:** دریافت اطلاعات پروفایل دانشجو

**Path Parameters:**
- `id`: شناسه یا نام کاربری دانشجو

**Headers (اختیاری برای مشاهده اطلاعات خود):**
```
Authorization: Bearer {token}
```

**پاسخ موفق (200 OK):**
```json
{
  "success": true,
  "data": {
    "username": "string",
    "name": "string",
    "email": "string",
    "active": true
  }
}
```

---

### ۴.۲. فعال/غیرفعال کردن دانشجو

**Endpoint:** `PUT /api/students/{id}/status`

**توضیحات:** فعال یا غیرفعال کردن حساب دانشجو (فقط کارمند)

**Headers:**
```
Authorization: Bearer {token}
```

**Path Parameters:**
- `id`: شناسه یا نام کاربری دانشجو

**بدنه درخواست (Request Body):**
```json
{
  "active": true
}
```

**پاسخ موفق (200 OK):**
```json
{
  "success": true,
  "message": "وضعیت دانشجو به‌روزرسانی شد",
  "data": {
    "username": "string",
    "active": true
  }
}
```

---

### ۴.۳. مشاهده تاریخچه امانت‌های دانشجو

**Endpoint:** `GET /api/students/{id}/borrow-history`

**توضیحات:** مشاهده تاریخچه کامل امانت‌های یک دانشجو (فقط کارمند)

**Headers:**
```
Authorization: Bearer {token}
```

**Path Parameters:**
- `id`: شناسه یا نام کاربری دانشجو

**Query Parameters (اختیاری):**
- `returned` (optional): فیلتر بر اساس وضعیت بازگرداندن (true/false)

**پاسخ موفق (200 OK):**
```json
{
  "success": true,
  "data": {
    "student": {
      "username": "string",
      "name": "string"
    },
    "loans": [
      {
        "loanRecordId": "string",
        "book": {
          "isbn": "string",
          "title": "string",
          "author": "string"
        },
        "startDate": "2024-01-01",
        "endDate": "2024-01-15",
        "returned": true,
        "returnedAt": "2024-01-16",
        "delayed": false
      }
    ],
    "totalLoans": 10,
    "activeLoans": 2
  }
}
```

---

## ۵. گزارش‌ها و آمار (Reports & Statistics)

### ۵.۱. آمار خلاصه

**Endpoint:** `GET /api/stats/summary`

**توضیحات:** دریافت آمار خلاصه سیستم (تعداد دانشجو، کتاب، امانت) - قابل دسترسی برای مهمان، کارمند و مدیر

**Headers (اختیاری):**
```
Authorization: Bearer {token}
```

**پاسخ موفق (200 OK):**
```json
{
  "success": true,
  "data": {
    "totalStudents": 100,
    "totalBooks": 500,
    "totalLoans": 250,
    "activeLoans": 30,
    "pendingRequests": 5
  }
}
```

---

### ۵.۲. آمار پیشرفته امانت‌ها

**Endpoint:** `GET /api/stats/borrows`

**توضیحات:** دریافت آمار پیشرفته در مورد امانت‌ها (فقط مدیر)

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters (اختیاری):**
- `startDate` (optional): تاریخ شروع بازه
- `endDate` (optional): تاریخ پایان بازه

**پاسخ موفق (200 OK):**
```json
{
  "success": true,
  "data": {
    "totalRequests": 300,
    "totalLoans": 250,
    "averageLoanDays": 12.5,
    "totalReturned": 220,
    "totalDelayed": 10,
    "returnRate": 0.88
  }
}
```

---

### ۵.۳. گزارش عملکرد کارمند

**Endpoint:** `GET /api/stats/employees/{id}/performance`

**توضیحات:** دریافت گزارش عملکرد یک کارمند (فقط مدیر)

**Headers:**
```
Authorization: Bearer {token}
```

**Path Parameters:**
- `id`: شناسه یا نام کاربری کارمند

**پاسخ موفق (200 OK):**
```json
{
  "success": true,
  "data": {
    "employee": {
      "username": "string",
      "name": "string"
    },
    "registeredBooks": 50,
    "loansGiven": 100,
    "booksReceived": 95,
    "averageProcessingTime": 2.5
  }
}
```

---

### ۵.۴. لیست دانشجویان با بیشترین تاخیر

**Endpoint:** `GET /api/stats/top-delayed`

**توضیحات:** دریافت لیست دانشجویانی که بیشترین تاخیر در بازگرداندن کتاب داشته‌اند (فقط مدیر)

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters (اختیاری):**
- `limit` (optional): تعداد نتایج (پیش‌فرض: 10)

**پاسخ موفق (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "student": {
        "username": "string",
        "name": "string",
        "email": "string"
      },
      "delayedReturnsCount": 5,
      "totalDelayedDays": 25
    }
  ],
  "total": 10
}
```

---

## ۶. مدیریت کارکنان (Employees) - فقط مدیر

### ۶.۱. ایجاد حساب کارمند جدید

**Endpoint:** `POST /api/admin/employees`

**توضیحات:** ایجاد حساب کارمند جدید (فقط مدیر)

**Headers:**
```
Authorization: Bearer {token}
```

**بدنه درخواست (Request Body):**
```json
{
  "username": "string",
  "password": "string",
  "name": "string"
}
```

**پاسخ موفق (201 Created):**
```json
{
  "success": true,
  "message": "کارمند با موفقیت ایجاد شد",
  "data": {
    "username": "string",
    "name": "string"
  }
}
```

**پاسخ خطا (400 Bad Request):**
```json
{
  "success": false,
  "message": "نام کاربری تکراری است"
}
```

---

### ۶.۲. لیست کارکنان

**Endpoint:** `GET /api/admin/employees`

**توضیحات:** دریافت لیست تمام کارکنان (فقط مدیر)

**Headers:**
```
Authorization: Bearer {token}
```

**پاسخ موفق (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "username": "string",
      "name": "string",
      "registeredBooks": 50,
      "loansGiven": 100,
      "booksReceived": 95
    }
  ],
  "total": 5
}
```

---

## قالب استاندارد پاسخ‌ها

### پاسخ موفق
```json
{
  "success": true,
  "message": "پیام اختیاری",
  "data": { /* داده‌های پاسخ */ }
}
```

### پاسخ خطا
```json
{
  "success": false,
  "message": "پیام خطا",
  "errors": [ /* آرایه خطاها (اختیاری) */ ]
}
```

## کدهای وضعیت HTTP

- **200 OK**: درخواست موفق
- **201 Created**: ایجاد موفق
- **400 Bad Request**: خطا در داده‌های ورودی
- **401 Unauthorized**: نیاز به احراز هویت
- **403 Forbidden**: عدم دسترسی
- **404 Not Found**: منبع یافت نشد
- **500 Internal Server Error**: خطای سرور

## احراز هویت

برای endpoint‌هایی که نیاز به احراز هویت دارند، باید token را در header با فرمت زیر ارسال کنید:

```
Authorization: Bearer {token}
```

Token از endpoint `/api/auth/login` دریافت می‌شود.

## نکات مهم

1. تمام پاسخ‌ها در قالب JSON هستند
2. برای جستجو و فیلتر از Query Parameters استفاده می‌شود
3. نسخه اولیه بدون pagination است
4. تاریخ‌ها به فرمت ISO 8601 (YYYY-MM-DD) ارسال می‌شوند
5. تمام رشته‌ها باید به UTF-8 encode شوند تا از پشتیبانی فارسی اطمینان حاصل شود

