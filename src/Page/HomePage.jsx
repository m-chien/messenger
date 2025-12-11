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
import { format, isToday } from "date-fns";
import { vi } from "date-fns/locale";
import { useTheme } from "./../Component/ThemeContext.jsx";
import { useChatWebSocket } from "../Hook/useChatWebSocket.js";
import useFetchAll from "../Hook/useFetchAll";
import { ConversationItem } from "./../Page/ConversationItem";

function HomePage() {
  const token = sessionStorage.getItem("accessToken");
  const [selectedChat, setSelectedChat] = useState(null);
  const [messageInput, setMessageInput] = useState("");
  const [showMenu, setShowMenu] = useState(false);
  const { theme, toggleTheme } = useTheme();
  const { data: chatRooms, loading: chatRoomsLoading } =
    useFetchAll("/chatRooms/user");
  const { data: messagesData, loading: messagesLoading } = useFetchAll(
    selectedChat ? `/messages/chatroom/${selectedChat.idChatroom}` : null,
  );
  const { messages, sendMessage, connected } = useChatWebSocket(
    selectedChat?.idChatroom,
    token,
  );
  const AllMessages = [...messagesData, ...messages];

  const handleSendMessage = () => {
    console.log("Sending message:", messageInput);
    console.log("Selected chat:", selectedChat);
    sendMessage({
      content: messageInput,
      dateSend: new Date().toISOString(),
      chatroom: selectedChat.idChatroom,
    });
    setMessageInput("");
  };

  const toggleMenu = () => {
    setShowMenu(!showMenu);
  };

  const formatTimeWithLibrary = (isoString) => {
    if (!isoString) return "";
    const date = new Date(isoString);

    if (isToday(date)) {
      return format(date, "HH:mm");
    } else {
      return format(date, "HH:mm d MMM, yyyy", { locale: vi });
    }
  };

  useEffect(() => {
    if (chatRooms && chatRooms.length > 0 && !selectedChat) {
      setSelectedChat(chatRooms[0]);
    }
  }, [chatRooms]);

  const handleChatRoomSelect = (chatroom) => {
    setSelectedChat(chatroom);
  };

  const getMyUserId = () => {
    const token = sessionStorage.getItem("accessToken");
    if (!token) return null;
    const payload = JSON.parse(atob(token.split(".")[1]));
    return Number(payload.sub);
  };

  const myUserId = getMyUserId();

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
              selectedChat={selectedChat?.idChatroom}
              setSelectedChat={() => handleChatRoomSelect(conv)}
            />
          ))}
        </div>
      </div>

      {/* Chat area */}
      <div className="chat">
        <div className="chat-header">
          <div className="chat-user">
            <img
              src={`http://localhost:8080${selectedChat?.logo}`}
              alt="User"
            />
            <div>
              <h2>{selectedChat?.name}</h2>
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
          {AllMessages.map((msg, index) => {
            const nextMsg = AllMessages[index + 1];
            const isMe = Number(msg.userId) === myUserId;
            const showAvatar = !nextMsg || nextMsg.userId !== msg.userId;

            return (
              <div
                key={msg.id || index}
                className={`message ${isMe ? "me" : "other"}`}
              >
                <div className="avatar-wrapper">
                  {!isMe && showAvatar && (
                    <img
                      src={`http://localhost:8080${msg.avatarUrl}`}
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
          <textarea
            className="input"
            placeholder="Aa"
            value={messageInput}
            onChange={(e) => setMessageInput(e.target.value)}
            onKeyDown={(e) => {
              if (
                e.key === "Enter" &&
                !e.shiftKey &&
                messageInput.trim() !== ""
              ) {
                e.preventDefault(); // tránh xuống dòng
                handleSendMessage();
              }
            }}
            rows={1}
          />
          {messageInput.trim() ? (
            <button className="icon-btn send-btn" onClick={handleSendMessage}>
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
