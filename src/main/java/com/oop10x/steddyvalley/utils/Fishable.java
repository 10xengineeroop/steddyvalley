package com.oop10x.steddyvalley.utils;
import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.TimeManager;
import com.oop10x.steddyvalley.model.items.Fish;
import com.oop10x.steddyvalley.model.locations.Location;

import java.util.*;

public interface Fishable{

    default void fish(Player player, Weather weather, Season season, Location location) {
        // GIMANA CARA HUBUNGIN INI DENGAN UI NANTI
        // STEP 0. NULL CHECKING DAN PRASYARAT
        if (player == null || weather == null || season == null || location == null) throw new NullPointerException();
        if (!isFishable()) return;
        if (!player.getEquippedItem().getName().equals("Fishing Rod") || !(player.getEnergy() >= 5)) return;

        // STEP 1. Ambil ikan yang available
        Set<Fish> fishes = Fish.getFishSet();
        List<Fish> fishableFish = new ArrayList<>();
        for (Fish fish : fishes) {
            if (fish.isInWeather(weather) && fish.isInSeason(season) && fish.isInLocation(location)) {
                fishableFish.add(fish);
            }
        }

        // STEP 2. Advance waktu dan kurangi energy player
        player.setEnergy(player.getEnergy() - 5);
        TimeManager time = TimeManager.getInstance();
        time.setMinutes(time.getMinutes() + 15);

        // STEP 3. RANDOMIZE IKAN
        Random r = new Random();
        Collections.shuffle(fishableFish, r);
        Fish f = fishableFish.getFirst();
        FishRarity fr = f.getRarity();

        // STEP 4. GAME SETTING
        int maxtries;
        int upperbound;
        if (fr.equals(FishRarity.COMMON)) {
            maxtries = 10; upperbound = 10;
        } else if (fr.equals(FishRarity.REGULAR)){
            maxtries = 10; upperbound = 100;
        } else {
            maxtries = 7; upperbound = 500;
        }

        // STEP 5. GUESSING GAME


    }
    /**
     * @return apakah bisa dipancing (simpen variabel boolean)
     * karena store itu location dan location implement fishable
     * set store return false soalnya dia ga bisa pancing di situ
     */
    boolean isFishable();
}
