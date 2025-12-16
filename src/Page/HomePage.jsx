import { useEffect, useState } from "react";
import "./../Style/HomePage.css";
import { useTheme } from "./../Component/ThemeContext.jsx";
import { useChatWebSocket } from "../Hook/useChatWebSocket.js";
import useFetchAll from "../Hook/useFetchAll";
import Sidebar from "../Component/Sidebar";
import ChatWindow from "../Component/ChatWindow";

function HomePage() {
  const token = sessionStorage.getItem("accessToken");
  const [selectedChat, setSelectedChat] = useState(null);
  const [messageInput, setMessageInput] = useState("");
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
    sendMessage({
      content: messageInput,
      chatroom: selectedChat.idChatroom,
    });
    setMessageInput("");
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
      <Sidebar
        theme={theme}
        toggleTheme={toggleTheme}
        chatRooms={chatRooms}
        selectedChat={selectedChat}
        onSelectChat={handleChatRoomSelect}
      />

      <ChatWindow
        selectedChat={selectedChat}
        messages={AllMessages}
        myUserId={myUserId}
        messageInput={messageInput}
        setMessageInput={setMessageInput}
        handleSendMessage={handleSendMessage}
      />
    </div>
  );
}

export default HomePage;
