package com.oop10x.steddyvalley.model.items;

import com.oop10x.steddyvalley.utils.Season;

import java.util.Set;

public class SeedStarter {
    public static void start() {
        Seed parsnipSeed = new Seed(
                "Parsnip Seeds",
                20,
                Set.of(Season.SPRING),
                1,
                "Parsnip"
        );
        Seed cauliflowerSeed = new Seed(
                "Cauliflower Seeds",
                80,
                Set.of(Season.SPRING),
                5,
                "Cauliflower"
        );
        Seed potatoSeed = new Seed(
                "Potato Seeds",
                50,
                Set.of(Season.SPRING),
                3,
                "Potato"
        );
        Seed wheatSeed = new Seed(
                "Wheat Seeds",
                60,
                Set.of(Season.SPRING, Season.AUTUMN),
                1,
                "Wheat"
        );
        Seed blueberrySeed = new Seed(
                "Blueberry Seeds",
                80,
                Set.of(Season.SUMMER),
                7,
                "Blueberry"
        );
        Seed tomatoSeed = new Seed(
                "Tomato Seeds",
                50,
                Set.of(Season.SUMMER),
                3,
                "Tomato"
        );
        Seed hotPepperSeed = new Seed(
                "Hot Pepper Seeds",
                40,
                Set.of(Season.SUMMER),
                1,
                "Hot Pepper"
        );
        Seed melonSeed = new Seed(
                "Melon Seeds",
                80,
                Set.of(Season.SUMMER),
                4,
                "Melon"
        );
        Seed cranberrySeed = new Seed(
                "Cranberry Seeds",
                100,
                Set.of(Season.AUTUMN),
                2,
                "Cranberry"
        );
        Seed pumpkinSeed = new Seed(
                "Pumpkin Seeds",
                150,
                Set.of(Season.AUTUMN),
                7,
                "Pumpkin"
        );
        Seed grapeSeed = new Seed(
                "Grape Seeds",
                60,
                Set.of(Season.AUTUMN),
                3,
                "Grape"
        );
    }
}
