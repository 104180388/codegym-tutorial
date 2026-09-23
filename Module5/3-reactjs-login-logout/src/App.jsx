import { useState } from "react";
import Home from "./components/Home";

function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);

    const handleLogIn = () => {
        setIsLoggedIn(true);
    };

    const handleLogOut = () => {
        setIsLoggedIn(false);
    };

    return (
        <div style={{ textAlign: "center" }}>
            {isLoggedIn ? (
                <Home onLogOut={handleLogOut} />
            ) : (
                <div>
                    <h1>Please Log in</h1>
                    <button onClick={handleLogIn}>Log in</button>
                </div>
            )}
        </div>
    );
}

export default App;