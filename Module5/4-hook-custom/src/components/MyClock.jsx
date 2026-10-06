import useClock from "../hooks/useClock";
import "./Clock.css";

function MyClock() {
    // Gọi custom hook để lấy time và ampm
    const [time, ampm] = useClock();

    return (
        <div id="Clock-style">
            {time}
            <span> {ampm}</span>
        </div>
    );
}

export default MyClock;