package model;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Scanner;


public class ConsoleLauncher {
	CRS aCRS = new CRS();
    public void start() throws IOException, ClassNotFoundException {
    	
    	Scanner in = new Scanner(System.in);
        int secim = 0;
        
        long tmplong = 0;

        int diplomaid = 0;
        int sectionid = 0;
        int hospitalid = 0;

        String patientname = "";
        String doctorname = "";
        String hospitalname = "";
        String sectionname = "";
   

    	System.out.println("--------------------------------------------------");
        System.out.println("Clinical Appointment System");
    	System.out.println("--------------------------------------------------");
        System.out.println("1-Patient Operations");
        System.out.println("2-Doctor Operations");
        System.out.println("3-Rendezvous Operations");
        System.out.println("4-Section Operations");
        System.out.println("5-Hospital Operations");
        System.out.print("CHOICE: ");

        secim = in.nextInt();

        switch (secim) {
            case 1:
            	System.out.println("--------------------------------------------------");
                System.out.println("1-Add Patient");
                System.out.println("2-List Patients");
                System.out.println("3-Main Menu");                
                System.out.print("CHOICE: ");
                secim = in.nextInt();
                
                if(secim == 1) {
                	System.out.println("--------------------------------------------------");
                    System.out.print("National ID: ");
                    tmplong = in.nextLong();
                    in.nextLine();
                    System.out.print("Patient Name: ");
                    patientname = in.nextLine();
                    Patient aPatient = new Patient(patientname,tmplong);
                    aCRS.addPatient(tmplong, aPatient);
                    aCRS.saveTablesToDisk(aCRS);
                    start();
                }else if(secim == 2){
                	System.out.println("--------------------------------------------------");
                	aCRS.loadTablesToDisk();
                	aCRS.showPatient();
                	start();
                }else {
                	System.out.println("--------------------------------------------------");
                	start();
                }
                break;
                
            case 2:
            	System.out.println("--------------------------------------------------");
            	System.out.println("1-Add Doctor");
                System.out.println("2-Main Menu");                
                System.out.print("CHOICE: ");
                secim = in.nextInt();
                
                if(secim == 1) {
                	System.out.println("--------------------------------------------------");
                    System.out.print("National ID: ");
                    tmplong = in.nextLong();
                    in.nextLine();
                    System.out.print("Doctor Name: ");
                    doctorname = in.nextLine();
                    System.out.print("Diploma ID: ");
                    diplomaid = in.nextInt();
                    Doctor aDoctor = new Doctor(doctorname,tmplong,diplomaid);
                    System.out.println("--------------------------------------------------");
                    System.out.println("HOSPITALS");
                    System.out.println("--------------------------------------------------");
                    aCRS.loadTablesToDisk();
                    aCRS.showHospital();
                    System.out.print("Enter Hospital ID: ");
                    hospitalid = in.nextInt();
                    System.out.println("--------------------------------------------------");
                    System.out.println("SECTIONS");
                    System.out.println("--------------------------------------------------");
                    aCRS.loadTablesToDisk();
                    aCRS.getHospitals().get(hospitalid).showSections();
                    System.out.print("Enter Section ID: ");
                    sectionid = in.nextInt();
                    aCRS.getHospitals().get(hospitalid).getSection(sectionid).addDoctor(aDoctor);
                    aCRS.saveTablesToDisk(aCRS);
                    start();
                }else {
                	System.out.println("--------------------------------------------------");
                	start();
                }
                break;
            case 3:
            	System.out.println("--------------------------------------------------");
                System.out.println("1-Create Rendezvous");
                System.out.println("2-List Rendezvous");
                System.out.println("3-Main Menu");
                System.out.println("CHOICE: ");
                secim = in.nextInt();
                
                if(secim == 1) {
                	System.out.println("--------------------------------------------------");
                	System.out.println("Patients:");
                	aCRS.loadTablesToDisk();
                    aCRS.showPatient();
                    System.out.print("Patient ID: ");
                    tmplong = in.nextLong();
                    
                	System.out.println("--------------------------------------------------");
                	System.out.println("Hospitals:");
                	aCRS.loadTablesToDisk();
                    aCRS.showHospital();
                    System.out.print("Hospital ID: ");
                    hospitalid = in.nextInt();
                    
                    System.out.println("--------------------------------------------------");
                	System.out.println("Sections:");
                	aCRS.loadTablesToDisk();
                    aCRS.getHospitals().get(hospitalid).showSections();
                    System.out.print("Section ID: ");
                    sectionid = in.nextInt();
                    
                    System.out.println("--------------------------------------------------");
                	System.out.println("Doctors:");
                	aCRS.loadTablesToDisk();
                    aCRS.getHospitals().get(hospitalid).getSection(sectionid).listDoctors();
                    System.out.print("Diploma ID: ");
                    diplomaid = in.nextInt();
                    
                    System.out.println("--------------------------------------------------");
                    in.nextLine();
                    System.out.print("Enter date and time (yyyy-MM-dd HH:mm format): ");
                    String inputDateTime = in.nextLine();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    
                    LocalDateTime dateTime = LocalDateTime.parse(inputDateTime, formatter);
                    
                    Date desiredDate = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
                    
                    aCRS.makeRendezvous(tmplong,hospitalid,sectionid,diplomaid,desiredDate);
                    aCRS.saveTablesToDisk(aCRS);
                    start();
                }else if(secim == 2){
                	System.out.println("--------------------------------------------------");
                	aCRS.loadTablesToDisk();
                	aCRS.showRendezvous();
                	start();
                }else {
                	start();
                }
                break;
            case 4:
            	System.out.println("--------------------------------------------------");
            	System.out.println("1-Add Section");
                System.out.println("2-Main Menu");                
                System.out.print("CHOICE: ");
                secim = in.nextInt();
                
                if(secim == 1) {
                	System.out.println("--------------------------------------------------");
                    System.out.print("Section ID: ");
                    sectionid = in.nextInt();
                    in.nextLine();
                    System.out.print("Section Name: ");
                    sectionname = in.nextLine();
                    Section aSection = new Section(sectionid, sectionname);
                    System.out.println("--------------------------------------------------");
                    System.out.println("HOSPITALS");
                    aCRS.loadTablesToDisk();
                    aCRS.showHospital();
                    System.out.print("Enter Hospital ID: ");
                    hospitalid = in.nextInt();
                    aCRS.getHospitals().get(hospitalid).addSection(aSection);
                    aCRS.saveTablesToDisk(aCRS);
                    start();
                }else {
                	start();
                }
                break;
            case 5:
            	System.out.println("--------------------------------------------------");
            	System.out.println("1-Add Hospital");
                System.out.println("2-List Hospitals");
                System.out.println("3-Main Menu");                
                System.out.print("CHOICE: ");
                secim = in.nextInt();
                
                if(secim == 1) {
                	System.out.println("--------------------------------------------------");
                    System.out.print("Hospital ID: ");
                    hospitalid = in.nextInt();
                    in.nextLine();
                    System.out.print("Hospital Name: ");
                    hospitalname = in.nextLine();
                    Hospital aHospital = new Hospital(hospitalid, hospitalname);
                    aCRS.addHospital(hospitalid, aHospital);
                    aCRS.saveTablesToDisk(aCRS);
                    start();
                }else if(secim == 2){
                	System.out.println("--------------------------------------------------");
                	aCRS.loadTablesToDisk();
                	aCRS.showHospital();
                	start();
                }else {
                	start();
                }
                break;
            default:
                System.out.println("Invalid choice!");
                start();
                break;
        }
        in.close();
    }
}