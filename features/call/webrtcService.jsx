// webrtcService.js
export function createWebRTC({ onTrack, onIce }) {
  const pc = new RTCPeerConnection({
    iceServers: [{ urls: "stun:stun.l.google.com:19302" }],
  });

  pc.ontrack = (e) => onTrack(e.streams[0]);
  pc.onicecandidate = (e) => e.candidate && onIce(e.candidate);

  return pc;
}

export async function addLocalStream(pc, stream) {
  stream.getTracks().forEach((t) => pc.addTrack(t, stream));
}

export async function makeOffer(pc) {
  const offer = await pc.createOffer();
  await pc.setLocalDescription(offer);
  return offer;
}

export async function makeAnswer(pc, offer) {
  await pc.setRemoteDescription(offer);
  const answer = await pc.createAnswer();
  await pc.setLocalDescription(answer);
  return answer;
}

export async function applyAnswer(pc, answer) {
  if (pc.signalingState === "have-local-offer") {
    await pc.setRemoteDescription(answer);
  }
}

export async function addCandidate(pc, candidate, queue) {
  if (!pc.remoteDescription) {
    queue.push(candidate);
    return;
  }
  await pc.addIceCandidate(candidate);
}

export async function flushCandidates(pc, queue) {
  for (const c of queue) await pc.addIceCandidate(c);
  queue.length = 0;
}
