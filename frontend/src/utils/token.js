export const saveToken = (accessToken, refreshToken, user) => {
    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("refreshToken", refreshToken);
    if (user) {
        localStorage.setItem("user", JSON.stringify(user));
    }
};

export const getToken = () => {
    return {
        accessToken: localStorage.getItem("accessToken"),
        refreshToken: localStorage.getItem("refreshToken"),
        user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null
    };
};

export const removeToken = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("user");
};
