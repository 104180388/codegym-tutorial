import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class NextDayCalculatorTest {

    @Test
    void nextDay() {
        String date = "2024-01-01";
        String expected = "2024-01-02";
        String result = NextDayCalculator.nextDay(LocalDate.parse(date));
        assertEquals(expected, result);
    }

    @Test
    void nextDay1() {
        String date = "2024-12-31";
        String expected = "2025-01-01";
        String result = NextDayCalculator.nextDay(LocalDate.parse(date));
        assertEquals(expected, result);
    }
    @Test
    void nextDay2() {
        String date = "2024-01-31";
        String expected = "2024-02-01";
        String result = NextDayCalculator.nextDay(LocalDate.parse(date));
        assertEquals(expected, result);
    }
    @Test
    void nextDay3() {
        String date = "2024-04-30";
        String expected = "2024-05-01";
        String result = NextDayCalculator.nextDay(LocalDate.parse(date));
        assertEquals(expected, result);
    }
}