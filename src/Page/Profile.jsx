import { ArrowLeft, Calendar, Edit2, LogOut, Mail, Phone, ArrowLeftCircle, UserPlus, MessageCircle } from "lucide-react";
import React, { useState, useEffect } from "react";
import { FastAverageColor } from "fast-average-color";
import { api } from "../Api/Api";
import "../Style/AuthForm.css";

export const Profile = ({ userData, onLogout, onUpdateProfile, isOtherProfile = false, onBack }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    firstName: userData?.firstName || userData?.name?.split(" ")[0] || "",
    lastName: userData?.lastName || userData?.name?.split(" ").slice(1).join(" ") || "",
    email: userData?.email || "",
    phone: userData?.phone || "",
    bio: userData?.bio || "",
  });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  // Default avatar if not provided. Also handle avatarUrl logic if the data came from Friend list
  const avatarLink = userData?.avatarUrl 
    ? `http://localhost:8080${userData.avatarUrl}` 
    : userData?.avatar;

  const [headerBg, setHeaderBg] = useState("");
  const [avatarBorderBg, setAvatarBorderBg] = useState("");
  const [containerStyle, setContainerStyle] = useState({});

  // States for dynamic profile actions
  const [isFriend, setIsFriend] = useState(false);
  const [mutualFriends, setMutualFriends] = useState([]);
  const [loadingExtra, setLoadingExtra] = useState(false);

  useEffect(() => {
    if (isOtherProfile && userData?.userId) {
      const fetchExtraProfileData = async () => {
        setLoadingExtra(true);
        try {
          // 1. Check friendship status
          const friendRes = await api.get(`/friends/check?userID2=${userData.userId}`);
          setIsFriend(friendRes.data === true);

          // 2. Fetch mutual friends
          const mutualRes = await api.get(`/friends/mutual?userID2=${userData.userId}`);
          setMutualFriends(mutualRes.data || []);
        } catch (error) {
          console.error("Lỗi lấy thông tin bạn bè:", error);
        } finally {
          setLoadingExtra(false);
        }
      };

      fetchExtraProfileData();
    }
  }, [isOtherProfile, userData?.userId]);

  useEffect(() => {
    if (avatarLink) {
      const fac = new FastAverageColor();
      fac.getColorAsync(avatarLink)
        .then(color => {
          const [r, g, b] = color.value;
          // Create harmonious gradients
          setHeaderBg(`linear-gradient(135deg, rgba(${r}, ${g}, ${b}, 0.5) 0%, rgba(${Math.max(0, r-40)}, ${Math.max(0, g-40)}, ${Math.max(0, b-40)}, 0.2) 100%)`);
          setAvatarBorderBg(`linear-gradient(135deg, rgba(${r}, ${g}, ${b}, 1) 0%, rgba(${Math.max(0, r-40)}, ${Math.max(0, g-40)}, ${Math.max(0, b-40)}, 1) 100%)`);

          const darkR = Math.floor(r * 0.15);
          const darkG = Math.floor(g * 0.15);
          const darkB = Math.floor(b * 0.15);

          setContainerStyle({
            '--auth-bg': `radial-gradient(circle at top left, rgb(${darkR}, ${darkG}, ${darkB}) 0%, #0f1016 100%)`,
            '--color-primary-rgb': `${r}, ${g}, ${b}`,
            '--color-secondary-rgb': `${Math.max(0, r-30)}, ${Math.min(255, g+30)}, ${Math.max(0, b-30)}`
          });
        })
        .catch(e => {
          console.error("FastAverageColor error:", e);
        });
    }
  }, [avatarLink]);

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

      // Only update local storage if it's the main user's own profile
      if (!isOtherProfile) {
        localStorage.setItem("user", JSON.stringify(updatedUser));
        if (onUpdateProfile) {
            onUpdateProfile(updatedUser);
        }
      }

      setSuccess("✅ Cập nhật hồ sơ thành công!");
      setIsEditing(false);
    } catch (err) {
      setError("❌ Cập nhật thất bại. Vui lòng thử lại.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container" style={containerStyle}>
      <div className="profile-card">
        {/* Header */}
        <div 
          className="profile-header"
          style={headerBg ? { background: headerBg } : {}}
        >
          {isOtherProfile && onBack && (
            <button 
              className="btn-back" 
              onClick={onBack} 
              style={{ position: 'absolute', top: '20px', left: '20px', background: 'none', border: 'none', color: '#fff', cursor: 'pointer' }}
              title="Quay lại"
            >
              <ArrowLeft size={24} />
            </button>
          )}

          <div 
            className="profile-avatar-container"
            style={avatarBorderBg ? { background: avatarBorderBg } : {}}
          >
            {avatarLink ? (
              <img
                src={avatarLink}
                alt={userData?.name}
                className="profile-avatar"
                crossOrigin="anonymous"
              />
            ) : (
              <div className="profile-avatar" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', backgroundColor: '#333', color: '#fff', fontSize: '2rem' }}>
                {userData?.name?.charAt(0).toUpperCase()}
              </div>
            )}
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

          {isEditing && !isOtherProfile ? (
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

              {/* MUTUAL FRIENDS SECTION */}
              {isOtherProfile && mutualFriends.length > 0 && (
                <div className="bio-section" style={{ marginTop: '20px' }}>
                  <h3 className="bio-header" style={{ marginBottom: '15px' }}>
                    👥 Bạn chung ({mutualFriends.length})
                  </h3>
                  <div style={{ display: 'flex', gap: '10px', overflowX: 'auto', paddingBottom: '5px' }}>
                    {mutualFriends.map(mf => (
                      <div key={mf.id} style={{ textAlign: 'center', minWidth: '60px' }}>
                        {mf.avatarUrl ? (
                          <img 
                            src={`http://localhost:8080${mf.avatarUrl}`} 
                            alt={mf.name}
                            style={{ width: '40px', height: '40px', borderRadius: '50%', objectFit: 'cover', border: '2px solid rgba(255,255,255,0.2)' }}
                          />
                        ) : (
                          <div style={{ width: '40px', height: '40px', borderRadius: '50%', backgroundColor: '#333', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '14px', margin: '0 auto', border: '2px solid rgba(255,255,255,0.2)' }}>
                            {mf.name?.charAt(0).toUpperCase()}
                          </div>
                        )}
                        <p style={{ fontSize: '11px', color: 'rgba(255,255,255,0.8)', marginTop: '5px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', maxWidth: '60px' }}>
                          {mf.name?.split(' ')[mf.name.split(' ').length - 1]}
                        </p>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {isOtherProfile ? (
                 <div className="profile-actions">
                  {loadingExtra ? (
                     <button className="form-button" disabled style={{ flex: 1, backgroundColor: 'rgba(255,255,255,0.1)' }}>
                       Đang tải...
                     </button>
                  ) : isFriend ? (
                    <button
                      className="form-button"
                      style={{ flex: 1 }}
                      onClick={() => {
                         // Fallback - in the future this should trigger opening the exact chat room directly
                         alert("Chức năng mở trực tiếp cửa sổ nhắn tin đang được cập nhật!");
                      }}
                    >
                      <MessageCircle size={18} className="btn-icon" />
                      Nhắn tin
                    </button>
                  ) : (
                    <button
                      className="form-button"
                      style={{ flex: 1, background: 'linear-gradient(135deg, #3b82f6 0%, #2dd4bf 100%)' }}
                      onClick={async () => {
                        try {
                           setLoadingExtra(true);
                           await api.post(`/friends/add?userID1=${JSON.parse(localStorage.getItem('user')).userId}&userID2=${userData.userId}`);
                           setIsFriend(true);
                        } catch(e) {
                           console.error("Add friend failed", e);
                        } finally {
                           setLoadingExtra(false);
                        }
                      }}
                    >
                      <UserPlus size={18} className="btn-icon" />
                      Kết bạn
                    </button>
                  )}
                 </div>
              ) : (
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
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
};
