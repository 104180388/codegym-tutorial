import { useState } from "react";

function Counter() {
    // Khởi tạo state count với giá trị ban đầu là 0
    const [count, setCount] = useState(0);

    // Hàm xử lý sự kiện khi click nút
    const handleClick = () => {
        const newValue = count + 1;
        setCount(newValue); // Cập nhật state => giao diện tự render lại
    };

    return (
        <div style={{ textAlign: "center", marginTop: 40 }}>
            <h2>Giá trị: {count}</h2>
            <button onClick={handleClick}>Tăng</button>
        </div>
    );
}

export default Counter;