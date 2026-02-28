package model;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static boolean isGuiMode = false;

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        Scanner input = new Scanner(System.in);
        int secim = 0;

        System.out.println("Hangi modda kullanmak istiyorsun: ");
        System.out.println("1: GUI mod");
        System.out.println("2: Console mod");
        System.out.print("SEÇİM: ");
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
