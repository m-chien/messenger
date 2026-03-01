import React, { useState, useEffect, useMemo } from "react";
import {
  UserX,
  UserCheck,
  UserPlus,
  Search,
  Users,
  ArrowLeft,
  Globe,
} from "lucide-react";
import useFetchAll from "../Hook/useFetchAll";
import { api } from "../Api/Api.js";
import "./../Style/FriendsPage.css";
import { Profile } from "./Profile"; // Import Profile component

function FriendsPage({ onBackToChat, defaultTab = "friends" }) {
  const token = sessionStorage.getItem("accessToken");
  const [activeTab, setActiveTab] = useState(defaultTab); // "friends" or "requests" or "add"
  const [searchQuery, setSearchQuery] = useState("");
  const [friends, setFriends] = useState([]);
  const [friendRequests, setFriendRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  
  // Thêm state lưu người bạn đang được chọn để xem profile
  const [selectedProfile, setSelectedProfile] = useState(null);

  useEffect(() => {
    setActiveTab(defaultTab);
  }, [defaultTab]);

  // Lấy userId từ token
  const myUserId = useMemo(() => {
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split(".")[1]));
      return Number(payload.sub);
    } catch (e) {
      console.error("Lỗi decode token:", e);
      return null;
    }
  }, [token]);

  // Fetch danh sách bạn bè
  const { data: friendsData, refetch: refetchFriends } =
    useFetchAll("/friends/list");
  console.log("🚀 ~ FriendsPage ~ friendsData:", friendsData)

  // Fetch danh sách yêu cầu kết bạn
  const { data: requestsData, refetch: refetchRequests } = useFetchAll(
    "friendRequests/friendRequestsForUser",
  );

  // Fetch TẤT CẢ user (trừ bản thân)
  const { data: allUsersData } = useFetchAll("/users");
  const allUsers = useMemo(() => {
    return (allUsersData || []).filter(u => u.id !== myUserId);
  }, [allUsersData, myUserId]);

  useEffect(() => {
    if (friendsData) {
      setFriends(friendsData);
    }
  }, [friendsData]);

  useEffect(() => {
    if (requestsData) {
      setFriendRequests(requestsData);
    }
  }, [requestsData]);

  // Lọc danh sách theo tìm kiếm
  const filteredFriends = friends.filter(
    (friend) =>
      friend.name?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      friend.email?.toLowerCase().includes(searchQuery.toLowerCase()),
  );

  const filteredRequests = friendRequests.filter(
    (request) =>
      request.senderName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      request.senderEmail?.toLowerCase().includes(searchQuery.toLowerCase()),
  );

  const filteredAllUsers = allUsers.filter(
    (u) =>
      u.name?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      u.email?.toLowerCase().includes(searchQuery.toLowerCase()),
  );

  // Hàm chấp nhận yêu cầu kết bạn
  const handleAcceptRequest = async (requestId) => {
    try {
      setLoading(true);
      await api.post(`/friends/accept/${requestId}`, {});
      setFriendRequests(
        friendRequests.filter((req) => req.idFriendRequest !== requestId),
      );
      await refetchFriends();
      await refetchRequests();
    } catch (error) {
      console.error("Lỗi chấp nhận yêu cầu:", error);
    } finally {
      setLoading(false);
    }
  };

  // Hàm từ chối yêu cầu kết bạn
  const handleRejectRequest = async (requestId) => {
    try {
      setLoading(true);
      await api.post(`/friends/reject/${requestId}`, {});
      setFriendRequests(
        friendRequests.filter((req) => req.idFriendRequest !== requestId),
      );
      await refetchRequests();
    } catch (error) {
      console.error("Lỗi từ chối yêu cầu:", error);
    } finally {
      setLoading(false);
    }
  };

  // Hàm xóa bạn
  const handleRemoveFriend = async (friendId, e) => {
    e.stopPropagation(); // Ngăn click lan ra ngoài thẻ card
    try {
      setLoading(true);
      await api.post(`/friends/remove/${friendId}`, {});
      setFriends(friends.filter((friend) => friend.idUser !== friendId));
      await refetchFriends();
    } catch (error) {
      console.error("Lỗi xóa bạn:", error);
    } finally {
      setLoading(false);
    }
  };

  // NẾU CÓ PROFILE NÀO ĐÓ ĐANG ĐƯỢC CHỌN THÌ RENDER PROFILE ĐÓ
  console.log("🚀 ~ FriendsPage ~ selectedProfile:", selectedProfile)
  if (selectedProfile) {
    return (
      <div className="friends-page" style={{ padding: 0, backgroundColor: 'transparent' }}>
        <Profile 
          userData={selectedProfile} 
          isOtherProfile={true} 
          onBack={() => setSelectedProfile(null)} 
        />
      </div>
    );
  }

  const handleProfileClick = async (friend) => {
    console.log("🚀 ~ handleProfileClick ~ friend:", friend)
    try {
      setLoading(true);
      const res = await api.post(`/api/blockLists/check-block?targetUserId=${friend.userId}`);
      if (res.data === true) {
        alert("Bạn không thể xem trang cá nhân của người này vì đã bị chặn hoặc bạn đã chặn họ.");
      } else {
        setSelectedProfile(friend);
      }
    } catch (error) {
      console.error("Lỗi khi kiểm tra block:", error);
      // Fallback: cứ cho phép vào nếu lỗi mạng
      setSelectedProfile(friend);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="friends-page">
      <div className="friends-header">
        <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
          {onBackToChat && (
            <button
              className="btn-back"
              onClick={onBackToChat}
              title="Quay lại chat"
            >
              <ArrowLeft size={20} />
            </button>
          )}
          <h2>Bạn bè</h2>
        </div>
        <div className="friends-search">
          <Search size={18} className="search-icon" />
          <input
            type="text"
            placeholder="Tìm kiếm bạn bè..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      <div className="friends-tabs">
        <button
          className={`tab-btn ${activeTab === "friends" ? "active" : ""}`}
          onClick={() => setActiveTab("friends")}
        >
          <Users size={18} />
          Danh sách bạn bè
          {friends.length > 0 && (
            <span className="tab-count">{friends.length}</span>
          )}
        </button>
        <button
          className={`tab-btn ${activeTab === "requests" ? "active" : ""}`}
          onClick={() => setActiveTab("requests")}
        >
          <UserPlus size={18} />
          Yêu cầu kết bạn
          {friendRequests.length > 0 && (
            <span className="tab-count">{friendRequests.length}</span>
          )}
        </button>
        <button
          className={`tab-btn ${activeTab === "add" ? "active" : ""}`}
          onClick={() => setActiveTab("add")}
        >
          <Globe size={18} />
          Khám phá
        </button>
      </div>

      <div className="friends-content">
        {/* TAB DANH SÁCH BẠN BÈ */}
        {activeTab === "friends" && (
          <div className="friends-list">
            {filteredFriends.length > 0 ? (
              <div className="friends-grid">
                {filteredFriends.map((friend) => (
                  <div 
                    key={friend.userId} 
                    className="friend-card" 
                    onClick={() => handleProfileClick(friend)}
                    style={{ cursor: "pointer" }}
                    title="Xem trang cá nhân"
                  >
                    <div className="friend-avatar">
                      {friend.avatarUrl ? (
                        <img
                          src={`http://localhost:8080${friend.avatarUrl}`}
                          alt={friend.name}
                        />
                      ) : (
                        <div className="avatar-placeholder">
                          {friend.name?.charAt(0).toUpperCase()}
                        </div>
                      )}
                    </div>
                    <div className="friend-info">
                      <h3>{friend.name}</h3>
                      <p>{friend.email}</p>
                    </div>
                    <button
                      className="btn-remove-friend"
                      onClick={(e) => handleRemoveFriend(friend.userId, e)}
                      disabled={loading}
                      title="Xóa bạn"
                    >
                      <UserX size={18} />
                    </button>
                  </div>
                ))}
              </div>
            ) : (
              <div className="empty-state">
                <p>
                  {searchQuery
                    ? "Không tìm thấy bạn bè nào"
                    : "Bạn chưa có bạn bè nào"}
                </p>
              </div>
            )}
          </div>
        )}

        {/* TAB YÊU CẦU KẾT BẠN */}
        {activeTab === "requests" && (
          <div className="requests-list">
            {filteredRequests.length > 0 ? (
              <div className="requests-grid">
                {filteredRequests.map((request) => (
                  <div key={request.requestId} className="request-card">
                    <div className="request-avatar">
                      {request.senderAvatarUrl ? (
                        <img
                          src={`http://localhost:8080${request.senderAvatarUrl}`}
                          alt={request.senderName}
                        />
                      ) : (
                        <div className="avatar-placeholder">
                          {request.senderName?.charAt(0).toUpperCase()}
                        </div>
                      )}
                    </div>
                    <div className="request-info">
                      <h3>{request.senderName}</h3>
                      <p>{request.senderEmail}</p>
                      <span className="request-time">
                        {new Date(request.dateSend).toLocaleDateString("vi-VN")}
                      </span>
                    </div>
                    <div className="request-actions">
                      <button
                        className="btn-accept"
                        onClick={() => handleAcceptRequest(request.requestId)}
                        disabled={loading}
                        title="Chấp nhận"
                      >
                        <UserCheck size={18} />
                      </button>
                      <button
                        className="btn-reject"
                        onClick={() => handleRejectRequest(request.requestId)}
                        disabled={loading}
                        title="Từ chối"
                      >
                        <UserX size={18} />
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="empty-state">
                <div className="empty-icon">
                  <UserPlus size={48} />
                </div>
                <h3>Không có yêu cầu kết bạn</h3>
                <p>Khi có người gửi yêu cầu kết bạn, nó sẽ xuất hiện ở đây.</p>
              </div>
            )}
          </div>
        )}

        {/* TAB TÌM BẠN MỚI (GLOBAL KHÁM PHÁ) */}
        {activeTab === "add" && (
          <div className="friends-list">
            {filteredAllUsers.length > 0 ? (
              <div className="friends-grid">
                {filteredAllUsers.map((userObj) => (
                  <div 
                    key={userObj.id} 
                    className="friend-card" 
                    onClick={() => handleProfileClick({ ...userObj, userId: userObj.id })}
                    style={{ cursor: "pointer" }}
                    title="Xem trang cá nhân"
                  >
                    <div className="friend-avatar">
                      {userObj.avatarUrl ? (
                        <img 
                          src={`http://localhost:8080${userObj.avatarUrl}`} 
                          alt={userObj.name} 
                        />
                      ) : (
                        <div className="avatar-placeholder">
                          {userObj.name?.charAt(0).toUpperCase()}
                        </div>
                      )}
                      {userObj.isOnline && <span className="status-indicator online"></span>}
                    </div>
                    <div className="friend-info">
                      <h3>{userObj.name}</h3>
                      <p>{userObj.email}</p>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="empty-state">
                <div className="empty-icon">
                  <Globe size={48} />
                </div>
                <h3>Không tìm thấy người dùng</h3>
                <p>Thử tìm kiếm với từ khóa khác.</p>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default FriendsPage;
