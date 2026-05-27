import axios from "axios";

const api = axios.create({
    baseURL: "https://chat-production-7399.up.railway.app",
    withCredentials: true,
});

export default api;