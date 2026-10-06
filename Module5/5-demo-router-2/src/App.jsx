import React from "react";
import { Routes, Route } from "react-router-dom";
import Category from "./components/Category";
import Product from "./components/Product";

function App() {
    return (
        <Routes>
            {/* Trang chính chứa dropdown chọn loại xe */}
            <Route path="/" element={<Category />} />

            {/* Trang hiển thị chi tiết sản phẩm theo categoryId */}
            <Route path="/product/:categoryId" element={<Product />} />
        </Routes>
    );
}

export default App;