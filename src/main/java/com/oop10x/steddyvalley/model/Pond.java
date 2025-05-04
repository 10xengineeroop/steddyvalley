package com.oop10x.steddyvalley.model;

import com.oop10x.steddyvalley.utils.Fishable;
import com.oop10x.steddyvalley.utils.Placeable;
import com.oop10x.steddyvalley.utils.Position;

public class Pond implements Placeable, Fishable {
    private final Position position = new Position(0, 0);
    private int fishCount = 0;

    public Pond(int x, int y) {
        setX(x);
        setY(y);
    }

    @Override
    public void setX(int x) {
        position.setX(x);
    }

    @Override
    public int getX() {
        return position.getX();
    }

    @Override
    public void setY(int y) {
        position.setY(y);
    }
    public int getY() {
        return position.getY();
    }
    public boolean isFishable(Player player) {
        if (player.getEnergy() >= 5 && player.getEquippedItem().getName().equals("Fishing Rod")) {
            return true;
        } else {
            return false;
        }
    }

    public void Fish(Player player) {
        if (isFishable()) {
            // Pada saat action Fishing dimulai, world time akan dihentikan, ditambah 15 menit, dan baru berlanjut kembali setelah action Fishing selesai. Mekanik dari action Fishing ini adalah dengan menggunakan RNG (random number generator), 
            // Tipe common: tebak angka 1-10 (maks. 10 percobaan)
            // Tipe regular: tebak angka 1-100 (maks. 10 percobaan)
            // Tipe legendary: tebak angka 1-500 (maks. 7 percobaan)
            // Penjelasan skenario:
            // Player melakukan action Fishing
            // Hentikan world time, kurangi energi Player sebanyak 5 poin, tambahkan 15 menit ke world time
            // Permainan akan melakukan randomizing terhadap ikan yang akan ditangkap, misalnya hasil random-nya adalah ikan Halibut yang merupakan regular fish
            // Permainan akan melakukan randoming terhadap angka 1-100, misalnya hasil random-nya adalah 77
            // Player diberikan 10 kesempatan untuk menebak angka tersebut
            // Apabila Player berhasil menebak dengan benar, maka ikan tersebut akan ditambahkan ke inventory
            // Lanjutkan world time
            // Action Fishing berakhir
            stopClock() ;
            player.setEnergy(player.getEnergy() - 5);
            advance(15) ;
            int fishType = randomizeFishType() ; // 1-3 (common, regular, legendary)
            if (fishType == 1) {
                int randomNumber = randomizeNumber(1, 10); //perlu rng
                boolean guessed = playerGuess(randomNumber, 10); //mekanisme guessing?
                if (guessed) {
                    add(Fish fish) ; 
                    System.out.println("You caught a common fish!");
                } else {
                    System.out.println("sad");
                }
            } 
            else if (fishType == 2) {
                // Regular fish
                int randomNumber = randomizeNumber(1, 100);
                boolean guessed = playerGuess(randomNumber, 10);
                if (guessed) {
                    add(Fish fish) ;
                    System.out.println("You caught a regular fish!");
                } else {
                    System.out.println("ouch.");
                }
            } 
            else if (fishType == 3) {
                // Legendary fish
                int randomNumber = randomizeNumber(1, 500);
                boolean guessed = playerGuess(randomNumber, 7);
                if (guessed) {
                    add(Fish fish) ;
                    System.out.println("You caught a legendary fish!");
                } else {
                    System.out.println("expected.");
                }
            }
        }
        else {
            System.out.println("You can't fish here.");
        }
    }

}