package model;

import java.io.Serializable;
import java.util.Date;

public class Rendezvous implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date dateTime;
    private Patient patient;
    private Doctor doctor;

    public Rendezvous(Patient patient, Doctor doctor, Date dateTime) {
        this.patient = patient;
        this.doctor = doctor;
        this.dateTime = dateTime;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    @Override
    public String toString() {
        return "Rendezvous{" + "dateTime=" + dateTime + ", hasta=" + patient.getName() + ", doctor=" + doctor.getName() + '}';
    }
}
