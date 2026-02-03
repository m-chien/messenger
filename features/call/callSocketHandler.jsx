// callSocketHandler.js
export function subscribeCallSocket(client, userId, onSignal) {
  return client.subscribe(`/topic/user/${userId}/call`, (payload) => {
    const data = JSON.parse(payload.body);
    onSignal(data);
  });
}

export function sendSignal(client, payload) {
  client.send("/app/call", {}, JSON.stringify(payload));
}
