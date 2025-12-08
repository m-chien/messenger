import { api } from "./Api";

export const getRoomByUserID = (userID) => {
  return api.get(`/chatRooms/user/${userID}`);
}