import React, { useState, useEffect, useMemo } from "react";
import { ArrowLeft, UserX, Search, Shield, ShieldOff } from "lucide-react";
import useFetchAll from "../Hook/useFetchAll";
import { api } from "../Api/Api";
import "../Style/FriendsPage.css"; // Reuse friends page styling for consistency

function RestrictedAccountsPage({ onBackToChat }) {
  const token = sessionStorage.getItem("accessToken");
  const [searchQuery, setSearchQuery] = useState("");
  const [restrictedUsers, setRestrictedUsers] = useState([]);
  const [loading, setLoading] = useState(false);

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

  // Fetch TẤT CẢ user (trừ bản thân)
  const { data: allUsersData } = useFetchAll("/users");
  
  // Fetch block lists
  const { data: blockListsData, refetch: refetchBlocks } = useFetchAll("/api/blockLists");

  useEffect(() => {
    if (allUsersData && blockListsData && myUserId) {
      // Tìm các block items do tôi (blocker) chặn người khác (blocked)
      const myBlocks = blockListsData.filter(b => b.blocker === myUserId);
      
      // Lấy thông tin user tương ứng
      const blockedUserDetails = myBlocks.map(block => {
        const user = allUsersData.find(u => u.id === block.blocked);
        return {
          ...user,
          blockId: block.id,
          blockedDate: block.blockedDate
        };
      }).filter(u => u.id != null); // Lọc bỏ nếu không map được

      setRestrictedUsers(blockedUserDetails);
    }
  }, [allUsersData, blockListsData, myUserId]);

  // Lọc danh sách theo tìm kiếm
  const filteredRestricted = restrictedUsers.filter(
    (user) =>
      user.name?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      user.email?.toLowerCase().includes(searchQuery.toLowerCase()),
  );

  // Hàm bỏ chặn (hiện tại có thể api chưa handle mượt, nhưng viết sẵn)
  const handleUnblock = async (blockId) => {
    try {
      setLoading(true);
      await api.delete(`/api/blockLists/${blockId}`);
      // Xóa thành công, refetch lại danh sách
      if (refetchBlocks) {
          await refetchBlocks();
      } else {
          setRestrictedUsers(prev => prev.filter(b => b.blockId !== blockId));
      }
    } catch (error) {
      console.error("Lỗi bỏ chặn:", error);
      alert("Lỗi khi bỏ chặn người dùng này!");
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
          <h2>Tài khoản hạn chế</h2>
        </div>
        <div className="friends-search">
          <Search size={18} className="search-icon" />
          <input
            type="text"
            placeholder="Tìm kiếm tài khoản..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      <div className="friends-tabs" style={{ padding: '0 24px', borderBottom: '1px solid var(--border-color)', marginBottom: '20px' }}>
         <div style={{ padding: '16px 0', display: 'flex', alignItems: 'center', gap: '10px', color: 'var(--text-color)', fontWeight: 600 }}>
             <Shield size={20} color="#ff4d4f" />
             <span>Danh sách đã chặn ({filteredRestricted.length})</span>
         </div>
      </div>

      <div className="friends-content">
          <div className="friends-list">
            {filteredRestricted.length > 0 ? (
              <div className="friends-grid">
                {filteredRestricted.map((user) => (
                  <div 
                    key={user.id} 
                    className="friend-card" 
                  >
                    <div className="friend-avatar">
                      {user.avatarUrl ? (
                        <img
                          src={`http://localhost:8080${user.avatarUrl}`}
                          alt={user.name}
                        />
                      ) : (
                        <div className="avatar-placeholder">
                          {user.name?.charAt(0).toUpperCase()}
                        </div>
                      )}
                    </div>
                    <div className="friend-info">
                      <h3>{user.name}</h3>
                      <p>{user.email}</p>
                    </div>
                    <button
                      className="btn-remove-friend"
                      onClick={(e) => {
                          e.stopPropagation();
                          handleUnblock(user.blockId);
                      }}
                      disabled={loading}
                      title="Bỏ chặn"
                      style={{ backgroundColor: 'rgba(255, 77, 79, 0.1)', color: '#ff4d4f' }}
                    >
                      Bỏ chặn
                    </button>
                  </div>
                ))}
              </div>
            ) : (
              <div className="empty-state">
                <div className="empty-icon">
                  <ShieldOff size={48} />
                </div>
                <h3>{searchQuery ? "Không tìm thấy kết quả" : "Không có tài khoản bị hạn chế"}</h3>
                <p>{searchQuery ? "Thử tìm với tên hoặc email khác." : "Bạn chưa chặn bất kỳ ai."}</p>
              </div>
            )}
          </div>
      </div>
    </div>
  );
}

export default RestrictedAccountsPage;
