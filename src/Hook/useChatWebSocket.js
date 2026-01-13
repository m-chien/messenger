import { useEffect, useRef, useState } from "react";
import SockJS from "sockjs-client";
import { over } from "stompjs";

export function useChatWebSocket(
  selectedChatId,
  token,
  userId,
  onSidebarUpdate
) {
  const stompClientRef = useRef(null);
  const subscriptionChatRef = useRef(null);
  const subscriptionSidebarRef = useRef(null);
  const [messages, setMessages] = useState([]);
  console.log("🚀 ~ useChatWebSocket ~ messages:", messages)
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    setMessages([]);
  }, [selectedChatId]);

  // 1. Kết nối WebSocket
  useEffect(() => {
    if (!token) return;

    const sock = new SockJS("http://localhost:8080/ws");
    const client = over(sock);
    // Tắt log debug của stompjs cho đỡ rác console
    client.debug = () => {};
    stompClientRef.current = client;

    client.connect({ Authorization: `Bearer ${token}` }, () => {
      setConnected(true);

      // 2. SUBSCRIBE KÊNH SIDEBAR (Luôn lắng nghe dù đang ở đâu)
      if (userId && onSidebarUpdate) {
        // Hủy đăng ký cũ nếu có để tránh duplicate
        if (subscriptionSidebarRef.current)
          subscriptionSidebarRef.current.unsubscribe();

        subscriptionSidebarRef.current = client.subscribe(
          `/topic/user/${userId}/sidebar`,
          (response) => {
            const sidebarDto = JSON.parse(response.body);
            console.log("🚀 ~ useChatWebSocket ~ sidebarDto:", sidebarDto)
            // Gọi callback để HomePage xử lý update UI
            onSidebarUpdate(sidebarDto);
          }
        );
      }
    });

    return () => {
      if (client && client.connected) client.disconnect();
    };
  }, [token, userId]); // Chỉ kết nối lại khi token hoặc userId thay đổi

  // 3. SUBSCRIBE KÊNH CHAT ROOM (Chỉ khi chọn phòng)
  useEffect(() => {
    if (!connected || !selectedChatId || !stompClientRef.current) return;

    if (subscriptionChatRef.current) subscriptionChatRef.current.unsubscribe();

    // Lắng nghe tin nhắn chi tiết để hiện vào khung chat
    subscriptionChatRef.current = stompClientRef.current.subscribe(
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
  }, [selectedChatId, connected]);

  const sendMessage = (messagePayload) => {
    if (stompClientRef.current && stompClientRef.current.connected) {
      stompClientRef.current.send(
        `/app/chat.send/${messagePayload.chatroom}`,
        {},
        JSON.stringify(messagePayload)
      );
    }
  };

  return { messages, sendMessage, connected };
}
