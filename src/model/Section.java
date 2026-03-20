package model;

import exception.DuplicateInfoException;
import java.io.Serializable;
import java.util.LinkedList;

public class Section implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int section_id;
    private String name;
    private LinkedList<Doctor> doctors;

    public Section(int id, String name) {
        this.section_id = id;
        this.name = name;
        this.doctors = new LinkedList<>();
    }

    public void listDoctors() {
        for (Doctor doctor : doctors) {
            System.out.println(doctor);
        }
    }

    public Doctor getDoctor(int diploma_id) {
        return doctors.stream()
                .filter(doctor -> doctor.getDiploma_id() == diploma_id)
                .findFirst()
                .orElse(null);
    }
    
    public LinkedList<Doctor> getDoctors() {
        return doctors; // Doktorları döndürüyoruz
    }

    public void addDoctor(Doctor doctor) throws DuplicateInfoException {
        if (doctors.contains(doctor)) {
            throw new DuplicateInfoException("Bu doktor zaten bölümde mevcut!");
        }
        doctors.add(doctor);
    }

    public int getId() {
        return section_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	@Override
	public String toString() {
		return section_id + " - " + name;
	}
    
    
}
