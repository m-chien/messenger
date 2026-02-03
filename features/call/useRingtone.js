// useRingtone.js
import { useRef } from "react";

export function useRingtone() {
  const ringtoneRef = useRef(null);
  const callingRef = useRef(null);

  const playRingtone = () => {
    if (!ringtoneRef.current) {
      ringtoneRef.current = new Audio("/sounds/ringtone.mp3");
      ringtoneRef.current.loop = true;
    }
    ringtoneRef.current.play().catch(console.error);
  };

  const playCallingTone = () => {
    if (!callingRef.current) {
      callingRef.current = new Audio("/sounds/calling.mp3");
      callingRef.current.loop = true;
    }
    callingRef.current.play().catch(console.error);
  };

  const stopRingtone = () => {
    if (ringtoneRef.current) {
      ringtoneRef.current.pause();
      ringtoneRef.current.currentTime = 0;
    }
  };

  const stopCallingTone = () => {
    if (callingRef.current) {
      callingRef.current.pause();
      callingRef.current.currentTime = 0;
    }
  };

  const stopAllTones = () => {
    stopRingtone();
    stopCallingTone();
  };

  return {
    playRingtone,
    playCallingTone,
    stopRingtone,
    stopCallingTone,
    stopAllTones,
  };
}
