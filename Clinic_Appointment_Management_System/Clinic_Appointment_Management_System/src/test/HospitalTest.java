package test;

import model.Hospital;
import model.Section;
import exception.DuplicateInfoException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HospitalTest {

    @Test
    void testAddSectionSuccessfully() {
        // Arrange
        Hospital hospital = new Hospital(1, "City Hospital");
        Section section1 = new Section(101, "Cardiology");

        // Act & Assert
        assertDoesNotThrow(() -> {
            hospital.addSection(section1);
        });

        assertTrue(hospital.getSections().contains(section1), "The section should be added successfully.");
    }

    @Test
    void testAddDuplicateSectionThrowsException() {
        // Arrange
        Hospital hospital = new Hospital(1, "City Hospital");
        Section section1 = new Section(101, "Cardiology");

        try {
            hospital.addSection(section1); // İlk ekleme başarılı
        } catch (DuplicateInfoException e) {
            fail("No exception should be thrown for the first addition.");
        }

        // Act & Assert
        DuplicateInfoException exception = assertThrows(DuplicateInfoException.class, () -> {
            hospital.addSection(section1); // Aynı bölüm tekrar ekleniyor
        });

        assertEquals("Bu bölüm zaten hastanede mevcut!", exception.getMessage());
    }
}
