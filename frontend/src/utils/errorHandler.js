export const handleError = (error) => {
    if (error.response) {
        const status = error.response.status;
        const message = error.response.data?.message || "An error occurred";
        
        switch (status) {
            case 401:
                return "Session expired. Please log in again.";
            case 403:
                return "You do not have permission to perform this action.";
            case 404:
                return "Requested resource not found.";
            case 500:
                return "Server error. Please try again later.";
            default:
                return message;
        }
    }
    return error.message || "Network error. Please check your connection.";
};
