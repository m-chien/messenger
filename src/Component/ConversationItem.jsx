import { MoreHorizontal } from "lucide-react";
import React, { useState } from "react";
import "../Style/ConversationItem.css";
import timeAgo from "../Hook/TimeAgo";

export const ConversationItem = ({ conv, selectedChat, setSelectedChat }) => {
  const [showMenu, setShowMenu] = useState(false);

  const handleMenuClick = (e) => {
    e.stopPropagation();
    setShowMenu(!showMenu);
  };

  const handleMenuItemClick = (e, action) => {
    e.stopPropagation();
    console.log(`Action: ${action}`);
    setShowMenu(false);
  };

  return (
    <div
      key={conv.idChatroom}
      onClick={() => setSelectedChat(conv)}
      className={`conversation-item ${
        selectedChat === conv.idChatroom ? "active" : ""
      }`}
    >
      <div className="avatar">
        <img src={`http://localhost:8080${conv.logo}`} alt={conv.name} />
        {conv.hasOnlineUser == 1 && <span className="online-dot"></span>}
      </div>
      <div className="conversation-text">
        <div className="conversation-header">
          <h3>{conv.name}</h3>
          <span>{timeAgo(conv.dateSend)}</span>
        </div>
        <p className={conv.isUnread ? "unread" : ""}>{conv.content}</p>
      </div>
      {conv.unread && <div className="unread-dot"></div>}
      <div className="conversation-menu-container">
        <button
          className="conversation-menu-btn"
          onClick={handleMenuClick}
          title="More options"
        >
          <MoreHorizontal size={18} />
        </button>
        {showMenu && (
          <div className="conversation-dropdown-menu">
            <button
              className="menu-option"
              onClick={(e) => handleMenuItemClick(e, "delete")}
            >
              Xóa cuộc trò chuyện
            </button>
            <button
              className="menu-option"
              onClick={(e) => handleMenuItemClick(e, "unfriend")}
            >
              Hủy kết bạn
            </button>
            <button
              className="menu-option danger"
              onClick={(e) => handleMenuItemClick(e, "block")}
            >
              Chặn
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
