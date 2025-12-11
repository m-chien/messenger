import { cs } from "date-fns/locale";
import { api } from "./Api"; // axios instance

export const User = () => {
  // Đăng nhập
  const login = async (email, pass) => {
    try {
      const response = await api.post("/users/login", {
        email: email,
        pass: pass,
      });
      console.log("Login response:", response.data);
      sessionStorage.setItem("accessToken", response.data.token);
      return response.data;
    } catch (error) {
      console.error("Login error:", error.response?.data || error.message);
      throw error;
    }
  };
  return {
    login,
  };
};
