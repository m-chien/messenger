import { api, authapi } from "../Api/Api";

export const userService = {
  login: async (email, password) => {
    const res = await authapi.post("/users/login", {
      email: email,
      pass: password,
    });
    console.log("Login response:", res.data);
    sessionStorage.setItem("accessToken", res.data.token);
    return res.data;
  },
  loginGG: async (idToken) => {
    const res = await authapi.post("/users/auth/google", {
      idToken: idToken,
    });
    sessionStorage.setItem("accessToken", res.data.token);
    return res.data;
  },
};
