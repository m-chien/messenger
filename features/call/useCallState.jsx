// useCallState.js
import { useState } from "react";

export function useCallState() {
  const [callState, setCallState] = useState("idle");
  const [callType, setCallType] = useState(null);
  const [remoteUserId, setRemoteUserId] = useState(null);
  const [micEnabled, setMicEnabled] = useState(true);
  const [cameraEnabled, setCameraEnabled] = useState(true);

  return {
    callState,
    setCallState,
    callType,
    setCallType,
    remoteUserId,
    setRemoteUserId,
    micEnabled,
    setMicEnabled,
    cameraEnabled,
    setCameraEnabled,
  };
}
