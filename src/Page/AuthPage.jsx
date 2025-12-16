import React, { useState } from "react";
import { LoginPage } from "./LoginPage";
import { RegisterPage } from "./RegisterPage";

export const AuthPage = () => {
  const [isLogin, setIsLogin] = useState(true);

  const handleRegisterSuccess = (userData) => {
    console.log("Register success:", userData);
  };

  return (
    <>
      {isLogin ? (
        <LoginPage
          onSwitchToRegister={() => setIsLogin(false)}
        />
      ) : (
        <RegisterPage
          onRegisterSuccess={handleRegisterSuccess}
          onSwitchToLogin={() => setIsLogin(true)}
        />
      )}
    </>
  );
};
