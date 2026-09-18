import React from "react";
import ReactDOM from "react-dom/client";

const name = "Vu Xuan Sang";

const element = React.createElement("h1", { style: { textAlign: "center" } }, name);

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(element);