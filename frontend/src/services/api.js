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

export default API;