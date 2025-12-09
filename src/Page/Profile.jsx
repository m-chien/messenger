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
    <div
      style={{
        background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
        minHeight: "100vh",
        padding: "40px 20px",
        position: "relative",
        overflow: "hidden",
      }}
    >
      {/* Animated background */}
      <div
        style={{
          position: "absolute",
          width: "400px",
          height: "400px",
          background: "rgba(255, 255, 255, 0.1)",
          borderRadius: "50%",
          top: "-100px",
          right: "-100px",
          animation: "float 6s ease-in-out infinite",
        }}
      />
      <div
        style={{
          position: "absolute",
          width: "300px",
          height: "300px",
          background: "rgba(255, 255, 255, 0.1)",
          borderRadius: "50%",
          bottom: "-50px",
          left: "-50px",
          animation: "float 8s ease-in-out infinite reverse",
        }}
      />

      <div
        style={{
          maxWidth: "900px",
          margin: "0 auto",
          backgroundColor: "var(--bg-color)",
          borderRadius: "20px",
          overflow: "hidden",
          boxShadow: "0 20px 60px rgba(102, 126, 234, 0.3)",
          position: "relative",
          zIndex: 1,
        }}
      >
        {/* Header */}
        <div
          style={{
            background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
            padding: "60px 40px",
            textAlign: "center",
            color: "white",
          }}
        >
          <div
            style={{
              width: "140px",
              height: "140px",
              borderRadius: "50%",
              border: "5px solid white",
              margin: "0 auto 20px",
              overflow: "hidden",
              boxShadow: "0 10px 30px rgba(0, 0, 0, 0.2)",
            }}
          >
            <img
              src={userData?.avatar}
              alt={userData?.name}
              style={{
                width: "100%",
                height: "100%",
                objectFit: "cover",
              }}
            />
          </div>
          <h1
            style={{ fontSize: "32px", margin: "0 0 5px 0", fontWeight: "900" }}
          >
            {userData?.name || "Người dùng"}
          </h1>
          <p style={{ margin: "0", opacity: 0.9, fontSize: "14px" }}>
            @{userData?.email?.split("@")[0]}
          </p>
        </div>

        {/* Content */}
        <div style={{ padding: "40px" }}>
          {error && (
            <div className="form-error" style={{ marginBottom: "20px" }}>
              {error}
            </div>
          )}
          {success && (
            <div className="form-success" style={{ marginBottom: "20px" }}>
              {success}
            </div>
          )}

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
                  style={{
                    width: "100%",
                    minHeight: "100px",
                    padding: "14px 16px",
                    border: "2px solid var(--border-color, #e5e5e5)",
                    borderRadius: "12px",
                    backgroundColor: "var(--chat-bg)",
                    color: "var(--text-color)",
                    fontSize: "14px",
                    fontFamily: "inherit",
                    boxSizing: "border-box",
                    resize: "vertical",
                    transition: "all 0.3s ease",
                  }}
                  placeholder="Viết gì đó về bạn..."
                  onFocus={(e) => {
                    e.target.style.borderColor = "#667eea";
                  }}
                  onBlur={(e) => {
                    e.target.style.borderColor = "var(--border-color, #e5e5e5)";
                  }}
                />
              </div>

              <div style={{ display: "flex", gap: "12px", marginTop: "25px" }}>
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
              <div style={{ marginBottom: "30px" }}>
                <div
                  style={{
                    display: "grid",
                    gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))",
                    gap: "20px",
                  }}
                >
                  {/* Email Card */}
                  <div
                    style={{
                      padding: "24px",
                      backgroundColor: "var(--chat-bg)",
                      borderRadius: "12px",
                      border: "2px solid var(--border-color, #e5e5e5)",
                      transition: "all 0.3s ease",
                    }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.borderColor = "#667eea";
                      e.currentTarget.style.boxShadow =
                        "0 4px 12px rgba(102, 126, 234, 0.2)";
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.borderColor =
                        "var(--border-color, #e5e5e5)";
                      e.currentTarget.style.boxShadow = "none";
                    }}
                  >
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        marginBottom: "12px",
                      }}
                    >
                      <Mail
                        size={20}
                        style={{ marginRight: "10px", color: "#667eea" }}
                      />
                      <span
                        style={{
                          fontSize: "12px",
                          color: "#999",
                          fontWeight: "700",
                        }}
                      >
                        EMAIL
                      </span>
                    </div>
                    <p
                      style={{
                        margin: "0",
                        fontWeight: "600",
                        fontSize: "15px",
                      }}
                    >
                      {userData?.email || "Không có"}
                    </p>
                  </div>

                  {/* Phone Card */}
                  <div
                    style={{
                      padding: "24px",
                      backgroundColor: "var(--chat-bg)",
                      borderRadius: "12px",
                      border: "2px solid var(--border-color, #e5e5e5)",
                      transition: "all 0.3s ease",
                    }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.borderColor = "#667eea";
                      e.currentTarget.style.boxShadow =
                        "0 4px 12px rgba(102, 126, 234, 0.2)";
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.borderColor =
                        "var(--border-color, #e5e5e5)";
                      e.currentTarget.style.boxShadow = "none";
                    }}
                  >
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        marginBottom: "12px",
                      }}
                    >
                      <Phone
                        size={20}
                        style={{ marginRight: "10px", color: "#667eea" }}
                      />
                      <span
                        style={{
                          fontSize: "12px",
                          color: "#999",
                          fontWeight: "700",
                        }}
                      >
                        ĐIỆN THOẠI
                      </span>
                    </div>
                    <p
                      style={{
                        margin: "0",
                        fontWeight: "600",
                        fontSize: "15px",
                      }}
                    >
                      {userData?.phone || "Chưa cập nhật"}
                    </p>
                  </div>

                  {/* Join Date Card */}
                  <div
                    style={{
                      padding: "24px",
                      backgroundColor: "var(--chat-bg)",
                      borderRadius: "12px",
                      border: "2px solid var(--border-color, #e5e5e5)",
                      transition: "all 0.3s ease",
                    }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.borderColor = "#667eea";
                      e.currentTarget.style.boxShadow =
                        "0 4px 12px rgba(102, 126, 234, 0.2)";
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.borderColor =
                        "var(--border-color, #e5e5e5)";
                      e.currentTarget.style.boxShadow = "none";
                    }}
                  >
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        marginBottom: "12px",
                      }}
                    >
                      <Calendar
                        size={20}
                        style={{ marginRight: "10px", color: "#667eea" }}
                      />
                      <span
                        style={{
                          fontSize: "12px",
                          color: "#999",
                          fontWeight: "700",
                        }}
                      >
                        THAM GIA
                      </span>
                    </div>
                    <p
                      style={{
                        margin: "0",
                        fontWeight: "600",
                        fontSize: "15px",
                      }}
                    >
                      {new Date().toLocaleDateString("vi-VN")}
                    </p>
                  </div>
                </div>
              </div>

              {userData?.bio && (
                <div
                  style={{
                    marginBottom: "30px",
                    padding: "24px",
                    backgroundColor: "var(--chat-bg)",
                    borderRadius: "12px",
                    border: "2px solid var(--border-color, #e5e5e5)",
                  }}
                >
                  <h3
                    style={{
                      margin: "0 0 12px 0",
                      fontSize: "14px",
                      fontWeight: "700",
                    }}
                  >
                    💭 Tiểu sử
                  </h3>
                  <p
                    style={{ margin: "0", fontSize: "14px", lineHeight: "1.6" }}
                  >
                    {userData.bio}
                  </p>
                </div>
              )}

              <div style={{ display: "flex", gap: "12px" }}>
                <button
                  onClick={() => setIsEditing(true)}
                  className="form-button"
                  style={{
                    flex: 1,
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    gap: "8px",
                  }}
                >
                  <Edit2 size={18} />
                  Chỉnh Sửa Hồ Sơ
                </button>

                <button
                  onClick={onLogout}
                  style={{
                    flex: 1,
                    padding: "14px 20px",
                    background:
                      "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)",
                    color: "white",
                    border: "none",
                    borderRadius: "12px",
                    cursor: "pointer",
                    fontWeight: "700",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    gap: "8px",
                    transition: "all 0.3s ease",
                    textTransform: "uppercase",
                    letterSpacing: "1px",
                    fontSize: "14px",
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.transform = "translateY(-2px)";
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.transform = "translateY(0)";
                  }}
                >
                  <LogOut size={18} />
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
