import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TriangleClassifierTest {

    @Test
    void triangleType() {
        int side1 = 2;
        int side2 = 2;
        int side3 = 2;
        String expected = TriangleClassifier.triangleType(side1,side2,side3);
        String result = "Equilateral triangle";
        assertEquals(result, expected);
    }

    @Test
    void triangleType1() {
        int side1 = 2;
        int side2 = 2;
        int side3 = 3;
        String expected = TriangleClassifier.triangleType(side1,side2,side3);
        String result = "Isosceles triangle";
        assertEquals(result, expected);
    }

    @Test
    void triangleType2() {
        int side1 = 3;
        int side2 = 4;
        int side3 = 5;
        String expected = TriangleClassifier.triangleType(side1,side2,side3);
        String result = "Just a triangle";
        assertEquals(result, expected);
    }

    @Test
    void triangleType3() {
        int side1 = 8;
        int side2 = 2;
        int side3 = 3;
        String expected = TriangleClassifier.triangleType(side1,side2,side3);
        String result = "Not a triangle";
        assertEquals(result, expected);
    }
}