import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FizzBuzzTest {

    @Test
    void translate() {
        String result = FizzBuzz.translate(15);
        assertEquals("FizzBuzz", result);
    }
}