import { useCallback, useEffect, useState, useMemo, useRef } from "react";
import "./../Style/HomePage.css";
import { useTheme } from "./../Component/ThemeContext.jsx";
import { useChatWebSocket } from "../Hook/useChatWebSocket.js";
import useFetchAll from "../Hook/useFetchAll";
import Sidebar from "../Component/Sidebar";
import ChatWindow from "../Component/ChatWindow";

function HomePage() {
  const token = sessionStorage.getItem("accessToken");
  const { theme, toggleTheme } = useTheme();

  // State
  const [selectedChat, setSelectedChat] = useState(null);
  const selectedChatRef = useRef(selectedChat);
  console.log("🚀 ~ HomePage ~ selectedChat:", selectedChat);
  const [messageInput, setMessageInput] = useState("");
  const [chatRooms, setChatRooms] = useState([]);
  console.log("🚀 ~ HomePage ~ chatRooms:", chatRooms);

  // 1. Lấy userId từ token (Cần thiết để subscribe kênh sidebar)
  const myUserId = useMemo(() => {
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split(".")[1]));
      return Number(payload.sub); // Đổi 'sub' thành field chứa ID thực tế của bạn
    } catch (e) {
      console.error("Lỗi decode token:", e);
      return null;
    }
  }, [token]);

  // 2. Load danh sách phòng ban đầu
  const { data: fetchedChatRooms } = useFetchAll("/chatRooms/user");

  useEffect(() => {
    if (fetchedChatRooms && fetchedChatRooms.length > 0) {
      setChatRooms(fetchedChatRooms);
      if (!selectedChat) setSelectedChat(fetchedChatRooms[0]);
    }
  }, [fetchedChatRooms]);

  useEffect(() => {
    selectedChatRef.current = selectedChat;
  }, [selectedChat]);

  // 3. HÀM XỬ LÝ KHI BE BẮN NOTI SIDEBAR VỀ
  // DTO BE gửi về: { roomId: 1, content: "abc", dateSend: "...", userId: 5 }
  const handleSidebarUpdate = useCallback(
    (sidebarDto) => {
      setChatRooms((prevRooms) => {
        const idx = prevRooms.findIndex(
          (r) => String(r.idChatroom) === String(sidebarDto.chatroomId)
        );

        if (idx === -1) return prevRooms;

        const isCurrentRoom =
          String(selectedChatRef.current?.idChatroom) ===
          String(sidebarDto.chatroomId);

        const updatedRoom = {
          ...prevRooms[idx],
          content: sidebarDto.lastMessage,
          dateSend: sidebarDto.time,

          // 👉 QUY TẮC CỐT LÕI
          isUnread: isCurrentRoom ? 0 : 1,
          unreadCount: isCurrentRoom
            ? 0
            : (prevRooms[idx].unreadCount || 0) + 1,
        };

        const updatedRooms = [...prevRooms];
        updatedRooms.splice(idx, 1);
        updatedRooms.unshift(updatedRoom);

        // Nếu đang mở phòng này → sync selectedChat
        if (isCurrentRoom) {
          setSelectedChat(updatedRoom);
        }

        return updatedRooms;
      });
    },
    []
  );

  // 4. Fetch tin nhắn lịch sử của phòng đang chọn
  const { data: messagesData } = useFetchAll(
    selectedChat ? `/messages/chatroom/${selectedChat.idChatroom}` : null
  );

  // 5. Khởi tạo WebSocket
  const { messages, sendMessage } = useChatWebSocket(
    selectedChat?.idChatroom,
    token,
    myUserId,
    handleSidebarUpdate
  );

  const AllMessages = [...(messagesData || []), ...messages];

  // 6. Xử lý gửi tin
  const handleSendMessage = () => {
    if (!messageInput.trim()) return;

    sendMessage({
      content: messageInput,
      chatroom: selectedChat.idChatroom,
    });

    setMessageInput("");
  };

  const handleChatRoomSelect = (chatroom) => {
    console.log("🚀 ~ handleChatRoomSelect ~ chatroom:", chatroom);
    setSelectedChat(chatroom);

    // clear unread / unreadCount cho phòng được mở
    setChatRooms((prev) =>
      prev.map((r) =>
        String(r.idChatroom) === String(chatroom.idChatroom)
          ? {
              ...r,
              lastSeenMessageId: r.idMessage,
              isUnread: 0,
              unreadCount: 0,
            }
          : r
      )
    );
  };

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
