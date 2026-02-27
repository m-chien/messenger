import { useState } from "react";

export function useCallState() {
  const [callState, setCallState] = useState("idle");
  const [callType, setCallType] = useState(null);
  // FIX: Support multiple participants
  const [remoteUsers, setRemoteUsers] = useState([]); // Array of userIds
  const [remoteStreams, setRemoteStreams] = useState({}); // Map: userId -> Stream

  const addRemoteStream = (userId, stream) => {
    setRemoteStreams((prev) => ({ ...prev, [userId]: stream }));
  };

  const removeRemoteStream = (userId) => {
    setRemoteStreams((prev) => {
      const newStreams = { ...prev };
      delete newStreams[userId];
      return newStreams;
    });
  };

  const [chatRoom, setChatRoom] = useState(null); // { id, name, logo }
  const [micEnabled, setMicEnabled] = useState(true);
  const [cameraEnabled, setCameraEnabled] = useState(true);
  const [userAccepted, setUserAccepted] = useState(false); // Track if current user accepted

  return {
    callState,
    setCallState,
    callType,
    setCallType,
    remoteUsers,
    setRemoteUsers,
    remoteStreams,
    addRemoteStream,
    removeRemoteStream,
    chatRoom,
    setChatRoom,
    micEnabled,
    setMicEnabled,
    cameraEnabled,
    setCameraEnabled,
    userAccepted,
    setUserAccepted,
  };
}
