package model;

import java.io.Serializable;
import java.util.Date;
import java.util.Calendar;
import java.util.LinkedList;

public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;
    private LinkedList<Rendezvous> sessions;
    private int maxPatientPerDay;
    private Doctor doctor;

    public Schedule(int maxPatientPerDay, Doctor doctor) {
        this.maxPatientPerDay = maxPatientPerDay;
        this.sessions = new LinkedList<>();
        this.doctor = doctor;
    }

    public Schedule() {
        this.maxPatientPerDay = 10;
        this.sessions = new LinkedList<>();
        this.doctor = null;
    }

    public boolean addRendezvous(Patient p, Date desired) {
        Calendar wanted = Calendar.getInstance();
        wanted.setTime(desired);

        long rendezvousCount = sessions.stream()
            .filter(rand -> {
                Calendar current = Calendar.getInstance();
                current.setTime(rand.getDateTime());
                return wanted.get(Calendar.YEAR) == current.get(Calendar.YEAR) &&
                       wanted.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR);
            }).count();

        if (rendezvousCount < maxPatientPerDay) {
            sessions.add(new Rendezvous(p, doctor, desired));
            return true;
        }
        return false;
    }

    public int getMaxPatientPerDay() {
        return maxPatientPerDay;
    }

    public void setMaxPatientPerDay(int maxPatientPerDay) {
        this.maxPatientPerDay = maxPatientPerDay;
    }

    public LinkedList<Rendezvous> getSessions() {
        return sessions;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "maxPatientPerDay=" + maxPatientPerDay +
                ", doctor=" + doctor +
                ", sessions=" + sessions +
                '}';
    }
}
