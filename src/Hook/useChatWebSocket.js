import { useEffect, useRef, useState } from "react";
import SockJS from "sockjs-client";
import { over } from "stompjs";

export function useChatWebSocket(selectedChat, token) {
  const stompClientRef = useRef(null);
  const subscriptionRef = useRef(null);
  const [messages, setMessages] = useState([]);
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    const sock = new SockJS("http://localhost:8080/ws");
    const client = over(sock);
    stompClientRef.current = client;

    client.connect({ Authorization: `Bearer ${token}` }, () =>
      setConnected(true),
    );

    return () => {
      if (client && client.connected) client.disconnect();
    };
  }, [token]);

  useEffect(() => {
    if (!connected || !selectedChat) return;

    if (subscriptionRef.current) subscriptionRef.current.unsubscribe();

    subscriptionRef.current = stompClientRef.current.subscribe(
      `/topic/chatroom/${selectedChat}`,
      (response) => {
        const msgBody = JSON.parse(response.body);
        setMessages((prev) => [...prev, msgBody]);
      },
    );

    return () => {
      if (subscriptionRef.current) subscriptionRef.current.unsubscribe();
    };
  }, [selectedChat, connected]);

  const sendMessage = (message) => {
    if (stompClientRef.current && stompClientRef.current.connected) {
      stompClientRef.current.send(
        `/app/chat.send/${selectedChat}`,
        {},
        JSON.stringify(message),
      );
    }
  };

  return { messages, sendMessage, connected };
}
