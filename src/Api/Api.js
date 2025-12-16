import axios from "axios";

export const authapi = axios.create({
  baseURL: "http://localhost:8080"
})
export const api = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true,
});
api.interceptors.request.use((config) => {
  const token = sessionStorage.getItem("accessToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
