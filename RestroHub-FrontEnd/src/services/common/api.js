import axios from "axios";
import { clearAuthSession, getAccessToken } from "./authStorage";

const api = axios.create({
  baseURL:
    import.meta.env.VITE_API_BASE_URL || "http://localhost:8181/restroly",
});

api.interceptors.request.use(
  (config) => {
    const accessToken = getAccessToken();

    if (accessToken && config.url?.includes("/secure/")) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }

    if (config.data instanceof FormData) {
      delete config.headers["Content-Type"];
    }

    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      clearAuthSession();
      window.location.href = "/login";
    }

    return Promise.reject(error);
  }
);

export default api;
