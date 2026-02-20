import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const PrivateRoute = ({ children, adminOnly = false }) => {
  const { user } = useAuth();

  // Check if user is logged in
  if (!user) {
    return <Navigate to="/login" />;
  }

  // Check if route requires admin role
  if (adminOnly) {
    const isAdmin = user.role && user.role.toUpperCase().includes('ADMIN');
    if (!isAdmin) {
      return <Navigate to="/" />;
    }
  }

  return children;
};

export default PrivateRoute;
