package test;

import model.Schedule;
import model.Patient;
import model.Doctor;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleTest {

    @Test
    void testAddRendezvousSuccessfully() {
        // Arrange
        Doctor doctor = new Doctor("Dr. Smith", 98765432101L, 101);
        Schedule schedule = new Schedule(2, doctor); // Max 2 patients per day
        Patient patient1 = new Patient("John Doe", 12345678901L);
        Patient patient2 = new Patient("Jane Doe", 12345678902L);

        Date today = new Date();

        // Act
        boolean result1 = schedule.addRendezvous(patient1, today);
        boolean result2 = schedule.addRendezvous(patient2, today);

        // Assert
        assertTrue(result1, "The first rendezvous should be successfully added.");
        assertTrue(result2, "The second rendezvous should be successfully added.");
        assertEquals(2, schedule.getSessions().size(), "The schedule should contain two rendezvous.");
    }

    @Test
    void testAddRendezvousFailsWhenExceedingMax() {
        // Arrange
        Doctor doctor = new Doctor("Dr. Smith", 98765432101L, 101);
        Schedule schedule = new Schedule(1, doctor); // Max 1 patient per day
        Patient patient1 = new Patient("John Doe", 12345678901L);
        Patient patient2 = new Patient("Jane Doe", 12345678902L);

        Date today = new Date();

        // Act
        boolean result1 = schedule.addRendezvous(patient1, today);
        boolean result2 = schedule.addRendezvous(patient2, today);

        // Assert
        assertTrue(result1, "The first rendezvous should be successfully added.");
        assertFalse(result2, "The second rendezvous should fail because the max patient limit is exceeded.");
        assertEquals(1, schedule.getSessions().size(), "The schedule should contain only one rendezvous.");
    }
}
