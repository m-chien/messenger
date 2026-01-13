import { Mic } from "lucide-react";
import { useRef, useState } from "react";
import "../Style/VoiceRecoder.css";
const VoiceRecorder = ({ onRecorded }) => {
  const [isRecording, setIsRecording] = useState(false);
  const recorderRef = useRef(null);
  const chunksRef = useRef([]);

  const toggleRecording = async () => {
    if (isRecording) {
      recorderRef.current.stop();
      setIsRecording(false);
      return;
    }

    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
    const recorder = new MediaRecorder(stream);

    recorderRef.current = recorder;
    chunksRef.current = [];

    recorder.ondataavailable = (e) => {
      if (e.data.size) chunksRef.current.push(e.data);
    };

    recorder.onstop = () => {
      const blob = new Blob(chunksRef.current, { type: "audio/webm" });
      const file = new File([blob], `voice-${Date.now()}.webm`, {
        type: "audio/webm",
      });

      stream.getTracks().forEach((t) => t.stop());
      onRecorded(file); // 🔥 đẩy file lên cha
    };

    recorder.start();
    setIsRecording(true);
  };

  return (
    <button
      className={`icon-btn mic-btn ${isRecording ? "recording" : ""}`}
      onClick={toggleRecording}
      title="Record voice"
    >
      <Mic size={20} />
    </button>
  );
};

export default VoiceRecorder;
