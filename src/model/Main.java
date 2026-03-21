package model;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static boolean isGuiMode = false;

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        Scanner input = new Scanner(System.in);
        int secim = 0;

        System.out.println("Which mode would you like to use?");
        System.out.println("1: GUI mode");
        System.out.println("2: Console mode");
        System.out.print("CHOICE: ");
        secim = input.nextInt();
        
        if (secim == 1) {
            isGuiMode = true;
        }

        if (isGuiMode) {
            GuiLauncher launcher1 = new GuiLauncher();
            launcher1.start();
        } else {
            ConsoleLauncher launcher2 = new ConsoleLauncher();
            launcher2.start();
        }
        input.close();
    }
}
