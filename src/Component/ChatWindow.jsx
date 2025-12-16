import React from "react";
import {
  Image,
  Info,
  Mic,
  Phone,
  Send,
  Smile,
  Video,
} from "lucide-react";
import { format, isToday } from "date-fns";
import { vi } from "date-fns/locale";

const ChatWindow = ({
  selectedChat,
  messages,
  myUserId,
  messageInput,
  setMessageInput,
  handleSendMessage,
}) => {
  const formatTimeWithLibrary = (isoString) => {
    if (!isoString) return "";
    const date = new Date(isoString);

    if (isToday(date)) {
      return format(date, "HH:mm");
    } else {
      return format(date, "HH:mm d MMM, yyyy", { locale: vi });
    }
  };

  return (
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
        {messages.map((msg, index) => {
          const nextMsg = messages[index + 1];
          const preMsg = messages[index - 1];
          const isMe = Number(msg.userId) === myUserId;
          const showAvatar = !nextMsg || nextMsg.userId !== msg.userId;
          const showTime =
            !preMsg ||
            new Date(msg.dateSend).getTime() -
              new Date(preMsg.dateSend).getTime() >
              10 * 60 * 1000;

          return (
            <React.Fragment key={msg.id || index}>
              {showTime && (
                <div className="time">
                  {formatTimeWithLibrary(msg.dateSend)}
                </div>
              )}
              <div className={`message ${isMe ? "me" : "other"}`}>
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
            </React.Fragment>
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
  );
};

export default ChatWindow;
