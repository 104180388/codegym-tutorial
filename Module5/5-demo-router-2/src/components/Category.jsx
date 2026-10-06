import React from "react";
import { useNavigate } from "react-router-dom";
import "../App.css"; // Import css để áp dụng style

function Category() {
    const navigate = useNavigate();

    // Khi người dùng chọn option, điều hướng sang trang Product với categoryId tương ứng
    const sendDataToProduct = (event) => {
        navigate(`/product/${event.target.value}`);
    };

    return (
        <div className="app-container">
            <h2>Select a Category:</h2>
            <select defaultValue="default" onChange={sendDataToProduct}>
                <option value="default" disabled hidden>
                    Choose your car
                </option>
                <option value="1">Honda</option>
                <option value="2">Suzuki</option>
                <option value="3">Yamaha</option>
            </select>
        </div>
    );
}

export default Category;