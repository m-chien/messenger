import { BrowserRouter, Route, Routes } from "react-router-dom";
import { RegisterPage } from "./Page/RegisterPage";
import { LoginPage } from "./Page/LoginPage";
import { AuthPage } from "./Page/AuthPage";
import { Profile } from "./Page/Profile";
import HomePage from "./Page/HomePage";
import { CallProvider } from "../features/call/CallProvider.jsx";
import { WebSocketProvider } from "./Context/WebSocketContext";
import CallModal from "./Component/CallModal";
import VideoCallWindow from "./Component/VideoCallWindow";

const App = () => {
  return (
    <WebSocketProvider>
      <CallProvider>
        <BrowserRouter>
          <CallModal />
          <VideoCallWindow />
          <Routes>
            <Route path="/" element={<AuthPage />} />
            <Route path="/home" element={<HomePage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/profile" element={<Profile />} />
          </Routes>
        </BrowserRouter>
      </CallProvider>
    </WebSocketProvider>
  );
};

export default App;
