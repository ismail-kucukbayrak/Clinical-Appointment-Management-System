package model;

import exception.DuplicateInfoException;
import java.io.Serializable;
import java.util.LinkedList;

public class Hospital implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int hospital_id;
    private String name;
    private LinkedList<Section> sections;

    public Hospital(int id, String name) {
        this.hospital_id = id;
        this.name = name;
        this.sections = new LinkedList<>();
    }

    public Hospital() {
        this.hospital_id = 0;
        this.name = "";
        this.sections = new LinkedList<>();
    }

    public Section getSection(int id) {
        return sections.stream()
                .filter(section -> section.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Section getSection(String name) {
        return sections.stream()
                .filter(section -> section.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public void addSection(Section section) throws DuplicateInfoException {
        if (sections.contains(section)) {
        	throw new DuplicateInfoException("This section already exists in the hospital!");
        }
        sections.add(section);
    }

    public int getId() {
        return hospital_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<Section> getSections() {
        return sections;
    }
    
    public void showSections() {
    	for (Section aSection : sections) {
			System.out.println(aSection);
		}
    }

    @Override
    public String toString() {
        return hospital_id + " - "+ name;
    }
}
