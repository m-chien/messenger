import {
  HelpCircle,
  Image,
  Info,
  Mic,
  Moon,
  MoreHorizontal,
  Phone,
  Search,
  Send,
  Settings,
  Shield,
  Smile,
  Sun,
  UserPlus,
  Users,
  UserX,
  Video,
} from "lucide-react";
import { useEffect, useState } from "react";
import "./../Style/HomePage.css";
import { format } from "date-fns";
import { useTheme } from "./../Component/ThemeContext.jsx";
import useFetchAll from "../Hook/useFetchAll";
import { ConversationItem } from "./../Page/ConversationItem";

function HomePage() {
  const [selectedChat, setSelectedChat] = useState(null);
  const [messageInput, setMessageInput] = useState("");
  const [showMenu, setShowMenu] = useState(false);
  const { theme, toggleTheme } = useTheme();

  const toggleMenu = () => {
    setShowMenu(!showMenu);
  };

  const formatTimeWithLibrary = (isoString) => {
    if (!isoString) return "";

    // 'HH:mm' là chuỗi định dạng (HH=Giờ 24h, mm=Phút)
    return format(new Date(isoString), "HH:mm");
  };

  const { data: chatRooms, loading: chatRoomsLoading } =
    useFetchAll("/chatRooms/user/1");
  const { data: messagesData, loading: messagesLoading } = useFetchAll(
    `/messages/chatroom/${selectedChat}`,
  );
  console.log("🚀 ~ HomePage ~ messagesData:", messagesData);

  useEffect(() => {
    if (chatRooms && chatRooms.length > 0 && !selectedChat) {
      setSelectedChat(chatRooms[0].idChatroom);
    }
  }, [chatRooms]);

  return (
    <div className="messenger">
      {/* Sidebar */}
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
              selectedChat={selectedChat}
              setSelectedChat={setSelectedChat}
            />
          ))}
        </div>
      </div>

      {/* Chat area */}
      <div className="chat">
        <div className="chat-header">
          <div className="chat-user">
            <img
              src="https://images.pexels.com/photos/1587009/pexels-photo-1587009.jpeg?auto=compress&cs=tinysrgb&w=100"
              alt="User"
            />
            <div>
              <h2>Nguyen Van A</h2>
              <p className="active-text">Active now</p>
            </div>
          </div>
          <div className="chat-actions">
            <button className="icon-btn">
              <Phone size={20} />
            </button>
            <button className="icon-btn">
              <Video size={20} />
            </button>
            <button className="icon-btn">
              <Info size={20} />
            </button>
          </div>
        </div>

        <div className="chat-messages">
          {messagesData.map((msg, index) => {
            const nextMsg = messagesData[index + 1];
            const isMe = msg.userId === 1;
            const showAvatar = !nextMsg || nextMsg.userId !== msg.userId;

            return (
              <div key={msg.id} className={`message ${isMe ? "me" : "other"}`}>
                <div className="avatar-wrapper">
                  {!isMe && showAvatar && (
                    <img
                      src="/images.png"
                      alt="User"
                      className="message-avatar"
                    />
                  )}
                </div>

                <div className="message-content">
                  <div className="message-bubble">{msg.content}</div>
                  <span className="timestamp">
                    {formatTimeWithLibrary(msg.dateSend)}
                  </span>
                </div>
              </div>
            );
          })}
        </div>

        <div className="chat-input">
          <button className="icon-btn">
            <Image size={20} />
          </button>
          <button className="icon-btn">
            <Smile size={20} />
          </button>
          <input
            type="text"
            placeholder="Aa"
            value={messageInput}
            onChange={(e) => setMessageInput(e.target.value)}
          />
          {messageInput.trim() ? (
            <button className="icon-btn send-btn">
              <Send size={20} />
            </button>
          ) : (
            <button className="icon-btn">
              <Mic size={20} />
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

export default HomePage;
