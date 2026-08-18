import axios from "axios";

const API = axios.create({
    baseURL: "http://localhost:8080/api"
});

export const loginUser = (username, password) => {
    return API.post("/login", {
        username: username,
        password: password
    });
};
export const uploadProject = (file) => {
    const formData = new FormData();
    formData.append("file", file);

    return API.post("/upload", formData, {
        headers: {
            "Content-Type": "multipart/form-data"
        }
    });
};

export default API;