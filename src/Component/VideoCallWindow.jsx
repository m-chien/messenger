import React, { useEffect, useRef, useState } from "react";
import { Mic, MicOff, PhoneOff, Video, VideoOff } from "lucide-react";
import { useCall } from "../../features/call/CallProvider.jsx";

const VideoCallWindow = () => {
  const {
    callState,
    localStreamRef,
    remoteStreamRef,
    endCall,
    toggleMic,
    toggleCamera,
    callType,
  } = useCall();

  // Get stream from refs
  const localStream = localStreamRef?.current;
  const remoteStream = remoteStreamRef?.current;

  const localVideoRef = useRef(null);
  const remoteVideoRef = useRef(null);

  const [micOn, setMicOn] = useState(true);
  const [camOn, setCamOn] = useState(true);
  console.log("🔥 VideoCallWindow render, callState =", callState);

  const shouldRender = ["outgoing", "incall"].includes(callState);

  useEffect(() => {
    if (!shouldRender || !localStream || !localVideoRef.current) return;

    localVideoRef.current.srcObject = localStream;
    localVideoRef.current.play().catch(console.error);
  }, [localStream, shouldRender]);

  useEffect(() => {
    if (!shouldRender || !remoteStream || !remoteVideoRef.current) return;

    remoteVideoRef.current.srcObject = remoteStream;
    remoteVideoRef.current.play().catch(console.error);
  }, [remoteStream, shouldRender]);

  if (!shouldRender) return null;

  const handleToggleMic = () => {
    toggleMic(localStream);
    setMicOn(!micOn);
  };

  const handleToggleCam = () => {
    toggleCamera(localStream);
    setCamOn(!camOn);
  };

  const hasRemoteVideo =
    remoteStream &&
    remoteStream.getVideoTracks &&
    remoteStream.getVideoTracks().length > 0;

  return (
    <div className="video-call-window">
      <div className="video-container">
        {/* Remote Stream (Main View) */}
        <div className="remote-video-wrapper">
          {callType === "VIDEO" && hasRemoteVideo ? (
            <video
              ref={remoteVideoRef}
              playsInline
              autoPlay
              className="remote-video"
            />
          ) : (
            <div className="audio-call-placeholder">
              <div className="avatar-placeholder big">User</div>
              <p>Voice Call in progress...</p>
            </div>
          )}
        </div>

        {/* Local Stream (Pip) */}
        {callType === "VIDEO" && (
          <div className="local-video-wrapper">
            <video
              ref={localVideoRef}
              playsInline
              autoPlay
              muted
              className="local-video"
            />
          </div>
        )}
      </div>

      <div className="call-controls">
        <button
          className={`control-btn ${!micOn ? "off" : ""}`}
          onClick={handleToggleMic}
        >
          {micOn ? <Mic size={24} /> : <MicOff size={24} />}
        </button>

        {callType === "VIDEO" && (
          <button
            className={`control-btn ${!camOn ? "off" : ""}`}
            onClick={handleToggleCam}
          >
            {camOn ? <Video size={24} /> : <VideoOff size={24} />}
          </button>
        )}

        <button className="control-btn end-call" onClick={endCall}>
          <PhoneOff size={24} />
        </button>
      </div>
    </div>
  );
};

export default VideoCallWindow;
