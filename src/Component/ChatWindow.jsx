import React, { useState } from "react";
import { Image, Info, Mic, Phone, Send, Smile, Video } from "lucide-react";
import { format, isToday } from "date-fns";
import { vi } from "date-fns/locale";

const ChatWindow = ({
  selectedChat,
  messages,
  myUserId,
  messageInput,
  setMessageInput,
  handleSendMessage,
  selectedFiles,
  setSelectedFiles,
}) => {
  const canSend = messageInput.trim() !== "" || selectedFiles.length > 0;
  const [previewImage, setPreviewImage] = useState(null);

  const formatTimeWithLibrary = (isoString) => {
    if (!isoString) return "";
    const date = new Date(isoString);

    if (isToday(date)) {
      return format(date, "HH:mm");
    } else {
      return format(date, "HH:mm d MMM, yyyy", { locale: vi });
    }
  };

  const handleSelectFile = (e) => {
    const files = Array.from(e.target.files);
    setSelectedFiles((prev) => [...prev, ...files]);
  };

  const renderAttachment = (att) => {
    if (att.fileType.startsWith("image/")) {
      return (
        <img
          src={att.fileUrl}
          alt={att.fileName}
          className="chat-image"
          onClick={() => setPreviewImage(att.fileUrl)}
        />
      );
    }

    if (att.fileType.startsWith("video/")) {
      return (
        <video controls className="chat-video">
          <source src={att.fileUrl} type={att.fileType} />
        </video>
      );
    }

    return (
      <a
        href={att.fileUrl}
        target="_blank"
        rel="noopener noreferrer"
        className="chat-file"
      >
        📎 {att.fileName}
      </a>
    );
  };

  const fileRef = React.useRef();

  return (
    <div className="chat">
      <div className="chat-header">
        <div className="chat-user">
          <img src={`http://localhost:8080${selectedChat?.logo}`} alt="User" />
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
                  <div className="message-body">
                    {msg.attachments?.length > 0 && (
                      <div className="message-attachments">
                        {msg.attachments.map((att) => (
                          <div key={att.id} className="attachment-item">
                            {renderAttachment(att)}
                          </div>
                        ))}
                      </div>
                    )}

                    {msg.content && (
                      <div className="message-bubble">{msg.content}</div>
                    )}
                  </div>

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
        <button className="icon-btn" onClick={() => fileRef.current.click()}>
          <Image size={25} />
        </button>

        <input type="file" hidden ref={fileRef} onChange={handleSelectFile} />
        <button className="icon-btn">
          <Smile size={25} />
        </button>
        <div className="input-box">
          {selectedFiles.length > 0 && (
            <div className="attachment-preview">
              {selectedFiles.map((file, index) => (
                <div key={index} className="preview-item">
                  <button
                    className="remove-file"
                    onClick={() =>
                      setSelectedFiles((prev) =>
                        prev.filter((_, i) => i !== index)
                      )
                    }
                  >
                    ✕
                  </button>

                  {file.type.startsWith("image/") ? (
                    <img
                      src={URL.createObjectURL(file)}
                      alt={file.name}
                      className="preview-image"
                    />
                  ) : (
                    <div className="preview-file">📎 {file.name}</div>
                  )}
                </div>
              ))}
            </div>
          )}

          <textarea
            className="input"
            placeholder="Aa"
            value={messageInput}
            onChange={(e) => setMessageInput(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter" && !e.shiftKey && canSend) {
                e.preventDefault();
                handleSendMessage();
              }
            }}
            rows={1}
          />
        </div>
        {canSend ? (
          <button className="icon-btn send-btn" onClick={handleSendMessage}>
            <Send size={20} />
          </button>
        ) : (
          <button className="icon-btn">
            <Mic size={20} />
          </button>
        )}
      </div>
      {previewImage && (
        <div
          className="image-preview-overlay"
          onClick={() => setPreviewImage(null)}
        >
          <img
            src={previewImage}
            className="image-preview"
            onClick={(e) => e.stopPropagation()}
          />
          <button className="close-btn" onClick={() => setPreviewImage(null)}>
            ✕
          </button>
        </div>
      )}
    </div>
  );
};

export default ChatWindow;
