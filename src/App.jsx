import { useState } from "react";
import {
  Search,
  MoreHorizontal,
  Phone,
  Video,
  Info,
  Smile,
  Image,
  Mic,
  Send,
  Settings,
  Users,
  UserPlus,
  UserX,
  Shield,
  HelpCircle,
} from "lucide-react";

import "./App.css";

function App() {
  const [selectedChat, setSelectedChat] = useState("1");
  const [messageInput, setMessageInput] = useState("");
  const [showMenu, setShowMenu] = useState(false);

  const toggleMenu = () => {
    setShowMenu(!showMenu);
  };

  const closeMenu = () => {
    setShowMenu(false);
  };

  const conversations = [
    {
      id: "1",
      name: "Nguyen Van A",
      avatar:
        "https://images.pexels.com/photos/1587009/pexels-photo-1587009.jpeg?auto=compress&cs=tinysrgb&w=100",
      lastMessage: "Hey, how are you?",
      timestamp: "2m ago",
      unread: true,
    },
    {
      id: "2",
      name: "Tran Thi B",
      avatar:
        "https://images.pexels.com/photos/1239291/pexels-photo-1239291.jpeg?auto=compress&cs=tinysrgb&w=100",
      lastMessage: "See you tomorrow!",
      timestamp: "1h ago",
    },
    {
      id: "3",
      name: "Le Van C",
      avatar:
        "https://images.pexels.com/photos/1222271/pexels-photo-1222271.jpeg?auto=compress&cs=tinysrgb&w=100",
      lastMessage: "Thanks for your help",
      timestamp: "3h ago",
    },
  ];

  const messages = [
    {
      id: "1",
      sender: "other",
      text: "Hey, how are you?",
      timestamp: "10:30 AM",
    },
    {
      id: "2",
      sender: "me",
      text: "I'm good, thanks! How about you?",
      timestamp: "10:32 AM",
    },
    {
      id: "3",
      sender: "other",
      text: "Doing great! Want to grab coffee later?",
      timestamp: "10:35 AM",
    },
    {
      id: "4",
      sender: "me",
      text: "Sure, sounds good! What time?",
      timestamp: "10:36 AM",
    },
  ];

  return (
    <div className="messenger">
      {/* Sidebar */}
      <div className="sidebar">
        <div className="sidebar-header">
          <h1>Chats</h1>
          <div className="menu-container">
            <button className="icon-btn" onClick={toggleMenu}>
              <MoreHorizontal size={20} />
            </button>

            {showMenu && (
              <div className="dropdown-menu">
                <button onClick={closeMenu}>
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

        <div className="search-bar">
          <Search size={18} className="search-icon" />
          <input type="text" placeholder="Search Messenger" />
        </div>

        <div className="conversation-list">
          {conversations.map((conv) => (
            <div
              key={conv.id}
              onClick={() => setSelectedChat(conv.id)}
              className={`conversation-item ${
                selectedChat === conv.id ? "active" : ""
              }`}
            >
              <div className="avatar">
                <img src={conv.avatar} alt={conv.name} />
                <span className="online-dot"></span>
              </div>
              <div className="conversation-text">
                <div className="conversation-header">
                  <h3>{conv.name}</h3>
                  <span>{conv.timestamp}</span>
                </div>
                <p className={conv.unread ? "unread" : ""}>
                  {conv.lastMessage}
                </p>
              </div>
              {conv.unread && <div className="unread-dot"></div>}
            </div>
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
          {messages.map((msg) => (
            <div
              key={msg.id}
              className={`message ${msg.sender === "me" ? "me" : "other"}`}
            >
              <div className="message-bubble">{msg.text}</div>
              <span className="timestamp">{msg.timestamp}</span>
            </div>
          ))}
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

export default App;
