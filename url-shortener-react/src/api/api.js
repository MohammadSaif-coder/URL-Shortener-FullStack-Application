//import axios from "axios";
//
//export default axios.create({
//    baseURL: import.meta.env.VITE_BACKEND_URL,
//});

import axios from "axios";

const API = axios.create({
  baseURL: "https://url-shortener-sb-fyd4.onrender.com", // ⚠️ must be production backend
  headers: {
    "Content-Type": "application/json",
    // Authorization added dynamically in requests
  },
});

export default API;
