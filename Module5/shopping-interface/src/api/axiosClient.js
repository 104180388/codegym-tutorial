import axios from 'axios';

const API_BASE_URL = 'http://localhost:5000';

const axiosClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 4000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
});

// Request interceptor
axiosClient.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
axiosClient.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    console.warn('Axios error or JSON Server offline:', error?.message);
    return Promise.reject(error);
  }
);

export default axiosClient;
