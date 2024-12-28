import { useState } from "react";

const useAuth = () => {
  const [isAuthenticated, setAuthenticated] = useState(false);

  const login = (credentials) => {
    // Handle login
    setAuthenticated(true);
  };

  const logout = () => {
    setAuthenticated(false);
  };

  return { isAuthenticated, login, logout };
};

export default useAuth;
