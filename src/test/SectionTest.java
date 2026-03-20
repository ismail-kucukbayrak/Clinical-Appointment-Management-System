package test;

import model.Section;
import model.Doctor;
import exception.DuplicateInfoException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SectionTest {

    @Test
    void testAddDoctorSuccessfully() {
        // Arrange
        Section section = new Section(101, "Cardiology");
        Doctor doctor1 = new Doctor("Dr. John Doe", 12345678901L, 101);

        // Act & Assert
        assertDoesNotThrow(() -> {
            section.addDoctor(doctor1);
        });

        assertTrue(section.getDoctor(101) != null, "The doctor should be successfully added to the section.");
        assertEquals(doctor1, section.getDoctor(101), "The added doctor should match the expected doctor.");
    }

    @Test
    void testAddDuplicateDoctorThrowsException() {
        // Arrange
        Section section = new Section(101, "Cardiology");
        Doctor doctor1 = new Doctor("Dr. John Doe", 12345678901L, 101);

        try {
            section.addDoctor(doctor1); // İlk ekleme başarılı
        } catch (DuplicateInfoException e) {
            fail("No exception should be thrown for the first addition.");
        }

        // Act & Assert
        DuplicateInfoException exception = assertThrows(DuplicateInfoException.class, () -> {
            section.addDoctor(doctor1); // Aynı doktor tekrar ekleniyor
        });

        assertEquals("Bu doktor zaten bölümde mevcut!", exception.getMessage(), "Exception message should match.");
    }
}
