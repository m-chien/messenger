// CallProvider.jsx
import React, { createContext, useContext, useRef, useEffect } from "react";
import { useWebSocket } from "../../src/Context/WebSocketContext.jsx";
import { useCallState } from "./useCallState.jsx";
import { useMicCamera } from "./useMicCamera.js";
import { useRingtone } from "./useRingtone.js";
import {
  createWebRTC,
  addLocalStream,
  makeOffer,
  makeAnswer,
  applyAnswer,
  addCandidate,
  flushCandidates,
} from "./webrtcService.jsx";
import { subscribeCallSocket, sendSignal } from "./callSocketHandler.jsx";

const CallContext = createContext();
export const useCall = () => useContext(CallContext);

export function CallProvider({ children }) {
  const { client, userId } = useWebSocket();
  const state = useCallState();
  const mic = useMicCamera();
  const ringtone = useRingtone();

  const peersRef = useRef({}); // Map: userId -> RTCPeerConnection
  const iceQueue = useRef({}); // Map: userId -> Array of candidates

  // để lưu tạm chatRoomId / callType cho gửi signal
  const chatRoomIdRef = useRef(null);
  const callTypeRef = useRef(null);

  // Helper to create a PC for a specific target user
  const createPC = async (targetUserId) => {
    if (peersRef.current[targetUserId]) return peersRef.current[targetUserId];

    let stream = localStreamRef.current;
    if (!stream) {
      try {
        stream = await navigator.mediaDevices.getUserMedia({
          video: state.callType === "VIDEO" || callTypeRef.current === "VIDEO",
          audio: true,
        });
        localStreamRef.current = stream;
      } catch (err) {
        console.error("getUserMedia failed", err);
        throw err;
      }
    }

    const pc = createWebRTC({
      onTrack: (s) => {
        state.addRemoteStream(targetUserId, s);
      },
      onIce: (c) => {
        if (client?.connected) {
          sendSignal(client, {
            type: "candidate",
            chatRoomId: chatRoomIdRef.current,
            toUserId: targetUserId, // Target specific user
            data: c,
          });
        }
      },
    });

    await addLocalStream(pc, stream);
    peersRef.current[targetUserId] = pc;
    return pc;
  };

  const localStreamRef = useRef(null);
  // Remote stream management is now in state.remoteStreams

  const endCallCleanup = () => {
    state.setCallState("idle");
    state.setCallType(null);
    state.setRemoteUsers([]);
    state.setChatRoom(null);
    state.setUserAccepted(false);
    // Remove all remote streams - simplified by component unmount mostly, but good to ensure
    // state.setRemoteStreams({}); // If exposed

    chatRoomIdRef.current = null;
    callTypeRef.current = null;

    try {
      // stop local tracks if any
      localStreamRef.current?.getTracks().forEach((t) => t.stop());
      localStreamRef.current = null;
    } catch (e) {}

    // Close all PCs
    Object.values(peersRef.current).forEach((pc) => pc.close());
    peersRef.current = {};
    iceQueue.current = {};
  };

  const handleSignal = async (data) => {
    try {
      const fromUserId = data.fromUserId;
      // const toUserId = data.toUserId; // If backend forwards this, we can check if it's for us

      switch (data.type) {
        case "call-request":
          // incoming call: set UI state
          chatRoomIdRef.current = data.chatRoomId;
          callTypeRef.current = data.callType;
          // state.setRemoteUsers([fromUserId]); // Maybe track the caller?
          state.setChatRoom(data.chatRoom || null);
          state.setCallType(data.callType || null);
          state.setCallState("incoming");
          ringtone.playRingtone(); // ✅ Phát chuông khi có cuộc gọi đến
          break;

        case "call-response":
          // Only the Caller (outgoing) cares about this, or other participants in Mesh
          // If we are "incoming", we ignore it (Auto-accept fix)
          if (state.callState === "incoming") {
             // console.log("Ignoring call-response because I am also an incoming callee");
             return;
          }

          if (data.data === "accepted") {
             // A participant accepted.
             ringtone.stopCallingTone();
             state.setCallState("incall");

             // Create PC for this specific user
             const pc = await createPC(fromUserId);
             const offer = await makeOffer(pc);
             
             sendSignal(client, {
               type: "offer",
               chatRoomId: chatRoomIdRef.current,
               toUserId: fromUserId, // Send offer ONLY to this user
               data: offer,
             });
          } else {
            // rejected
            // For now, keep simple log
            console.log(`User ${fromUserId} rejected`);
          }
          break;

        case "offer":
          // Verify if this offer is for ME
          if (data.toUserId && String(data.toUserId) !== String(userId)) return;

          // We are answerer for this specific connection
          const pcOffer = await createPC(fromUserId);
          const answer = await makeAnswer(pcOffer, data.data);
          
          // flush buffered ICE
          const queue = iceQueue.current[fromUserId] || [];
          await flushCandidates(pcOffer, queue);
          delete iceQueue.current[fromUserId];

          sendSignal(client, {
             type: "answer",
             chatRoomId: chatRoomIdRef.current,
             toUserId: fromUserId,
             data: answer,
          });
          
          // Ensure we are in incall state
          if (state.callState !== "incall") {
             state.setCallState("incall");
             ringtone.stopRingtone();
          }
          break;

        case "answer":
          if (data.toUserId && String(data.toUserId) !== String(userId)) return;
          
          const pcAnswer = peersRef.current[fromUserId];
          if (pcAnswer) {
            await applyAnswer(pcAnswer, data.data);
            const queue = iceQueue.current[fromUserId] || [];
            await flushCandidates(pcAnswer, queue);
            delete iceQueue.current[fromUserId];
          }
          break;

        case "candidate":
          if (data.toUserId && String(data.toUserId) !== String(userId)) return;

          const pcIce = peersRef.current[fromUserId];
          if (pcIce) {
             await addCandidate(pcIce, data.data, []); 
          } else {
             if (!iceQueue.current[fromUserId]) iceQueue.current[fromUserId] = [];
             iceQueue.current[fromUserId].push(data.data);
          }
          break;

        case "end-call":
          ringtone.stopAllTones(); // ✅ Dừng tất cả chuông khi cuộc gọi kết thúc
          endCallCleanup();
          break;
      }
    } catch (err) {
      console.error("handleSignal error", err);
    }
  };

  useEffect(() => {
    if (!client?.connected || !userId) return;
    const sub = subscribeCallSocket(client, userId, handleSignal);
    return () => sub.unsubscribe();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [client, userId]);

  /* ================= CALL ACTIONS (public API) ================= */
  const startCall = async (chatRoom, type) => {
    const chatRoomId = chatRoom.idChatroom;
    chatRoomIdRef.current = chatRoomId;
    callTypeRef.current = type;

    state.setChatRoom(chatRoom);
    state.setCallType(type);
    state.setCallState("outgoing");

    ringtone.playCallingTone(); // ✅ Phát chuông calling khi bắt đầu gọi

    // Broadcast call request to room
    sendSignal(client, {
      type: "call-request",
      chatRoomId,
      chatRoom: {
        id: chatRoom.idChatroom,
        name: chatRoom.name,
        logo: chatRoom.logo,
      },
      callType: type,
    });
  };

  const acceptCall = async () => {
    // user accepts incoming call
    ringtone.stopRingtone(); // ✅ Dừng chuông ringtone khi chấp nhận cuộc gọi
    state.setUserAccepted(true);
    state.setCallState("incall");
    
    // Ensure we have local stream ready
    try {
        const stream = await navigator.mediaDevices.getUserMedia({
          video: state.callType === "VIDEO" || callTypeRef.current === "VIDEO",
          audio: true,
        });
        localStreamRef.current = stream;
    } catch(e) {
        console.error("Failed to get local stream", e);
    }

    // notify caller
    sendSignal(client, {
      type: "call-response",
      chatRoomId: chatRoomIdRef.current,
      data: "accepted",
    });
  };

  const rejectCall = () => {
    ringtone.stopRingtone(); // ✅ Dừng chuông khi từ chối
    sendSignal(client, {
      type: "call-response",
      chatRoomId: chatRoomIdRef.current,
      data: "rejected",
    });
    endCallCleanup();
  };

  const endCall = () => {
    ringtone.stopAllTones(); // ✅ Dừng tất cả chuông khi kết thúc
    sendSignal(client, {
      type: "end-call",
      chatRoomId: chatRoomIdRef.current,
    });
    endCallCleanup();
  };

  return (
    <CallContext.Provider
      value={{
        ...state,
        ...mic,
        // remoteStreamRef, // REMOVED
        localStreamRef,
        startCall,
        acceptCall,
        rejectCall,
        endCall,
      }}
    >
      {children}
    </CallContext.Provider>
  );
}
