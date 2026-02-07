import React from "react";
import { Phone, PhoneOff, Video } from "lucide-react";
import { useCall } from "../../features/call/CallProvider.jsx";

const CallModal = () => {
  const { callState, callType, chatRoom, acceptCall, rejectCall, endCall } =
    useCall();

  if (callState === "idle" || callState === "incall") return null;

  const isIncoming = callState === "incoming";
  const avatarUrl = chatRoom?.logo
    ? `http://localhost:8080${chatRoom.logo}`
    : "https://via.placeholder.com/100";
  const displayName = chatRoom?.name || "Unknown User";

  return (
    <div className="call-modal-overlay">
      <div className="call-modal">
        <div className="call-avatar">
          <img
            src={avatarUrl}
            alt="User"
            onError={(e) => {
              e.target.onerror = null;
              e.target.src = "https://via.placeholder.com/100";
            }}
          />
        </div>
        <h3>{displayName}</h3>
        <p>
          {isIncoming
            ? `Incoming ${callType === "VIDEO" ? "Video" : "Voice"} Call...`
            : "Calling..."}
        </p>

        <div className="call-actions">
          {isIncoming ? (
            <>
              <button className="btn-reject" onClick={rejectCall}>
                <PhoneOff size={24} />
              </button>
              <button className="btn-accept" onClick={acceptCall}>
                {callType === "VIDEO" ? <Video size={24} /> : <Phone size={24} />}
              </button>
            </>
          ) : (
            <button className="btn-reject" onClick={endCall}>
              <PhoneOff size={24} />
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default CallModal;
