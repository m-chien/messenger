import { Zap } from "lucide-react";
import React, { useState } from "react";
import "../Style/AuthForm.css";
import { useNavigate } from "react-router-dom";

export const RegisterPage = ({ onSwitchToLogin }) => {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: "",
    phone: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      if (
        !formData.firstName ||
        !formData.lastName ||
        !formData.email ||
        !formData.password ||
        !formData.confirmPassword
      ) {
        setError("Vui lòng điền đầy đủ thông tin bắt buộc");
        setLoading(false);
        return;
      }

      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
        setError("Email không hợp lệ");
        setLoading(false);
        return;
      }

      if (formData.password.length < 6) {
        setError("Mật khẩu phải có ít nhất 6 ký tự");
        setLoading(false);
        return;
      }

      if (formData.password !== formData.confirmPassword) {
        setError("Mật khẩu không khớp");
        setLoading(false);
        return;
      }

      if (
        formData.phone &&
        !/^[0-9]{10,11}$/.test(formData.phone.replace(/\D/g, ""))
      ) {
        setError("Số điện thoại không hợp lệ");
        setLoading(false);
        return;
      }

      // Simulate API call
      await new Promise((resolve) => setTimeout(resolve, 1500));

      // Mock successful registration
      const userData = {
        id: Date.now().toString(),
        name: `${formData.firstName} ${formData.lastName}`,
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        phone: formData.phone,
        avatar:
          "https://images.pexels.com/photos/1587009/pexels-photo-1587009.jpeg?auto=compress&cs=tinysrgb&w=100",
      };

      localStorage.setItem("user", JSON.stringify(userData));
      onRegisterSuccess(userData);
    } catch (err) {
      setError("Đăng ký thất bại. Vui lòng thử lại.");
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
            Tạo tài khoản ChatHub ngay để bắt đầu cuộc trò chuyện tuyệt vời.
          </p>
          <ul className="features">
            <li>✨ Giao diện dễ sử dụng cho mọi người</li>
            <li>🔔 Thông báo tin nhắn mới tức thì</li>
            <li>🌍 Kết nối với những người khắp nơi</li>
            <li>⚡ Hiệu suất nhanh và ổn định</li>
          </ul>
        </div>
      </div>

      <div className="auth-right">
        <div className="auth-form-container">
          <div className="auth-form-header">
            <h1>Tạo Tài Khoản</h1>
            <p>Gia nhập cộng đồng ChatHub ngay hôm nay</p>
          </div>

          <form onSubmit={handleRegister}>
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="firstName">Tên</label>
                <input
                  id="firstName"
                  type="text"
                  name="firstName"
                  placeholder="Tên của bạn"
                  value={formData.firstName}
                  onChange={handleChange}
                  disabled={loading}
                />
              </div>
              <div className="form-group">
                <label htmlFor="lastName">Họ</label>
                <input
                  id="lastName"
                  type="text"
                  name="lastName"
                  placeholder="Họ của bạn"
                  value={formData.lastName}
                  onChange={handleChange}
                  disabled={loading}
                />
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="email">Email</label>
              <input
                id="email"
                type="email"
                name="email"
                placeholder="user@example.com"
                value={formData.email}
                onChange={handleChange}
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="phone">Số điện thoại (Tùy chọn)</label>
              <input
                id="phone"
                type="tel"
                name="phone"
                placeholder="+84 xxx xxx xxx"
                value={formData.phone}
                onChange={handleChange}
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">Mật khẩu</label>
              <input
                id="password"
                type="password"
                name="password"
                placeholder="••••••••"
                value={formData.password}
                onChange={handleChange}
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="confirmPassword">Xác nhận mật khẩu</label>
              <input
                id="confirmPassword"
                type="password"
                name="confirmPassword"
                placeholder="••••••••"
                value={formData.confirmPassword}
                onChange={handleChange}
                disabled={loading}
              />
            </div>

            {error && <div className="form-error">{error}</div>}

            <button type="submit" className="form-button" disabled={loading}>
              {loading ? "⏳ Đang đăng ký..." : "🚀 Tạo Tài Khoản"}
            </button>
          </form>

          <div className="form-divider">
            <span>hoặc đăng ký bằng</span>
          </div>

          <button className="form-button google-btn">
            <img 
              src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg" 
              alt="Google" 
              style={{ width: '20px', height: '20px' }} 
            />
            Google
          </button>

          <div className="form-link">
            Đã có tài khoản? <a onClick={onSwitchToLogin}>Đăng nhập</a>
          </div>

          <div
            style={{
              fontSize: "12px",
              color: "rgba(255, 255, 255, 0.5)",
              marginTop: "20px",
              textAlign: "center",
              lineHeight: "1.6",
            }}
          >
            Bằng cách đăng ký, bạn đồng ý với{" "}
            <a href="#" style={{ color: "#a78bfa", textDecoration: "none" }}>
              Điều khoản
            </a>
            ,{" "}
            <a href="#" style={{ color: "#a78bfa", textDecoration: "none" }}>
              Chính sách dữ liệu
            </a>{" "}
            và{" "}
            <a href="#" style={{ color: "#a78bfa", textDecoration: "none" }}>
              Chính sách cookie
            </a>
            .
          </div>
        </div>
      </div>
    </div>
  );
};
