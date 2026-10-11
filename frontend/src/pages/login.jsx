
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../App.css";
import { loginUser } from "../services/api";

function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();

        if (loading) return;

        setLoading(true);

        try {
            const response = await loginUser(username, password);

            if (response.data.success) {
                alert("Login Successful!");
                navigate("/upload");
            } else {
                alert("Invalid username or password");
            }
        } catch (error) {
            console.error(error);
            alert("Unable to connect to the server");
        } finally {
            setLoading(false);
        }
    };

    return (
        <main className="login-container">
            {/* Subtle decorative background */}
            <div className="login-background-glow" aria-hidden="true" />

            <div className="login-layout">

                {/* LEFT: BRANDING */}
                <section className="login-intro">

                    <div className="login-brand">
                        <span className="brand-icon">&lt;/&gt;</span>

                        <div>
                            <h1>
                                Code<span>Morph</span>
                            </h1>
                            <p>Java Project Migration &amp; Analysis</p>
                        </div>
                    </div>

                    <div className="login-intro-content">
                        <span className="login-eyebrow">
                            INTELLIGENT CODE TRANSFORMATION
                        </span>

                        <h2>
                            Transform legacy.
                            <br />
                            Build what's <span>next.</span>
                        </h2>

                        <p className="login-description">
                            Analyze, assess, and modernize your Java projects
                            with clarity and confidence.
                        </p>

                        <div className="login-features">

                            <div className="login-feature">
                                <div className="feature-icon">⌘</div>
                                <div>
                                    <h3>Deep Code Analysis</h3>
                                    <p>
                                        Understand project structure and dependencies.
                                    </p>
                                </div>
                            </div>

                            <div className="login-feature">
                                <div className="feature-icon">◇</div>
                                <div>
                                    <h3>Migration Risk Assessment</h3>
                                    <p>
                                        Identify components that need attention.
                                    </p>
                                </div>
                            </div>

                            <div className="login-feature">
                                <div className="feature-icon">✳</div>
                                <div>
                                    <h3>AI-Powered Insights</h3>
                                    <p>
                                        Turn complex results into clear guidance.
                                    </p>
                                </div>
                            </div>

                        </div>
                    </div>

                    <p className="login-footer">
                        YOUR CODE. YOUR NEXT CHAPTER.
                    </p>

                </section>

                {/* RIGHT: LOGIN FORM */}
                <section className="login-card">

                    <div className="login-card-heading">
                        <span className="login-welcome">Welcome back</span>

                        <h2>
                            Sign in to <span>CodeMorph</span>
                        </h2>

                        <p>
                            Continue your project migration journey.
                        </p>
                    </div>

                    <form onSubmit={handleLogin}>

                        <div className="input-group">
                            <label htmlFor="username">Username</label>

                            <input
                                id="username"
                                type="text"
                                placeholder="Enter your username"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                autoComplete="username"
                                required
                            />
                        </div>

                        <div className="input-group">
                            <label htmlFor="password">Password</label>

                            <div className="password-input-wrapper">
                                <input
                                    id="password"
                                    type={showPassword ? "text" : "password"}
                                    placeholder="Enter your password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    autoComplete="current-password"
                                    required
                                />

                                <button
                                    type="button"
                                    className="password-toggle"
                                    onClick={() =>
                                        setShowPassword((previous) => !previous)
                                    }
                                    aria-label={
                                        showPassword
                                            ? "Hide password"
                                            : "Show password"
                                    }
                                >
                                    {showPassword ? "Hide" : "Show"}
                                </button>
                            </div>
                        </div>

                        <button
                            type="submit"
                            className="login-submit-button"
                            disabled={loading}
                        >
                            {loading ? "Signing in..." : "Login"}
                            {!loading && <span aria-hidden="true">→</span>}
                        </button>

                    </form>

                    <div className="login-security">
                        <span aria-hidden="true">◇</span>
                        <p>
                            Secure access to your analysis workspace
                        </p>
                    </div>

                    <p className="login-card-footer">
                        Built for developers. Designed for modernization.
                    </p>

                </section>

            </div>
        </main>
    );
}

export default Login;
