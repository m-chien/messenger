import React, { createContext, useContext, useEffect, useRef, useState } from "react";
import SockJS from "sockjs-client";
import { over } from "stompjs";

const WebSocketContext = createContext(null);

export const useWebSocket = () => useContext(WebSocketContext);

export const WebSocketProvider = ({ children }) => {
  const stompClientRef = useRef(null);
  const [connected, setConnected] = useState(false);
  const [userId, setUserId] = useState(null);

  useEffect(() => {
    const token = sessionStorage.getItem("accessToken");
    
    // Attempt to decode userId from token
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split(".")[1]));
         // Adjust 'sub' if your ID is in a different claim
        setUserId(Number(payload.sub));
      } catch (e) {
        console.error("Invalid token:", e);
      }
    }

    if (!token) return;

    if (stompClientRef.current && stompClientRef.current.connected) {
        setConnected(true);
        return;
    }

    const sock = new SockJS("http://localhost:8080/ws");
    const client = over(sock);
    client.debug = () => {}; // Disable debug logs

    client.connect(
      { Authorization: `Bearer ${token}` },
      () => {
        console.log("✅ WebSocket Connected Globally");
        stompClientRef.current = client;
        setConnected(true);
      },
      (err) => {
        console.error("❌ WebSocket Error:", err);
        setConnected(false);
      }
    );

    return () => {
      // Allow connection to persist or cleanup if needed. 
      // Usually, in SPA, we might want to keep it unless logout.
      // For now, let's keep it simplest: disconnect on unmount (refresh/close)
      if (stompClientRef.current) {
         // stompClientRef.current.disconnect(); 
         // Optional: Don't strictly disconnect on re-renders if App doesn't unmount
      }
    };
  }, []);

  return (
    <WebSocketContext.Provider value={{ client: stompClientRef.current, connected, userId }}>
      {children}
    </WebSocketContext.Provider>
  );
};
