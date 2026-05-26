import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Entity.Student;
import java.util.List;

public class StudentServiceTest {

    private StudentService service;

    @BeforeEach
    void setUp() {
        service = StudentService.getInstance();
        service.clearData();
    }

    @Test
    void testLookupById_ShouldReturnCorrectStudent() {
        service.define("Tuấn", "12A");

        String generatedId = service.getStudentList().get(0).getStudentId();

        List<Student> result = service.lookup("id", generatedId);

        assertFalse(result.isEmpty());
        assertEquals("Tuấn", result.get(0).getName());
    }


    @Test
    void testDropStudent_ShouldRemoveFromAllStructures() {
        service.define("Tuấn", "12A");
        String id = service.getStudentList().get(0).getStudentId();

        boolean isDeleted = service.drop(id);

        assertTrue(isDeleted);
        assertTrue(service.getStudentList().isEmpty());
        assertTrue(service.lookup("id", id).isEmpty());
    }

    @Test
    void testLookupNonExistent_ShouldReturnEmptyList() {
        List<Student> result = service.lookup("id", "UNKNOWN-ID");
        assertTrue(result.isEmpty());
    }
}