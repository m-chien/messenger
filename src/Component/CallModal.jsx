import React from "react";
import { Phone, PhoneOff, Video } from "lucide-react";
import { useCall } from "../../features/call/CallProvider.jsx";

const CallModal = () => {
  const { callState, caller, callType, acceptCall, rejectCall, endCall } = useCall();

  if (callState === "idle" || callState === "incall") return null;

  const isIncoming = callState === "incoming";

  return (
    <div className="call-modal-overlay">
      <div className="call-modal">
        <div className="call-avatar">
          <img
            src="https://via.placeholder.com/100" // Replace with caller avatar if available
            alt="User"
          />
        </div>
        <h3>{caller?.name || "Unknown User"}</h3>
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
