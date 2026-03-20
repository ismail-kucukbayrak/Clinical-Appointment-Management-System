package model;

public class Doctor extends Person {
    private static final long serialVersionUID = 1L;
    private final int diploma_id;
    private Schedule schedule;

    public Doctor(String name, long national_id, int diploma_id) {
        super(name, national_id);
        this.diploma_id = diploma_id;
        this.schedule = new Schedule();
    }

    public int getDiploma_id() {
        return diploma_id;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    @Override
    public String toString() {
        return super.toString() + " - (Diploma ID: " + diploma_id + ")";
    }
}
