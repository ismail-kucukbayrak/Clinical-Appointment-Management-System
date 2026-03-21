package test;

import model.Rendezvous;
import model.Patient;
import model.Doctor;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class RendezvousTest {

    @Test
    void testRendezvousConstructorAndGetters() {
        // Arrange
        Patient patient = new Patient("John Doe", 12345678901L);
        Doctor doctor = new Doctor("Dr. Smith", 98765432101L, 101);
        Date dateTime = new Date();

        // Act
        Rendezvous rendezvous = new Rendezvous(patient, doctor, dateTime);

        // Assert
        assertEquals(patient, rendezvous.getPatient(), "Patient should match the one provided in constructor.");
        assertEquals(doctor, rendezvous.getDoctor(), "Doctor should match the one provided in constructor.");
        assertEquals(dateTime, rendezvous.getDateTime(), "DateTime should match the value provided in constructor.");
    }

    @Test
    void testSetDateTime() {
        // Arrange
        Patient patient = new Patient("John Doe", 12345678901L);
        Doctor doctor = new Doctor("Dr. Smith", 98765432101L, 101);
        Date initialDateTime = new Date();
        Rendezvous rendezvous = new Rendezvous(patient, doctor, initialDateTime);

        // Act
        Date newDateTime = new Date(initialDateTime.getTime() + 100000);
        rendezvous.setDateTime(newDateTime);

        // Assert
        assertEquals(newDateTime, rendezvous.getDateTime(), "DateTime should be updated to the new value.");
    }
}
