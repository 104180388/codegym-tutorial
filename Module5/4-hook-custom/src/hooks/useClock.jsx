import { useState, useEffect } from "react";

export default function useClock() {
    const [time, setTime] = useState("");
    const [ampm, setAmpm] = useState("");

    // Function cập nhật thời gian
    const updateTime = () => {
        const dateInfo = new Date();
        let hour = 0;

        // Lấy giờ theo chuẩn 12h
        if (dateInfo.getHours() === 0) {
            hour = 12;
        } else if (dateInfo.getHours() > 12) {
            hour = dateInfo.getHours() - 12;
        } else {
            hour = dateInfo.getHours();
        }

        // Lấy phút
        const minutes =
            dateInfo.getMinutes() < 10
                ? "0" + dateInfo.getMinutes()
                : dateInfo.getMinutes();

        // Lấy giây
        const seconds =
            dateInfo.getSeconds() < 10
                ? "0" + dateInfo.getSeconds()
                : dateInfo.getSeconds();

        // AM hay PM
        const ampmValue = dateInfo.getHours() >= 12 ? "PM" : "AM";

        // Cập nhật state
        setAmpm(ampmValue);
        setTime(`${hour}:${minutes}:${seconds}`);
    };

    // Sử dụng useEffect để chạy setInterval khi hook được gọi
    useEffect(() => {
        updateTime(); // cập nhật ngay lần đầu
        const timer = setInterval(updateTime, 1000);

        // Cleanup khi component unmount
        return () => clearInterval(timer);
    }, []);

    return [time, ampm];
}