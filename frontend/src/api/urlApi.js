import axiosInstance from "./axiosInstance";

// Create short URL
export const createUrl = (data) => {
    return axiosInstance.post(
        "/api/urls",
        data
    );
};

// Get all URLs
export const getUrls = () => {
    return axiosInstance.get(
        "/api/urls"
    );
};

// Get URL by id
export const getUrlById = (id) => {
    return axiosInstance.get(
        `/api/urls/${id}`
    );
};

// Update URL
export const updateUrl = (id, data) => {
    return axiosInstance.put(
        `/api/urls/${id}`,
        data
    );
};

// Delete URL
export const deleteUrl = (id) => {
    return axiosInstance.delete(
        `/api/urls/${id}`
    );
};
