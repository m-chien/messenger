// useMicCamera.js
import { useState } from "react";

export function useMicCamera() {
  const [micEnabled, setMicEnabled] = useState(true);
  const [cameraEnabled, setCameraEnabled] = useState(true);

  const toggleMic = (localStream) => {
    if (!localStream) return;
    localStream.getAudioTracks().forEach((track) => {
      track.enabled = !track.enabled;
      setMicEnabled(track.enabled);
    });
  };

  const toggleCamera = (localStream) => {
    if (!localStream) return;
    localStream.getVideoTracks().forEach((track) => {
      track.enabled = !track.enabled;
      setCameraEnabled(track.enabled);
    });
  };

  return {
    micEnabled,
    cameraEnabled,
    setMicEnabled,
    setCameraEnabled,
    toggleMic,
    toggleCamera,
  };
}
