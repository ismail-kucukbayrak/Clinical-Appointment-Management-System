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
        JFrame mainFrame = new JFrame("Klinik Randevu Sistemi");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setBounds(50, 50, 700, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1));

        JLabel titleLabel = new JLabel("Klinik Randevu Sistemi", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel);

        JButton patientButton = new JButton("Hasta İşlemleri");
        JButton doctorButton = new JButton("Doktor İşlemleri");
        JButton rendezvousButton = new JButton("Randevu İşlemleri");
        JButton sectionButton = new JButton("Bölüm İşlemleri");
        JButton hospitalButton = new JButton("Hastane İşlemleri");
        

        panel.add(patientButton);
        panel.add(doctorButton);
        panel.add(rendezvousButton);
        panel.add(sectionButton);
        panel.add(hospitalButton);

        mainFrame.add(panel);
        mainFrame.setVisible(true);

        patientButton.addActionListener(e -> patientMenu());
        doctorButton.addActionListener(e -> doctorMenu());
        rendezvousButton.addActionListener(e -> rendezvousMenu());
        sectionButton.addActionListener(e -> sectionMenu());
        hospitalButton.addActionListener(e -> hospitalMenu());
    }

    private void patientMenu() {
        JFrame frame = new JFrame("Hasta İşlemleri");
        frame.setBounds(200, 200, 500, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JButton addPatientButton = new JButton("Hasta Ekle");
        JButton listPatientsButton = new JButton("Hastaları Listele");
        JButton backButton = new JButton("Ana Menüye Dön");

        panel.add(addPatientButton);
        panel.add(listPatientsButton);
        panel.add(backButton);

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
                "Hasta ID:", idField,
                "Hasta ismi:", nameField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Hasta Ekle", JOptionPane.OK_CANCEL_OPTION);
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
                JOptionPane.showMessageDialog(null, "Hasta başarıyla eklendi!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Hasta Eklenemedi!", "Hata", JOptionPane.ERROR_MESSAGE);
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

        JOptionPane.showMessageDialog(parentFrame, scrollPane, "Hasta Listesi", JOptionPane.INFORMATION_MESSAGE);
    }

    private void doctorMenu() {
        JFrame frame = new JFrame("Doktor İşlemleri");
        frame.setBounds(200, 200, 500, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));

        JButton addDoctorButton = new JButton("Doktor Ekle");
        JButton  backButton= new JButton("Ana Menüye dön");

        panel.add(addDoctorButton);
        panel.add(backButton);

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
                "Kimlik No:", idField,
                "Doktor Adı:", nameField,
                "Diploma ID:", diplomaField,
                "Hastane ID:", hospitalField,
                "Bölüm ID:", sectionField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Doktor Ekle", JOptionPane.OK_CANCEL_OPTION);
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
                JOptionPane.showMessageDialog(null, "Doktor başarıyla eklendi!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Doktor eklenemedi!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void rendezvousMenu() {
        JFrame frame = new JFrame("Randevu İşlemleri");
        frame.setBounds(200, 200, 500, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JButton createRendezvousButton = new JButton("Randevu al");
        JButton listRendezvousButton = new JButton("Randevuları Listele");
        JButton backButton = new JButton("Ana Menüye Dön");

        panel.add(createRendezvousButton);
        panel.add(listRendezvousButton);
        panel.add(backButton);

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
                "Hasta ID:", patientidField,
                "Hastane ID:", hospitalidField,
                "Bölüm ID:", sectionidField,
                "Doktor ID:", diplomaidField,
                "Tarih ve Saat (yyyy-MM-dd HH:mm):", dateTimeField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Hasta Ekle", JOptionPane.OK_CANCEL_OPTION);
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
                
                JOptionPane.showMessageDialog(null, "Randevu Oluşturuldu!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Randevu Oluşturulamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
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

        JOptionPane.showMessageDialog(parentFrame, scrollPane, "Hasta Listesi", JOptionPane.INFORMATION_MESSAGE);
    }

    private void sectionMenu() {
        JFrame frame = new JFrame("Bölüm İşlemleri");
        frame.setBounds(200, 200, 500, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JButton createSectionButton = new JButton("Bölüm Ekle");
        JButton backButton = new JButton("Ana Menüye Dön");

        panel.add(createSectionButton);
        panel.add(backButton);

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
                "Bölüm ID:", idField,
                "Bölüm ismi:", sectionNameField,
                "Hastane ID:", hospitalidField 
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Bölüm Ekle", JOptionPane.OK_CANCEL_OPTION);
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
                
                JOptionPane.showMessageDialog(null, "Bölüm başarıyla eklendi!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Bölüm eklenemedi!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void hospitalMenu() {
        JFrame frame = new JFrame("Hastane İşlemleri");
        frame.setBounds(200, 200, 500, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JButton createHospitalButton = new JButton("Hastanele Ekle");
        JButton listHospitalsButton = new JButton("Hastaneleri Listele");
        JButton backButton = new JButton("Ana Menüye Dön");

        panel.add(createHospitalButton);
        panel.add(listHospitalsButton);
        panel.add(backButton);

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
                "Hastane ID:", idField,
                "Hastane ismi:", nameField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Hastane Ekle", JOptionPane.OK_CANCEL_OPTION);
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
                
                JOptionPane.showMessageDialog(null, "Hastane başarıyla eklendi!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Hastane Eklenemedi", "Hata", JOptionPane.ERROR_MESSAGE);
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

        JOptionPane.showMessageDialog(parentFrame, scrollPane, "Hasta Listesi", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuiLauncher().start());
    }
}
