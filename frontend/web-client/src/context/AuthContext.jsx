import { createContext, useContext, useState } from "react";
import logger from "../utils/logger";

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    try {
      const userJson = localStorage.getItem("user");
      return userJson && userJson !== "undefined" ? JSON.parse(userJson) : null;
    } catch (error) {
      logger.error("Error parsing user from localStorage:", error);
      localStorage.removeItem("user");
      return null;
    }
  });

  const login = (data) => {
    localStorage.setItem("token", data.token);
    localStorage.setItem("user", JSON.stringify(data.user));
    setUser(data.user);
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
