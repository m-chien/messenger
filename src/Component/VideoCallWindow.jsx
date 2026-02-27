import React, { useEffect, useRef, useState } from "react";
import { Mic, MicOff, PhoneOff, Video, VideoOff } from "lucide-react";
import { useCall } from "../../features/call/CallProvider.jsx";

const VideoCallWindow = () => {
  const {
    callState,
    localStreamRef,
    // remoteStreamRef, // REMOVED
    endCall,
    toggleMic,
    toggleCamera,
    callType,
    chatRoom,
    remoteStreams, // Map: userId -> Stream
    micEnabled,
    cameraEnabled,
  } = useCall();

  // Get stream from refs
  const localStream = localStreamRef?.current;
  const localVideoRef = useRef(null);

  console.log("🔥 VideoCallWindow render, callState =", callState);

  const shouldRender = ["outgoing", "incall"].includes(callState);

  useEffect(() => {
    if (!shouldRender || !localStream || !localVideoRef.current) return;

    localVideoRef.current.srcObject = localStream;
    localVideoRef.current.play().catch(console.error);
  }, [localStream, shouldRender]);

  if (!shouldRender) return null;

  const handleToggleMic = () => {
    toggleMic(localStream);
  };

  const handleToggleCam = () => {
    toggleCamera(localStream);
  };

  return (
    <div className="video-call-window">
      <div className="video-container">
        {/* Remote Streams Grid */}
        <div className="remote-video-grid" style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit, minmax(300px, 1fr))",
            gap: "10px",
            width: "100%",
            height: "100%",
            alignContent: "center"
        }}>
          {Object.entries(remoteStreams).length > 0 ? (
            Object.entries(remoteStreams).map(([userId, stream]) => (
              <RemoteVideo key={userId} stream={stream} callType={callType} />
            ))
          ) : (
             <div className="audio-call-placeholder">
              <div className="avatar-placeholder big">
                {chatRoom?.logo ? (
                  <img
                    src={`http://localhost:8080${chatRoom.logo}`}
                    alt={chatRoom.name}
                    className="call-avatar-img"
                    style={{
                      width: "100%",
                      height: "100%",
                      objectFit: "cover",
                      borderRadius: "50%",
                    }}
                  />
                ) : (
                  chatRoom?.name?.charAt(0) || "U"
                )}
              </div>
              <p>{chatRoom?.name || "Unknown User"}</p>
              <p>Waiting for others...</p>
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
          className={`control-btn ${!micEnabled ? "off" : ""}`}
          onClick={handleToggleMic}
        >
          {micEnabled ? <Mic size={24} /> : <MicOff size={24} />}
        </button>

        {callType === "VIDEO" && (
          <button
            className={`control-btn ${!cameraEnabled ? "off" : ""}`}
            onClick={handleToggleCam}
          >
            {cameraEnabled ? <Video size={24} /> : <VideoOff size={24} />}
          </button>
        )}

        <button className="control-btn end-call" onClick={endCall}>
          <PhoneOff size={24} />
        </button>
      </div>
    </div>
  );
};

// Sub-component for individual remote video
const RemoteVideo = ({ stream, callType }) => {
  const videoRef = useRef(null);

  useEffect(() => {
    if (videoRef.current && stream) {
      videoRef.current.srcObject = stream;
      videoRef.current.play().catch(console.error);
    }
  }, [stream]);

  if (callType === "AUDIO") {
     return (
        <div className="remote-video-item audio-only">
           <div className="avatar-placeholder">User</div>
        </div>
     );
  }

  return (
    <div className="remote-video-item">
      <video
        ref={videoRef}
        playsInline
        autoPlay
        className="remote-video-element"
        style={{ width: "100%", height: "100%", objectFit: "cover" }}
      />
    </div>
  );
};

export default VideoCallWindow;
