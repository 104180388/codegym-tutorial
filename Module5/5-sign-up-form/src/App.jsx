import React, { useState } from "react";
import "./App.css";

function App() {
    const [form, setForm] = useState({});

    const handleChange = (event) => {
        setForm({
            ...form,
            [event.target.name]: event.target.value,
        });
    };

    const handleSubmit = () => {
        const isValid =
            form.username && form.email && form.password && form.confirmPassword;

        alert(
            isValid
                ? "Sign up success!!!"
                : "Please fill out all the fields!!!"
        );
    };

    return (
        <div style={{ maxWidth: 400, margin: "0 auto", padding: 20 }}>
            <h1>Sign up</h1>
            <form>
                <div className="custom-input">
                    <label>Username</label>
                    <input
                        name="username"
                        value={form.username || ""}
                        onChange={handleChange}
                    />
                </div>
                <div className="custom-input">
                    <label>Email</label>
                    <input
                        name="email"
                        value={form.email || ""}
                        onChange={handleChange}
                    />
                </div>
                <div className="custom-input">
                    <label>Password</label>
                    <input
                        type="password"
                        name="password"
                        value={form.password || ""}
                        onChange={handleChange}
                    />
                </div>
                <div className="custom-input">
                    <label>Confirm Password</label>
                    <input
                        type="password"
                        name="confirmPassword"
                        value={form.confirmPassword || ""}
                        onChange={handleChange}
                    />
                </div>
                <button type="button" onClick={handleSubmit}>
                    Submit
                </button>
            </form>
        </div>
    );
}

export default App;