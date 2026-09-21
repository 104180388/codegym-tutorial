import ReactDOM from "react-dom/client";

const root = ReactDOM.createRoot(document.getElementById("root"));

const tick = () => {
    const now = new Date();

    // Format date as dd/mm/yyyy
    const day = now.getDate().toString().padStart(2, '0');
    const month = (now.getMonth() + 1).toString().padStart(2, '0');
    const year = now.getFullYear();
    const dateString = `${day}/${month}/${year}`;

    // Format time as hh:mm:ss
    const hours = now.getHours().toString().padStart(2, '0');
    const minutes = now.getMinutes().toString().padStart(2, '0');
    const seconds = now.getSeconds().toString().padStart(2, '0');
    const timeString = `${hours}:${minutes}:${seconds}`;

    root.render(
        <div style={{ textAlign: "center", fontFamily: "sans-serif", marginTop: "50px" }}>
            <h1>Hello, world!</h1>
            <h2>It is {dateString} – {timeString}.</h2>
        </div>
    );
};

setInterval(tick, 1000);