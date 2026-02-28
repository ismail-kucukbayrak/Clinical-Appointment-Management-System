package test;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class CRSTest {
    private CRS aCRS;

    @BeforeEach
    void setUp() {
        aCRS = new CRS();

        // Hasta ekleme
        Patient patient = new Patient("John Doe", 12345678901L);
        aCRS.getPatients().put(12345678901L, patient);

        // Doktor ekleme ve Schedule atama
        Doctor doctor = new Doctor("Dr. Smith", 98765432101L, 101);
        Schedule schedule = new Schedule(5, doctor);
        doctor.setSchedule(schedule);

        // Bölüm ve Hastane ekleme
        Section section = new Section(10, "Cardiology");
        try {
            section.addDoctor(doctor);
        } catch (Exception e) {
            fail("No exception should be thrown while adding doctor.");
        }

        Hospital hospital = new Hospital(1, "City Hospital");
        try {
            hospital.addSection(section);
        } catch (Exception e) {
            fail("No exception should be thrown while adding section.");
        }

        aCRS.getHospitals().put(1, hospital);
    }

    @Test
    void testMakeRendezvous() {
        // Arrange
        long patientId = 12345678901L;
        int hospitalId = 1;
        int sectionId = 10;
        int doctorDiplomaId = 101;
        Date desiredDate = new Date();

        // Act & Assert
        assertDoesNotThrow(() -> {
            boolean result = aCRS.makeRendezvous(patientId, hospitalId, sectionId, doctorDiplomaId, desiredDate);
            assertTrue(result, "Rendezvous should be successfully created.");
        });
    }

    @Test
    void testSaveAndLoadTables() {
        // Arrange
        String fileName = "datas.dat";

        try {
            // Save işlemi
            aCRS.saveTablesToDisk(aCRS);

            // Load işlemi
            CRS loadedCRS = new CRS();
            loadedCRS.loadTablesToDisk();

            // Assert
            assertEquals(aCRS.getPatients().size(), loadedCRS.getPatients().size(), "Patients should match after load.");
            assertEquals(aCRS.getHospitals().size(), loadedCRS.getHospitals().size(), "Hospitals should match after load.");
            assertEquals(aCRS.getRendezvouss().size(), loadedCRS.getRendezvouss().size(), "Rendezvous should match after load.");
        } catch (Exception e) {
            fail("Exception occurred during save/load process: " + e.getMessage());
        } finally {
            // Temizlik
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
