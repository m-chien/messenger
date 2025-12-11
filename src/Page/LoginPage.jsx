import { Zap } from "lucide-react";
import React, { useState } from "react";
import "../Style/AuthForm.css";
import { useNavigate } from "react-router-dom";
import { User } from "../Api/User.js";

export const LoginPage = ({ onLoginSuccess, onSwitchToRegister }) => {
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
      const userData = await User().login(email, password);
      naviagte("/");
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
          <div
            style={{
              display: "flex",
              alignItems: "center",
              marginBottom: "20px",
            }}
          >
            <Zap size={48} style={{ color: "#fff" }} />
            <h2 style={{ margin: "0 0 0 15px" }}>ChatHub</h2>
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

            <button type="submit" className="form-button" disabled={loading}>
              {loading ? "⏳ Đang đăng nhập..." : "🚀 Đăng Nhập"}
            </button>
          </form>

          <div className="form-divider">
            <span>hoặc tiếp tục với</span>
          </div>

          <button
            className="form-button"
            style={{
              background: "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)",
              marginTop: "8px",
            }}
          >
            Google
          </button>

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
