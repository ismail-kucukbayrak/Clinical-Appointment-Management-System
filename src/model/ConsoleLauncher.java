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
        System.out.println("Klinik Randevu Sistemi");
    	System.out.println("--------------------------------------------------");
        System.out.println("1-Hasta İşlemleri");
        System.out.println("2-Doktor İşlemleri");
        System.out.println("3-Randevu İşlemleri");
        System.out.println("4-Bölüm İşlemleri");
        System.out.println("5-Hastane İşlemleri");
        System.out.print("SEÇİM: ");

        secim = in.nextInt();

        switch (secim) {
            case 1:
            	System.out.println("--------------------------------------------------");
                System.out.println("1-Hasta ekle");
                System.out.println("2-Hastaları listele");
                System.out.println("3-Ana menü");                
                System.out.print("SEÇİM: ");
                secim = in.nextInt();
                
                if(secim == 1) {
                	System.out.println("--------------------------------------------------");
                    System.out.print("Kimlik no: ");
                    tmplong = in.nextLong();
                    in.nextLine();
                    System.out.print("Hasta ismi: ");
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
            	System.out.println("1-Doktor ekle");
                System.out.println("2-Ana menü");                
                System.out.print("SEÇİM: ");
                secim = in.nextInt();
                
                if(secim == 1) {
                	System.out.println("--------------------------------------------------");
                    System.out.print("Kimlik no: ");
                    tmplong = in.nextLong();
                    in.nextLine();
                    System.out.print("Doktor ismi: ");
                    doctorname = in.nextLine();
                    System.out.print("Diploma ID: ");
                    diplomaid = in.nextInt();
                    Doctor aDoctor = new Doctor(doctorname,tmplong,diplomaid);
                    System.out.println("--------------------------------------------------");
                    System.out.println("HASTANELER");
                    System.out.println("--------------------------------------------------");
                    aCRS.loadTablesToDisk();
                    aCRS.showHospital();
                    System.out.print("Eklemek istediğiniz hastane ID'si: ");
                    hospitalid = in.nextInt();
                    System.out.println("--------------------------------------------------");
                    System.out.println("BÖLÜMLER");
                    System.out.println("--------------------------------------------------");
                    aCRS.loadTablesToDisk();
                    aCRS.getHospitals().get(hospitalid).showSections();
                    System.out.print("Eklemek istediğiniz bölüm ID'si: ");
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
                System.out.println("1-Randevu oluştur.");
                System.out.println("2-Randevuları listele");
                System.out.println("3-Ana menü");
                System.out.println("SEÇİM: ");
                secim = in.nextInt();
                
                if(secim == 1) {
                	System.out.println("--------------------------------------------------");
                	System.out.println("Hastalar:");
                	aCRS.loadTablesToDisk();
                    aCRS.showPatient();
                    System.out.print("Hasta ID'si: ");
                    tmplong = in.nextLong();
                    
                	System.out.println("--------------------------------------------------");
                	System.out.println("Hastaneler:");
                	aCRS.loadTablesToDisk();
                    aCRS.showHospital();
                    System.out.print("Hastane ID'si: ");
                    hospitalid = in.nextInt();
                    
                    System.out.println("--------------------------------------------------");
                	System.out.println("Bölümler:");
                	aCRS.loadTablesToDisk();
                    aCRS.getHospitals().get(hospitalid).showSections();
                    System.out.print("Bölüm ID'si: ");
                    sectionid = in.nextInt();
                    
                    System.out.println("--------------------------------------------------");
                	System.out.println("Doktorlar:");
                	aCRS.loadTablesToDisk();
                    aCRS.getHospitals().get(hospitalid).getSection(sectionid).listDoctors();
                    System.out.print("Diploma ID'si: ");
                    diplomaid = in.nextInt();
                    
                    System.out.println("--------------------------------------------------");
                    in.nextLine();
                    System.out.print("Tarih ve saati giriniz (yyyy-MM-dd HH:mm formatında): ");
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
            	System.out.println("1-Bölüm ekle");
                System.out.println("2-Ana menü");                
                System.out.print("SEÇİM: ");
                secim = in.nextInt();
                
                if(secim == 1) {
                	System.out.println("--------------------------------------------------");
                    System.out.print("Bölüm ID: ");
                    sectionid = in.nextInt();
                    in.nextLine();
                    System.out.print("Bölüm ismi: ");
                    sectionname = in.nextLine();
                    Section aSection = new Section(sectionid, sectionname);
                    System.out.println("--------------------------------------------------");
                    System.out.println("HASTANELER");
                    aCRS.loadTablesToDisk();
                    aCRS.showHospital();
                    System.out.print("Eklemek istediğiniz hastane ID'si: ");
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
            	System.out.println("1-Hastane ekle");
                System.out.println("2-Hastaneleri listele");
                System.out.println("3-Ana menü");                
                System.out.print("SEÇİM: ");
                secim = in.nextInt();
                
                if(secim == 1) {
                	System.out.println("--------------------------------------------------");
                    System.out.print("Hastane id: ");
                    hospitalid = in.nextInt();
                    in.nextLine();
                    System.out.print("Hastane ismi: ");
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
                System.out.println("Geçersiz seçim!");
                start();
                break;
        }
        in.close();
    }
}
