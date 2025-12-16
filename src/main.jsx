import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.jsx";
import { ThemeProvider } from "./Component/ThemeContext.jsx";
import { GoogleOAuthProvider } from "@react-oauth/google";

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <ThemeProvider>
      <GoogleOAuthProvider clientId="959381924356-5sdi5bunvmu825hfm75r13a5rktqv2uc.apps.googleusercontent.com">
      <App />
      </GoogleOAuthProvider>
    </ThemeProvider>
  </StrictMode>,
);
