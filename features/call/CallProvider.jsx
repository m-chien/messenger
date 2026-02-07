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

  const pcRef = useRef(null);
  const iceQueue = useRef([]);
  const remoteStreamRef = useRef(null);
  const localStreamRef = useRef(null);

  // để lưu tạm chatRoomId / callType cho gửi signal
  const chatRoomIdRef = useRef(null);
  const callTypeRef = useRef(null);

  const initPC = async () => {
    // guard: nếu đã có pc thì thôi
    if (pcRef.current) return pcRef.current;

    let stream;
    try {
      stream = await navigator.mediaDevices.getUserMedia({
        video: state.callType === "VIDEO" || callTypeRef.current === "VIDEO",
        audio: true,
      });
    } catch (err) {
      console.error("getUserMedia failed", err);
      throw err;
    }

    localStreamRef.current = stream;

    const pc = createWebRTC({
      onTrack: (s) => {
        remoteStreamRef.current = s;
      },
      onIce: (c) => {
        // only send ICE when socket connected
        if (client?.connected) {
          sendSignal(client, {
            type: "candidate",
            chatRoomId: chatRoomIdRef.current,
            data: c,
          });
        }
      },
    });

    await addLocalStream(pc, stream);
    pcRef.current = pc;
    return pc;
  };

  const endCallCleanup = () => {
    state.setCallState("idle");
    state.setCallType(null);
    state.setRemoteUserId(null); // nếu bạn có field này trong state
    state.setChatRoom(null);
    chatRoomIdRef.current = null;
    callTypeRef.current = null;
    remoteStreamRef.current = null;

    try {
      // stop local tracks if any
      localStreamRef.current?.getTracks().forEach((t) => t.stop());
      localStreamRef.current = null;
    } catch (e) {}

    try {
      // stop pc tracks as well
      pcRef.current?.getSenders()?.forEach((s) => {
        if (s.track) s.track.stop();
      });
    } catch (e) {}

    pcRef.current?.close();
    pcRef.current = null;
    iceQueue.current = [];
  };

  const handleSignal = async (data) => {
    // data shape assumed: { type, chatRoomId, data }
    try {
      switch (data.type) {
        case "call-request":
          // incoming call: set UI state, keep chatRoomId / callType
          chatRoomIdRef.current = data.chatRoomId;
          callTypeRef.current = data.callType;
          state.setRemoteUserId(data.fromUserId || null);
          state.setChatRoom(data.chatRoom || null);
          state.setCallType(data.callType || null);
          state.setCallState("incoming");
          ringtone.playRingtone(); // ✅ Phát chuông khi có cuộc gọi đến
          break;

        case "call-response":
          // remote accepted/rejected our call-request
          if (data.data === "accepted") {
            ringtone.stopCallingTone(); // ✅ Dừng chuông calling khi được chấp nhận
            state.setCallState("incall");
            // caller side: create pc and send offer
            await initPC();
            const offer = await makeOffer(pcRef.current);
            sendSignal(client, {
              type: "offer",
              chatRoomId: chatRoomIdRef.current,
              data: offer,
            });
          } else {
            // rejected
            ringtone.stopCallingTone(); // ✅ Dừng chuông calling khi bị từ chối
            endCallCleanup();
          }
          break;

        case "offer":
          // remote sent offer -> we are answerer
          // ensure pc exists (and local stream added)
          await initPC();
          // makeAnswer will setRemoteDescription(offer) and create/set local answer
          const answer = await makeAnswer(pcRef.current, data.data);
          // flush any buffered ICE
          await flushCandidates(pcRef.current, iceQueue.current);
          // send answer
          sendSignal(client, {
            type: "answer",
            chatRoomId: data.chatRoomId || chatRoomIdRef.current,
            data: answer,
          });
          break;

        case "answer":
          // caller receives answer
          if (pcRef.current) {
            await applyAnswer(pcRef.current, data.data);
            await flushCandidates(pcRef.current, iceQueue.current);
          } else {
            // no pc yet -> this is abnormal but buffer answer? (rare)
            console.warn("Received answer but pc not created yet");
          }
          break;

        case "candidate":
          // ICE candidate from remote
          // If pc doesn't exist yet, buffer candidate
          if (!pcRef.current) {
            iceQueue.current.push(data.data);
          } else {
            await addCandidate(pcRef.current, data.data, iceQueue.current);
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

    state.setCallType(type);
    state.setChatRoom(chatRoom);
    state.setCallState("outgoing");

    ringtone.playCallingTone(); // ✅ Phát chuông calling khi bắt đầu gọi

    // First, let the callee know we want to call
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
    // actual offer will be sent only after callee accepts (handled in call-response)
  };

  const acceptCall = async () => {
    // user accepts incoming call
    ringtone.stopRingtone(); // ✅ Dừng chuông ringtone khi chấp nhận cuộc gọi
    state.setCallState("incall");
    // create pc early (so it can gather local ICE)
    await initPC();
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
        remoteStreamRef,
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
