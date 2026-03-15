import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "../pages/Login";
import Home from "../pages/Home/Home";
import CategoryPage from "../pages/CategoryPage/CategoryPage";
import ProductDetailPage from "../pages/ProductDetail/ProductDetail";
import RegisterPage from "../pages/Register/Register";
import CartPage from "../pages/Cart/Cart";
import ProfilePage from "../pages/Profile/Profile";
import AddressPage from "../pages/Address/Address";
import PaymentPage from "../pages/Payment/Payment";
import SearchPage from "../pages/Search/Search";
import TestAPI from "../pages/TestAPI/TestAPI";
import AdminDashboardPage from "../pages/AdminDashboard/AdminDashboard";
import UserManagementPage from "../pages/AdminDashboard/UserManagementPage";
import OrderManagementPage from "../pages/AdminDashboard/OrderManagementPage";
import RevenueDashboardPage from "../pages/AdminDashboard/RevenueDashboardPage";
import AuditLogsPage from "../pages/AdminDashboard/AuditLogsPage";
import PrivateRoute from "./PrivateRoute";

function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />

        <Route path="/register" element={<RegisterPage />} />

        <Route path="/test-api" element={<TestAPI />} />

        <Route
          path="/"
          element={<Home />}
        />

        <Route
          path="/product/:id"
          element={<ProductDetailPage />}
        />

        <Route
          path="/category/:category"
          element={<CategoryPage />}
        />

        <Route
          path="/san-pham"
          element={<CategoryPage />}
        />

        <Route
          path="/nam"
          element={<CategoryPage />}
        />

        <Route
          path="/nu"
          element={<CategoryPage />}
        />

        <Route
          path="/giay"
          element={<CategoryPage />}
        />

        <Route
          path="/phu-kien"
          element={<CategoryPage />}
        />

        <Route
          path="/search"
          element={<SearchPage />}
        />

        <Route
          path="/cart"
          element={
            <PrivateRoute>
              <CartPage />
            </PrivateRoute>
          }
        />

        {/* Removed /orders route - orders are now displayed in /profile */}

        <Route
          path="/profile"
          element={
            <PrivateRoute>
              <ProfilePage />
            </PrivateRoute>
          }
        />

        <Route
          path="/address"
          element={
            <PrivateRoute>
              <AddressPage />
            </PrivateRoute>
          }
        />

        <Route
          path="/admin/orders"
          element={
            <PrivateRoute adminOnly={true}>
              <OrderManagementPage />
            </PrivateRoute>
          }
        />

        <Route
          path="/admin/users"
          element={
            <PrivateRoute adminOnly={true}>
              <UserManagementPage />
            </PrivateRoute>
          }
        />

        <Route
          path="/admin/revenue"
          element={
            <PrivateRoute adminOnly={true}>
              <RevenueDashboardPage />
            </PrivateRoute>
          }
        />

        <Route
          path="/admin"
          element={
            <PrivateRoute adminOnly={true}>
              <AdminDashboardPage />
            </PrivateRoute>
          }
        />

        <Route
          path="/payment"
          element={
            <PrivateRoute>
              <PaymentPage />
            </PrivateRoute>
          }
        />

        {/* Hidden route for Audit Logs */}
        <Route
          path="/audit-logs"
          element={
            <PrivateRoute adminOnly={true}>
              <AuditLogsPage />
            </PrivateRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default AppRoutes;
