import React, { useState } from "react";
import {
  HelpCircle,
  Moon,
  MoreHorizontal,
  Search,
  Settings,
  Shield,
  Sun,
  UserPlus,
  Users,
  UserX,
} from "lucide-react";
import { ConversationItem } from "./ConversationItem";

const Sidebar = ({
  theme,
  toggleTheme,
  chatRooms,
  selectedChat,
  onSelectChat,
}) => {
  const [showMenu, setShowMenu] = useState(false);

  const toggleMenu = () => {
    setShowMenu(!showMenu);
  };

  return (
    <div className="sidebar">
      <div className="sidebar-header">
        <h1>ChienIBox</h1>
        <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
          <button
            className="icon-btn theme-toggle"
            onClick={toggleTheme}
            title="Đổi chế độ sáng/tối"
          >
            {theme === "dark" ? <Sun size={20} /> : <Moon size={20} />}
          </button>

          <div className="menu-container">
            <button className="icon-btn" onClick={toggleMenu}>
              <MoreHorizontal size={20} />
            </button>

            {showMenu && (
              <div className="dropdown-menu">
                <button className="menu-item" type="button">
                  <Settings size={18} />
                  <span>Tùy chọn</span>
                </button>
                <button>
                  <Users size={18} />
                  <span>Danh sách bạn bè</span>
                </button>
                <button>
                  <UserPlus size={18} />
                  <span>Kết bạn</span>
                </button>
                <button>
                  <UserX size={18} />
                  <span>Tài khoản đã hạn chế</span>
                </button>
                <button>
                  <Shield size={18} />
                  <span>Quyền riêng tư và an toàn</span>
                </button>
                <button>
                  <HelpCircle size={18} />
                  <span>Trợ giúp</span>
                </button>
              </div>
            )}
          </div>
        </div>
      </div>

      <div className="search-bar">
        <Search size={18} className="search-icon" />
        <input type="text" placeholder="Search Messenger" />
      </div>

      <div className="conversation-list">
        {chatRooms.map((conv) => (
          <ConversationItem
            key={conv.idChatroom}
            conv={conv}
            selectedChat={selectedChat?.idChatroom}
            setSelectedChat={() => onSelectChat(conv)}
          />
        ))}
      </div>
    </div>
  );
};

export default Sidebar;
