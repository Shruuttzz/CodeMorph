import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../App.css";
import { loginUser } from "../services/api";

function Login() {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();

        try {

            const response = await loginUser(username, password);

            console.log(response.data);

            if (response.data.success) {

                alert("Login Successful!");

                navigate("/upload");

            } else {

                alert("Invalid username or password");

            }

        } catch (error) {

            console.error(error);

            alert("Unable to connect to the server");

        }
    };

    return (

        <div className="login-container">

            <div className="login-card">

                <h1>CodeMorph</h1>

                <p className="subtitle">
                    Java Project Migration & Analysis
                </p>

                <form onSubmit={handleLogin}>

                    <div className="input-group">

                        <label>Username</label>

                        <input
                            type="text"
                            placeholder="Enter your username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                        />

                    </div>

                    <div className="input-group">

                        <label>Password</label>

                        <input
                            type="password"
                            placeholder="Enter your password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />

                    </div>

                    <button type="submit">
                        Login
                    </button>

                </form>

            </div>

        </div>

    );
}

export default Login;