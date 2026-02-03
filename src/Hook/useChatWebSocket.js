import { useEffect, useRef, useState } from "react";
import { useWebSocket } from "../Context/WebSocketContext";

export function useChatWebSocket(
  selectedChatId,
  token,
  userId,
  onSidebarUpdate
) {
  const { client, connected } = useWebSocket();
  const subscriptionChatRef = useRef(null);
  const subscriptionSidebarRef = useRef(null);
  const [messages, setMessages] = useState([]);
  
  // Ref to hold client for sendMessage usage
  const internalClientRef = useRef(client);

  useEffect(() => {
    internalClientRef.current = client;
  }, [client]);

  useEffect(() => {
    setMessages([]);
  }, [selectedChatId]);

  // 1. SUBSCRIBE KÊNH SIDEBAR (Luôn lắng nghe dù đang ở đâu)
  useEffect(() => {
    if (!connected || !client || !userId || !onSidebarUpdate) return;
    
    // Hủy đăng ký cũ nếu có để tránh duplicate
    if (subscriptionSidebarRef.current)
        subscriptionSidebarRef.current.unsubscribe();

    console.log("✅ Subscribing Sidebar channel:", `/topic/user/${userId}/sidebar`);
    
    subscriptionSidebarRef.current = client.subscribe(
        `/topic/user/${userId}/sidebar`,
        (response) => {
        const sidebarDto = JSON.parse(response.body);
        console.log("🚀 ~ useChatWebSocket ~ sidebarDto:", sidebarDto)
        onSidebarUpdate(sidebarDto);
        }
    );

    return () => {
        if (subscriptionSidebarRef.current) subscriptionSidebarRef.current.unsubscribe();
    };
  }, [connected, client, userId]);

  // 3. SUBSCRIBE KÊNH CHAT ROOM (Chỉ khi chọn phòng)
  useEffect(() => {
    if (!connected || !selectedChatId || !client) return;

    if (subscriptionChatRef.current) subscriptionChatRef.current.unsubscribe();

    console.log("✅ Subscribing ChatRoom:", selectedChatId);

    // Lắng nghe tin nhắn chi tiết để hiện vào khung chat
    subscriptionChatRef.current = client.subscribe(
      `/topic/chatroom/${selectedChatId}`,
      (response) => {
        const msgBody = JSON.parse(response.body);
        setMessages((prev) => [...prev, msgBody]);
      }
    );

    // Call API đánh dấu đã đọc (nếu cần)
    fetch(`http://localhost:8080/chatRoomUsers/${selectedChatId}/read-latest`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
    }).catch(console.error);

    return () => {
      if (subscriptionChatRef.current)
        subscriptionChatRef.current.unsubscribe();
    };
  }, [selectedChatId, connected, client]);

  const sendMessage = (messagePayload) => {
    if (internalClientRef.current && internalClientRef.current.connected) {
      internalClientRef.current.send(
        `/app/chat.send/${messagePayload.chatroom}`,
        {},
        JSON.stringify(messagePayload)
      );
    } else {
        console.warn("⚠️ WebSocket not connected, cannot send message");
    }
  };

  return { messages, sendMessage, connected };
}
