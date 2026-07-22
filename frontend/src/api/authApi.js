import axiosInstance from "./axiosInstance";

// Register User
export const registerUser = (userData) => {
    return axiosInstance.post(
        "/api/auth/register",
        userData
    );
};

// Login User
export const loginUser = (loginData) => {
    return axiosInstance.post(
        "/api/auth/login",
        loginData
    );
};

// Refresh Access Token
export const refreshToken = (refreshToken) => {
    return axiosInstance.post(
        "/api/auth/refresh",
        {
            refreshToken: refreshToken
        }
    );
};

// Logout User
export const logoutUser = () => {
    return axiosInstance.post(
        "/api/auth/logout"
    );
};
