# 🎨 Frontend Web Client - Tài Liệu Chi Tiết

## Tổng Quan

### Tên Ứng Dụng
**Sixedi Web Client** - Giao diện khách hàng e-commerce

### Chức Năng Chính
- 🏠 **Trang chủ** - Hiển thị sản phẩm nổi bật, banner khuyến mãi
- 🔍 **Tìm kiếm & Lọc** - Tìm sản phẩm theo keyword, danh mục ✅, giá
- 👕 **Chi tiết sản phẩm** - Xem ảnh, mô tả, giá
- 🛒 **Giỏ hàng** - Thêm/xóa sản phẩm, chỉnh sửa số lượng
- 💳 **Thanh toán** - Điền thông tin giao hàng, phí vận chuyển, xác nhận đơn ✅
- 👤 **Tài khoản cá nhân** - Lịch sử đơn hàng, thông tin profile
- 🔐 **Đăng nhập/Đăng ký** - Xác thực người dùng
- 👨‍💼 **Admin Dashboard** - Quản lý sản phẩm (thêm/sửa/xóa)

---

## 🎯 UI Flow - Luồng Tương Tác

### 1️⃣ **Khách Lạ → Đăng Ký**
```
Khách lạ vào trang
  ├─ Thấy sản phẩm, tìm kiếm
  ├─ Click button "Thêm vào giỏ" → request login
  └─ Hoặc click "Đăng ký" → Register page
     ├─ Điền: username, email, password, fullName, phone
     ├─ Validate form (frontend)
     ├─ Submit → Auth Service
     ├─ Nhận response (token + user info)
     └─ Redirect → Home page (logged in)
```

### 2️⃣ **Khách Hàng → Mua Hàng**
```
Home page
  ├─ Search/Filter sản phẩm
  ├─ Click sản phẩm → Product Detail page
  ├─ Click "Thêm vào giỏ"
  │   ├─ Validate: đã chọn size/color?
  │   ├─ Call Order Service: POST /api/orders/{userId}/items
  │   └─ Giỏ hàng +1 sản phẩm
  │
  ├─ Go to Cart page
  │   ├─ Xem danh sách sản phẩm
  │   ├─ Chỉnh sửa số lượng
  │   ├─ Xóa sản phẩm
  │   └─ Xem tổng tiền
  │
  └─ Click "Thanh toán" → Payment page
     ├─ Điền address + phone
     ├─ Validate form
     ├─ Call Order Service: POST /api/orders/checkout
     └─ Redirect → Order Confirmation page
```

---

## 📁 Cấu Trúc Thư Mục

```
frontend/web-client/src/
├── App.jsx                             # Root component
├── main.jsx                            # Entry point
├── pages/                              # Page components (Home, Login, etc)
├── components/                         # Reusable components (Header, Footer, Card)
├── context/                            # React Context (AuthContext, CartContext)
├── api/                                # API calls (axios instances)
├── utils/                              # Utility functions (validation, formatters)
├── routes/                             # Route definitions (React Router)
├── styles/                             # CSS files (global, variables)
└── assets/                             # Static files (images, icons)
```

---

## 💾 State Management

### AuthContext - User Authentication
```javascript
const { user, login, logout, isLoggedIn } = useAuth();
// { user: {id, username, email, role}, isLoggedIn: bool, token: string }
```

### CartContext - Shopping Cart
```javascript
const { cart, addToCart, removeFromCart, getCartCount } = useCart();
// { items: [...], totalAmount, itemCount }
```

---

## 🔌 Backend Integration

### Axios Client Setup
- **Base URL**: `/api` (relative path for DevTunnel & production support) ✅
- **Fallback**: `http://localhost:3000/api` (for local development)
- **Request Interceptor**: Auto-add JWT token to header
- **Response Interceptor**: Catch 401 → redirect to login

### Data Flow - Checkout
1. Cart stores item list in React Context
2. Payment page reads address from form  
3. Data passed via sessionStorage between pages
4. POST /api/orders/checkout returns full OrderDTO ✅
5. Response includes: id, address, itemDetails, shippingFee, status

### API Files
| File | Endpoint | Methods |
|------|----------|---------|
| `auth.api.js` | /auth | POST login, register, logout |
| `product.api.js` | /products | GET list, detail, by category |
| `order.api.js` | /orders | GET, POST, PUT (cart & checkout) |
| `user.api.js` | /users | GET profile, PUT update |
| `address.api.js` | /addresses | GET, POST, PUT, DELETE |

---

## ✔️ Validation & UX

### Form Validation
```javascript
import { validateLoginForm } from '../utils/validation';

const validation = validateLoginForm(formData);
if (!validation.isValid) {
  setErrors(validation.errors);
  return;  // Block submission
}
```

### Loading & Error States
- **Loading**: Show spinner, disable button
- **Success**: Show message, redirect
- **Error**: Show error message beside input or alert

---

## 🔐 Security

### Token Storage
```javascript
// Save after login
localStorage.setItem('token', response.data.token);

// Retrieved and sent in requests
Authorization: Bearer <token>
```

### Route Protection
```javascript
<Route path="/admin" element={
  <PrivateRoute element={<AdminDashboard />} requiredRole="ADMIN" />
} />
```

### Role-Based UI
```javascript
{user?.role?.includes('ADMIN') && <AdminMenuItem />}
```

---

## 🏃 How to Run

### Development
```bash
cd frontend/web-client
npm install
npm run dev

# Open browser: http://localhost:5173
```

### Build for Production
```bash
npm run build      # Creates dist/ folder
npm run preview    # Preview built version locally
```

### Code Quality
```bash
npm run lint       # Check code style
npx eslint . --fix # Auto-fix issues
```

---

## 📦 Main Dependencies
| Package | Version | Purpose |
|---------|---------|---------|
| react | 19.2.0 | UI framework |
| react-router-dom | 7.12.0 | Client-side routing |
| axios | 1.13.2 | HTTP requests |
| antd | 6.1.1 | UI components (Button, Form, Table, Modal) |
| vite | 7.2.4 | Build tool & dev server |

---

## 🚀 Extending Features

### Add Wishlist
- Create `pages/Wishlist.jsx`
- Add context: `WishlistContext.jsx`
- Update routes in `routes/index.jsx`

### Add Product Reviews
- Create `components/ReviewForm.jsx`
- Add review API in `api/review.api.js`
- Display in `ProductDetail.jsx`

### Add Real Payment Gateway
- Integrate Stripe/PayPal in `Payment.jsx`
- Create `api/payment.api.js`
- Update Order status after payment

---

## ✅ Checklist for New Developers

- [ ] Clone repository
- [ ] Run `npm install`
- [ ] Run `npm run dev` - verify server starts
- [ ] Open http://localhost:5173 in browser
- [ ] Test Home page loads with products
- [ ] Test Register page - try signup
- [ ] Test Login - verify token saved
- [ ] Test Add to Cart
- [ ] Open DevTools → Network tab
- [ ] See API requests being made
- [ ] Try Checkout flow
- [ ] Read AuthContext.jsx and CartContext.jsx
- [ ] Modify a component, verify HMR works
- [ ] Try building: `npm run build`
- [ ] Review error handling in one API call

---

**Last Updated**: 2026-01-16  
**Tech Stack**: React 19 + Vite + Axios  
**Status**: ✅ Production Ready

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Babel](https://babeljs.io/) (or [oxc](https://oxc.rs) when used in [rolldown-vite](https://vite.dev/guide/rolldown)) for Fast Refresh
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/) for Fast Refresh

## React Compiler

The React Compiler is not enabled on this template because of its impact on dev & build performances. To add it, see [this documentation](https://react.dev/learn/react-compiler/installation).

## Expanding the ESLint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.
