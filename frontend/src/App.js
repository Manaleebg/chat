import { useEffect, useState } from "react";
import api from "./api";
import "./App.css";

function App() {

  const [user, setUser] = useState(null);
  const [isRegister, setIsRegister] = useState(false);

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");

  useEffect(() => {
    checkUser();
  }, []);

  // CHECK LOGIN
  const checkUser = async () => {

    try {

      const res = await api.get("/api/auth/me");

      if (res.data) {

        setUser(res.data);

        loadHistory();
      }

    } catch (err) {

      console.log("User not logged in");
    }
  };

  // REGISTER
  const register = async () => {

    try {

      await api.post("/api/auth/register", {
        username,
        password,
      });

      alert("Registered Successfully");

      setIsRegister(false);

    } catch (err) {

      alert("Registration Failed");
    }
  };

  // LOGIN
  const login = async () => {

    try {

      const formData = new URLSearchParams();

      formData.append("username", username);
      formData.append("password", password);

      await api.post(
        "/api/auth/login",
        formData,
        {
          headers: {
            "Content-Type":
              "application/x-www-form-urlencoded",
          },
        }
      );

      await checkUser();

    } catch (err) {

      alert("Login Failed");
    }
  };

  // LOGOUT
  const logout = async () => {

    try {

      await api.post("/api/auth/logout");

      setUser(null);

      setMessages([]);

    } catch (err) {

      console.log("Logout Error");
    }
  };

  // LOAD HISTORY
  const loadHistory = async () => {

    try {

      const res = await api.get("/api/chat/history");

      if (Array.isArray(res.data)) {

        setMessages(res.data);

      } else {

        setMessages([]);
      }

    } catch (err) {

      console.log("No Chat History");

      setMessages([]);
    }
  };

  // SEND MESSAGE
  const sendMessage = async () => {

    if (!input.trim()) return;

    const userText = input;

    setInput("");

    // show user msg instantly
    setMessages((prev) => [
      ...prev,
      {
        role: "user",
        content: userText,
      },
    ]);

    try {

      const res = await api.post("/api/chat", {
        message: userText,
      });

      setMessages((prev) => [
        ...prev,
        {
          role: "assistant",
          content: res.data.reply,
        },
      ]);

    } catch (err) {

      setMessages((prev) => [
        ...prev,
        {
          role: "assistant",
          content: "Error getting response",
        },
      ]);
    }
  };

  // LOGIN PAGE
  if (!user) {

    return (

      <div className="auth-container">

        <h2>
          {isRegister ? "Register" : "Login"}
        </h2>

        <input
          placeholder="Username"
          value={username}
          onChange={(e) =>
            setUsername(e.target.value)
          }
        />

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) =>
            setPassword(e.target.value)
          }
        />

        <button
          onClick={
            isRegister ? register : login
          }
        >
          {isRegister ? "Register" : "Login"}
        </button>

        <p
          onClick={() =>
            setIsRegister(!isRegister)
          }
        >
          {isRegister
            ? "Already have account? Login"
            : "No account? Register"}
        </p>

      </div>
    );
  }

  // CHAT PAGE
  return (

    <div className="app">

      <div className="sidebar">

        <h2>AI Chat</h2>

        <p>{user.username}</p>

        <button onClick={logout}>
          Logout
        </button>

      </div>

      <div className="chat-area">

        <div className="messages">

          {Array.isArray(messages) &&
            messages.map((msg, index) => (

              <div
                key={index}
                className={`message ${msg.role}`}
              >

                <b>
                  {msg.role === "user"
                    ? "You"
                    : "AI"}
                </b>

                <p>{msg.content}</p>

              </div>
            ))}

        </div>

        <div className="input-area">

          <input
            placeholder="Ask anything..."
            value={input}
            onChange={(e) =>
              setInput(e.target.value)
            }
            onKeyDown={(e) =>
              e.key === "Enter" &&
              sendMessage()
            }
          />

          <button onClick={sendMessage}>
            Send
          </button>

        </div>

      </div>

    </div>
  );
}

export default App;