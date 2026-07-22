import axiosInstance from "./axiosInstance";

export const getDashboardAnalytics = () => {
    return axiosInstance.get(
        "/api/analytics/dashboard"
    );
};
