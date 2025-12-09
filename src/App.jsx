import { BrowserRouter, Route, Routes } from "react-router-dom"
import { RegisterPage } from "./Page/RegisterPage"
import { LoginPage } from "./Page/LoginPage"
import { Profile } from "./Page/Profile"
import HomePage from "./Page/HomePage"


const App = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/profile" element={<Profile />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App