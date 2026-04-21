import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NextDayCalculatorTest {

    @Test
    void nextDay() {
        //GIVEN
        String date = "2024-06-30";
        String expected = "2024-06-30 00:00:00";

        //WHEN
        String result = NextDayCalculator.nextDay(date);

        //THEN
        assertEquals(expected, result);
    }
}