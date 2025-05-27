package com.oop10x.steddyvalley.utils ;
import java.util.Random ;
import java.util.Scanner ;

public class RNG {
    public static int randomizeNumber (int bawah, int atas) {
        Random random = new Random() ;
        int randomNumber = random.nextInt(atas - bawah + 1) + bawah ;
        return randomNumber ;
    }
    public static boolean playerGuess(int targetNumber, int maxAttempts) {
        Scanner scanner = new Scanner(System.in);
        int attempt = 0;

        while (attempt < maxAttempts) {
            System.out.print("Tebakan ke-" + (attempt + 1) + ": ");
            int guess = scanner.nextInt();

            if (guess == targetNumber) {
                return true;}
            // else if (guess < targetNumber) {
            //     System.out.println("Terlalu kecil.");
            // } else {
            //     System.out.println("Terlalu besar.");
            // }
            attempt++;
        }
        scanner.close();
        return false;
    }
}