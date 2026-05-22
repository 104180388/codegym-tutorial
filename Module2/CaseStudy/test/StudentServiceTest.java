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
    void testDefineStudent_ShouldMaintainAlphabeticalOrder() {
        service.define("Tuấn", "12A");
        service.define("An", "12B");
        service.define("Bình", "12A");

        List<Student> list = service.getStudentList();

        assertEquals("An", list.get(0).getName());
        assertEquals("Bình", list.get(1).getName());
        assertEquals("Tuấn", list.get(2).getName());
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
    void testLookupByClass_ShouldReturnMultipleStudents() {
        service.define("Anh", "12A");
        service.define("Bảo", "12A");
        service.define("Chi", "12B");

        List<Student> class12A = service.lookup("class", "12A");

        assertEquals(2, class12A.size(), "Lớp 12A phải có 2 học sinh");
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