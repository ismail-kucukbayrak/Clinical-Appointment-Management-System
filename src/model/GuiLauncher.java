package model;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class GuiLauncher {
    private CRS aCRS = new CRS(); 

    public void start() {
        JFrame mainFrame = new JFrame("Clinical Appointment System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setBounds(50, 50, 700, 350);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Clinical Appointment System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton patientButton = new JButton("Patient Operations");
        JButton doctorButton = new JButton("Doctor Operations");
        JButton rendezvousButton = new JButton("Rendezvous Operations");
        JButton sectionButton = new JButton("Section Operations");
        JButton hospitalButton = new JButton("Hospital Operations");

        Dimension buttonSize = new Dimension(400, 50);

        JButton[] buttons = {
            patientButton, doctorButton, rendezvousButton, sectionButton, hospitalButton
        };

        for (JButton btn : buttons) {
            btn.setMaximumSize(buttonSize);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        panel.add(Box.createVerticalStrut(15));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));

        for (JButton btn : buttons) {
            panel.add(btn);
            panel.add(Box.createVerticalStrut(10));
        }

        mainFrame.add(panel);
        mainFrame.setVisible(true);

        patientButton.addActionListener(e -> patientMenu());
        doctorButton.addActionListener(e -> doctorMenu());
        rendezvousButton.addActionListener(e -> rendezvousMenu());
        sectionButton.addActionListener(e -> sectionMenu());
        hospitalButton.addActionListener(e -> hospitalMenu());
    }

    private void patientMenu() {
        JFrame frame = new JFrame("Patient Operations");
        frame.setBounds(200, 200, 500, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton addPatientButton = new JButton("Add Patient");
        JButton listPatientsButton = new JButton("List Patients");
        JButton backButton = new JButton("Back to Main Menu");

        Dimension buttonSize = new Dimension(220, 40);

        JButton[] buttons = {
            addPatientButton, listPatientsButton, backButton
        };

        for (JButton btn : buttons) {
            btn.setMaximumSize(buttonSize);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        panel.add(Box.createVerticalStrut(20));

        for (JButton btn : buttons) {
            panel.add(btn);
            panel.add(Box.createVerticalStrut(10));
        }

        frame.add(panel);
        frame.setVisible(true);

        addPatientButton.addActionListener(e -> addPatient());
        listPatientsButton.addActionListener(e -> listPatients(frame));
        backButton.addActionListener(e -> frame.dispose());
    }
    
    private void addPatient() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();

        Object[] message = {
                "Patient ID:", idField,
                "Patient name:", nameField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add Patient", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                long id = Long.parseLong(idField.getText());
                String name = nameField.getText();
                Patient patient = new Patient(name, id);
                
                try {
					aCRS.loadTablesToDisk();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                aCRS.addPatient(id , patient);
                try {
					aCRS.saveTablesToDisk(aCRS);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                JOptionPane.showMessageDialog(null, "Patient added successfully!");
            } catch (NumberFormatException e) {
            	JOptionPane.showMessageDialog(null, "Patient could not be added!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void listPatients(JFrame parentFrame) {
    	DefaultListModel<String> model = new DefaultListModel<>();
    	try {
			aCRS.loadTablesToDisk();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        for (Patient aPatient : aCRS.getPatients().values()) {
            model.addElement(aPatient.toString());
        }

        JList<String> patientList = new JList<>(model);
        JScrollPane scrollPane = new JScrollPane(patientList);

        JOptionPane.showMessageDialog(parentFrame, scrollPane, "Patient List", JOptionPane.INFORMATION_MESSAGE);
    }

    private void doctorMenu() {
        JFrame frame = new JFrame("Doctor Operations");
        frame.setBounds(200, 200, 500, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton addDoctorButton = new JButton("Add Doctor");
        JButton backButton = new JButton("Back to Main Menu");

        Dimension buttonSize = new Dimension(220, 40);

        JButton[] buttons = {
            addDoctorButton, backButton
        };

        for (JButton btn : buttons) {
            btn.setMaximumSize(buttonSize);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        panel.add(Box.createVerticalStrut(30));

        for (JButton btn : buttons) {
            panel.add(btn);
            panel.add(Box.createVerticalStrut(10));
        }

        frame.add(panel);
        frame.setVisible(true);

        addDoctorButton.addActionListener(e -> addDoctor());
        backButton.addActionListener(e -> frame.dispose());
    }

    private void addDoctor() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField diplomaField = new JTextField();
        JTextField hospitalField = new JTextField();
        JTextField sectionField = new JTextField();


        Object[] message = {
        		"National ID:", idField,
                "Doctor Name:", nameField,
                "Diploma ID:", diplomaField,
                "Hospital ID:", hospitalField,
                "Section ID:", sectionField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add Doctor", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                long id = Long.parseLong(idField.getText());
                String name = nameField.getText();
                int diploma = Integer.parseInt(diplomaField.getText());
                int hospital = Integer.parseInt(hospitalField.getText());
                int section = Integer.parseInt(sectionField.getText());
                
                Doctor aDoctor = new Doctor(name, id, diploma);
                try {
					aCRS.loadTablesToDisk();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                aCRS.getHospitals().get(hospital).getSection(section).addDoctor(aDoctor);
                try {
					aCRS.saveTablesToDisk(aCRS);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                JOptionPane.showMessageDialog(null, "Doctor added successfully!");
            } catch (NumberFormatException e) {
            	JOptionPane.showMessageDialog(null, "Doctor could not be added!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void rendezvousMenu() {
        JFrame frame = new JFrame("Rendezvous Operations");
        frame.setBounds(200, 200, 500, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton createRendezvousButton = new JButton("Create Rendezvous");
        JButton listRendezvousButton = new JButton("List Rendezvous");
        JButton backButton = new JButton("Back to Main Menu");

        Dimension buttonSize = new Dimension(220, 40);

        JButton[] buttons = {
            createRendezvousButton, listRendezvousButton, backButton
        };

        for (JButton btn : buttons) {
            btn.setMaximumSize(buttonSize);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        panel.add(Box.createVerticalStrut(20));

        for (JButton btn : buttons) {
            panel.add(btn);
            panel.add(Box.createVerticalStrut(10));
        }

        frame.add(panel);
        frame.setVisible(true);

        createRendezvousButton.addActionListener(e -> createRendezvous());
        listRendezvousButton.addActionListener(e -> listRendezvous(frame));
        backButton.addActionListener(e -> frame.dispose());
    }
    
    private void createRendezvous() {
    	JTextField patientidField = new JTextField();
    	JTextField hospitalidField = new JTextField();
    	JTextField sectionidField = new JTextField();
    	JTextField diplomaidField = new JTextField();
        JTextField dateTimeField = new JTextField("yyyy-MM-dd HH:mm");


        Object[] message = {
                "Patient ID:", patientidField,
                "Hospital ID:", hospitalidField,
                "Section ID:", sectionidField,
                "Doctor ID:", diplomaidField,
                "Date and Time (yyyy-MM-dd HH:mm):", dateTimeField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add Patient", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                long patientid = Long.parseLong(patientidField.getText());
                int hospitalid = Integer.parseInt(hospitalidField.getText());
                int sectionid = Integer.parseInt(sectionidField.getText());
                int diplomaid = Integer.parseInt(diplomaidField.getText());
                
                String dateTimeInput = dateTimeField.getText();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeInput, formatter);
                Date desiredDate = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
                
                try {
					aCRS.loadTablesToDisk();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                aCRS.makeRendezvous(patientid,hospitalid,sectionid,diplomaid,desiredDate);

                try {
					aCRS.saveTablesToDisk(aCRS);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                JOptionPane.showMessageDialog(null, "Rendezvous created!");
            } catch (NumberFormatException e) {
            	JOptionPane.showMessageDialog(null, "Rendezvous could not be created!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void listRendezvous(JFrame parentFrame) {
    	DefaultListModel<String> model = new DefaultListModel<>();
    	
    	try {
			aCRS.loadTablesToDisk();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        for (Rendezvous aRendezvous : aCRS.getRendezvouss()) {
            model.addElement(aRendezvous.toString());
        }

        JList<String> patientList = new JList<>(model);
        JScrollPane scrollPane = new JScrollPane(patientList);

        JOptionPane.showMessageDialog(parentFrame, scrollPane, "Patient List", JOptionPane.INFORMATION_MESSAGE);
    }

    private void sectionMenu() {
        JFrame frame = new JFrame("Section Operations");
        frame.setBounds(200, 200, 500, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton createSectionButton = new JButton("Add Section");
        JButton backButton = new JButton("Back to Main Menu");

        Dimension buttonSize = new Dimension(220, 40);

        JButton[] buttons = {
            createSectionButton, backButton
        };

        for (JButton btn : buttons) {
            btn.setMaximumSize(buttonSize);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        panel.add(Box.createVerticalStrut(30));

        for (JButton btn : buttons) {
            panel.add(btn);
            panel.add(Box.createVerticalStrut(10));
        }

        frame.add(panel);
        frame.setVisible(true);

        createSectionButton.addActionListener(e -> addSection());
        backButton.addActionListener(e -> frame.dispose());
    }

    private void addSection() {
    	JTextField idField = new JTextField();
        JTextField sectionNameField = new JTextField();
        JTextField hospitalidField = new JTextField();

        Object[] message = {
                "Section ID:", idField,
                "Section name:", sectionNameField,
                "Hospital ID:", hospitalidField 
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add Section", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = sectionNameField.getText();
                int hospitalid = Integer.parseInt(hospitalidField.getText());
                Section aSection = new Section(id,name);
                
                try {
					aCRS.loadTablesToDisk();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                aCRS.getHospitals().get(hospitalid).addSection(aSection);
                
                try {
					aCRS.saveTablesToDisk(aCRS);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                JOptionPane.showMessageDialog(null, "Section added successfully!");
            } catch (NumberFormatException e) {
            	JOptionPane.showMessageDialog(null, "Section could not be added!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void hospitalMenu() {
        JFrame frame = new JFrame("Hospital Operations");
        frame.setBounds(200, 200, 500, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton createHospitalButton = new JButton("Add Hospital");
        JButton listHospitalsButton = new JButton("List Hospitals");
        JButton backButton = new JButton("Back to Main Menu");

        Dimension buttonSize = new Dimension(220, 40);

        JButton[] buttons = {
            createHospitalButton, listHospitalsButton, backButton
        };

        for (JButton btn : buttons) {
            btn.setMaximumSize(buttonSize);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        panel.add(Box.createVerticalStrut(20));

        for (JButton btn : buttons) {
            panel.add(btn);
            panel.add(Box.createVerticalStrut(10));
        }

        frame.add(panel);
        frame.setVisible(true);

        createHospitalButton.addActionListener(e -> addHospital());
        listHospitalsButton.addActionListener(e -> listHospitals(frame));
        backButton.addActionListener(e -> frame.dispose());
    }

    private void addHospital() {
    	JTextField idField = new JTextField();
        JTextField nameField = new JTextField();

        Object[] message = {
                "Hospital ID:", idField,
                "Hospital name:", nameField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add Hospital", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                Hospital aHospital = new Hospital(id, name);
                
                try {
					aCRS.loadTablesToDisk();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                aCRS.addHospital(id, aHospital);
                
                try {
					aCRS.saveTablesToDisk(aCRS);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                JOptionPane.showMessageDialog(null, "Hospital added successfully!");
            } catch (NumberFormatException e) {
            	JOptionPane.showMessageDialog(null, "Hospital could not be added", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void listHospitals(JFrame parentFrame) {
        DefaultListModel<String> model = new DefaultListModel<>();
        try {
			aCRS.loadTablesToDisk();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        for (Hospital aHospital : aCRS.getHospitals().values()) {
            model.addElement(aHospital.toString());
        }

        JList<String> patientList = new JList<>(model);
        JScrollPane scrollPane = new JScrollPane(patientList);

        JOptionPane.showMessageDialog(parentFrame, scrollPane, "Patient List", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuiLauncher().start());
    }
}
