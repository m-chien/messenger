import { Calendar, Edit2, LogOut, Mail, Phone } from "lucide-react";
import React, { useState } from "react";
import "../Style/AuthForm.css";

export const Profile = ({ userData, onLogout, onUpdateProfile }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    firstName: userData?.firstName || userData?.name?.split(" ")[0] || "",
    lastName:
      userData?.lastName || userData?.name?.split(" ").slice(1).join(" ") || "",
    email: userData?.email || "",
    phone: userData?.phone || "",
    bio: userData?.bio || "",
  });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    setError("");
  };

  const handleSaveProfile = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    setLoading(true);

    try {
      if (!formData.firstName || !formData.lastName) {
        setError("Vui lòng nhập tên đầy đủ");
        setLoading(false);
        return;
      }

      await new Promise((resolve) => setTimeout(resolve, 1000));

      const updatedUser = {
        ...userData,
        firstName: formData.firstName,
        lastName: formData.lastName,
        name: `${formData.firstName} ${formData.lastName}`,
        email: formData.email,
        phone: formData.phone,
        bio: formData.bio,
      };

      localStorage.setItem("user", JSON.stringify(updatedUser));
      onUpdateProfile(updatedUser);
      setSuccess("✅ Cập nhật hồ sơ thành công!");
      setIsEditing(false);
    } catch (err) {
      setError("❌ Cập nhật thất bại. Vui lòng thử lại.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="profile-card">
        {/* Header */}
        <div className="profile-header">
          <div className="profile-avatar-container">
            <img
              src={userData?.avatar}
              alt={userData?.name}
              className="profile-avatar"
            />
          </div>
          <h1 className="profile-name">
            {userData?.name || "Người dùng"}
          </h1>
          <p className="profile-email">
            @{userData?.email?.split("@")[0]}
          </p>
        </div>

        {/* Content */}
        <div className="profile-content">
          {error && <div className="form-error">{error}</div>}
          {success && <div className="form-success">{success}</div>}

          {isEditing ? (
            // Edit Form
            <form onSubmit={handleSaveProfile}>
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="firstName">Tên</label>
                  <input
                    id="firstName"
                    type="text"
                    name="firstName"
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
                  value={formData.email}
                  onChange={handleChange}
                  disabled={true}
                  style={{ opacity: 0.7, cursor: 'not-allowed' }}
                />
              </div>

              <div className="form-group">
                <label htmlFor="phone">Số điện thoại</label>
                <input
                  id="phone"
                  type="tel"
                  name="phone"
                  value={formData.phone}
                  onChange={handleChange}
                  disabled={loading}
                />
              </div>

              <div className="form-group">
                <label htmlFor="bio">Tiểu sử</label>
                <textarea
                  id="bio"
                  name="bio"
                  value={formData.bio}
                  onChange={handleChange}
                  disabled={loading}
                  className="form-control"
                  placeholder="Viết gì đó về bạn..."
                />
              </div>

              <div className="profile-actions">
                <button
                  type="submit"
                  className="form-button"
                  disabled={loading}
                  style={{ flex: 1 }}
                >
                  {loading ? "⏳ Đang lưu..." : "💾 Lưu Thay Đổi"}
                </button>
                <button
                  type="button"
                  onClick={() => setIsEditing(false)}
                  className="form-button form-button-secondary"
                  style={{ flex: 1 }}
                >
                  ✕ Hủy
                </button>
              </div>
            </form>
          ) : (
            // View Profile
            <>
              <div className="info-grid">
                {/* Email Card */}
                <div className="info-card">
                  <div className="info-header">
                    <Mail size={16} className="btn-icon" />
                    EMAIL
                  </div>
                  <p className="info-value">
                    {userData?.email || "Không có"}
                  </p>
                </div>

                {/* Phone Card */}
                <div className="info-card">
                  <div className="info-header">
                    <Phone size={16} className="btn-icon" />
                    ĐIỆN THOẠI
                  </div>
                  <p className="info-value">
                    {userData?.phone || "Chưa cập nhật"}
                  </p>
                </div>

                {/* Join Date Card */}
                <div className="info-card">
                  <div className="info-header">
                    <Calendar size={16} className="btn-icon" />
                    THAM GIA
                  </div>
                  <p className="info-value">
                    {new Date().toLocaleDateString("vi-VN")}
                  </p>
                </div>
              </div>

              {userData?.bio && (
                <div className="bio-section">
                  <h3 className="bio-header">
                    💭 Tiểu sử
                  </h3>
                  <p className="bio-text">
                    {userData.bio}
                  </p>
                </div>
              )}

              <div className="profile-actions">
                <button
                  onClick={() => setIsEditing(true)}
                  className="form-button"
                  style={{ flex: 1 }}
                >
                  <Edit2 size={18} className="btn-icon" />
                  Chỉnh Sửa Hồ Sơ
                </button>

                <button
                  onClick={onLogout}
                  className="form-button form-button-danger"
                  style={{ flex: 1 }}
                >
                  <LogOut size={18} className="btn-icon" />
                  Đăng Xuất
                </button>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
};
