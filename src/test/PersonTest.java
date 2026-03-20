package test;

import model.Person;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    @Test
    void testPersonConstructorAndGetters() {
        // Arrange
        String name = "John Doe";
        long nationalId = 12345678901L;

        // Act
        Person person = new Person(name, nationalId);

        // Assert
        assertEquals(name, person.getName(), "Name should match the value provided in constructor.");
        assertEquals(nationalId, person.getNational_id(), "National ID should match the value provided in constructor.");
    }

    @Test
    void testSetName() {
        // Arrange
        Person person = new Person("John Doe", 12345678901L);

        // Act
        person.setName("Jane Doe");

        // Assert
        assertEquals("Jane Doe", person.getName(), "Name should be updated to the new value.");
    }
}
