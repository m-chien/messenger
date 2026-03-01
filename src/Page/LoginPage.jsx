import { Zap } from "lucide-react";
import React, { useState } from "react";
import "../Style/AuthForm.css";
import { GoogleLogin } from "@react-oauth/google";
import { useNavigate } from "react-router-dom";
import { userService } from "../Hook/userService.js";
export const LoginPage = ({ onSwitchToRegister }) => {
  const naviagte = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      if (!email || !password) {
        setError("Vui lòng điền đầy đủ thông tin");
        setLoading(false);
        return;
      }

      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        setError("Email không hợp lệ");
        setLoading(false);
        return;
      }
      const userData = await userService.login(email, password);
      naviagte("/home");
    } catch (err) {
      console.error("Login failed:", err);
      setError("Đăng nhập thất bại. Vui lòng thử lại.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-left">
        <div className="auth-left-content">
          <div className="brand-wrapper">
            <Zap size={48} className="brand-icon" />
            <h2 className="brand-name">ChatHub</h2>
          </div>
          <p className="subtitle">
            Kết nối ngay, trò chuyện vui. Nơi những cuộc hội thoại trở nên ý
            nghĩa và thú vị.
          </p>
          <ul className="features">
            <li>💬 Nhắn tin tức thời với bạn bè</li>
            <li>🔒 Bảo mật hàng đầu cho tin nhắn của bạn</li>
            <li>📱 Truy cập mọi lúc mọi nơi</li>
            <li>🎨 Giao diện hiện đại và dễ sử dụng</li>
          </ul>
        </div>
      </div>

      <div className="auth-right">
        <div className="auth-form-container">
          <div className="auth-form-header">
            <h1>Đăng Nhập</h1>
            <p>Chào mừng quay lại ChatHub</p>
          </div>

          <form onSubmit={handleLogin}>
            <div className="form-group">
              <label htmlFor="email">Email hoặc Số điện thoại</label>
              <input
                id="email"
                type="email"
                placeholder="your@email.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">Mật khẩu</label>
              <input
                id="password"
                type="password"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={loading}
              />
            </div>

            {error && <div className="form-error">{error}</div>}

            <button
              type="submit"
              className="form-button"
              disabled={loading}
              onClick={handleLogin}
            >
              {loading ? "⏳ Đang đăng nhập..." : "🚀 Đăng Nhập"}
            </button>
          </form>

          <div className="form-divider">
            <span>hoặc tiếp tục với</span>
          </div>

          <GoogleLogin
            onSuccess={async (res) => {
              const idToken = res.credential;
              console.log("ID TOKEN:", idToken);
              const data = await userService.loginGG(idToken);
              naviagte("/home");
              console.log("🚀 ~ LoginPage ~ data:", data);
            }}
            onError={() => {
              console.log("Google Login Failed");
            }}
          />

          <div className="form-link">
            Chưa có tài khoản?{" "}
            <a onClick={onSwitchToRegister}>Tạo tài khoản mới</a>
          </div>

          <div className="form-link">
            <a href="#">Quên mật khẩu?</a>
          </div>
        </div>
      </div>
    </div>
  );
};
