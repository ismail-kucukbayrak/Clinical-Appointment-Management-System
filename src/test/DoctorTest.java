package test;

import model.Doctor;
import model.Schedule;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DoctorTest {

    @Test
    void testDoctorConstructorAndGetters() {
        // Arrange
        String name = "Dr. John Doe";
        long nationalId = 12345678901L;
        int diplomaId = 101;

        // Act
        Doctor doctor = new Doctor(name, nationalId, diplomaId);

        // Assert
        assertEquals(name, doctor.getName(), "Name should match the value provided in constructor.");
        assertEquals(nationalId, doctor.getNational_id(), "National ID should match the value provided in constructor.");
        assertEquals(diplomaId, doctor.getDiploma_id(), "Diploma ID should match the value provided in constructor.");
    }

    @Test
    void testSetSchedule() {
        // Arrange
        Doctor doctor = new Doctor("Dr. John Doe", 12345678901L, 101);
        Schedule schedule = new Schedule(10,doctor); // Max 10 patients per day

        // Act
        doctor.setSchedule(schedule);

        // Assert
        assertNotNull(doctor.getSchedule(), "Schedule should not be null after being set.");
        assertEquals(schedule, doctor.getSchedule(), "Schedule should match the value set.");
    }
}
