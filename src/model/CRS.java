package model;

import exception.IDException;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class CRS implements Serializable {
    private static final long serialVersionUID = 1L;
    private HashMap<Long, Patient> patients; 
    private HashMap<Integer, Hospital> hospitals; 
    private LinkedList<Rendezvous> rendezvouss;

    public CRS() {
        this.patients = new HashMap<Long, Patient>();
        this.hospitals = new HashMap<Integer, Hospital>();
        this.rendezvouss = new LinkedList<Rendezvous>();
    }
    
    public void addPatient(Long x, Patient y) {
    	patients.put(x, y);
    }
    public void addHospital(Integer x, Hospital y) {
    	hospitals.put(x, y);
    }
    public void addRendezvous(Rendezvous x) {
    	rendezvouss.add(x);
    }
    public void showPatient() {
    	for (Patient aPatient : patients.values()) {
			System.out.println(aPatient);
		}
    }
    public void showHospital() {
    	for (Hospital aHospital : hospitals.values()) {
			System.out.println(aHospital);
		}
    }
    public void showRendezvous() {
    	for (Rendezvous aRendezvous : rendezvouss) {
			System.out.println(aRendezvous);
		}
    }
    

    public boolean makeRendezvous(long national_id, int hospital_id, int section_id, int diploma_id, Date desiredDate) {
        Patient patient = patients.get(national_id);
        if (patient == null) {
            throw new IDException("Invalid ID for patient: " + national_id);
        }

        Hospital hospital = hospitals.get(hospital_id);
        if (hospital == null) {
            throw new IDException("Invalid ID for hospital: " + hospital_id);
        }

        Section section = hospital.getSection(section_id);
        if (section == null) {
            throw new IDException("Invalid ID for section: " + section_id);
        }

        Doctor doctor = section.getDoctor(diploma_id);
        if (doctor == null) {
            throw new IDException("Invalid ID for doctor: " + diploma_id);
        }

        Schedule schedule = doctor.getSchedule();
        if (schedule == null) {
            System.out.println("!");
            return false;
        }

        boolean result = schedule.addRendezvous(patient, desiredDate);
        if (result) {
            rendezvouss.add(new Rendezvous(patient, doctor, desiredDate));
        }
        return result;
    }

    public void saveTablesToDisk(CRS aCRS) throws IOException {
    	String fileName = "Datas.dat";
    	
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(aCRS);
            oos.close();
        }
    }

    public void loadTablesToDisk() throws IOException, ClassNotFoundException {
    	String fileName = "Datas.dat";

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            CRS loadedCRS = (CRS) ois.readObject();
            this.patients = loadedCRS.getPatients();
            this.hospitals = loadedCRS.getHospitals();
            this.rendezvouss = loadedCRS.getRendezvouss();
            ois.close();
        }
    }

    public HashMap<Long, Patient> getPatients() {
        return patients;
    }

    public HashMap<Integer, Hospital> getHospitals() {
        return hospitals;
    }

    public LinkedList<Rendezvous> getRendezvouss() {
        return rendezvouss;
    }
}
