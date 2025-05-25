package com.oop10x.steddyvalley.model.items;

import com.oop10x.steddyvalley.utils.FishRarity;
import com.oop10x.steddyvalley.utils.Season;
import com.oop10x.steddyvalley.utils.TimeRange;
import com.oop10x.steddyvalley.utils.Weather;

import java.util.Set;

public class FishStarter {

    public static void start() {
        //    COMMON FISH
        new Fish(
                "Bullhead",
                FishRarity.COMMON,
                Set.of(Season.SPRING,Season.SUMMER,Season.AUTUMN,Season.WINTER),
                Set.of(new TimeRange(0,24*60)),
                Set.of(Weather.RAINY,Weather.SUNNY),
                Set.of("Mountain Lake")
        );
        new Fish(
                "Carp",
                FishRarity.COMMON,
                Set.of(Season.SPRING,Season.SUMMER,Season.AUTUMN,Season.WINTER),
                Set.of(new TimeRange(0,24*60)),
                Set.of(Weather.RAINY,Weather.SUNNY),
                Set.of("Mountain Lake", "Pond")
        );
        new Fish(
                "Chub",
                FishRarity.COMMON,
                Set.of(Season.SPRING,Season.SUMMER,Season.AUTUMN,Season.WINTER),
                Set.of(new TimeRange(0,24*60)),
                Set.of(Weather.RAINY,Weather.SUNNY),
                Set.of("Forest River", "Mountain Lake")
        );

    //    REGULAR FISH
        new Fish(
                "Largemouth Bass",
                FishRarity.REGULAR,
                Set.of(Season.SPRING,Season.SUMMER,Season.AUTUMN,Season.WINTER),
                Set.of(new TimeRange(6 * 60, 18 * 60)),
                Set.of(Weather.RAINY,Weather.SUNNY),
                Set.of("Mountain Lake")
        );
        new Fish(
                "Rainbow Trout",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER),
                Set.of(new TimeRange(6 * 60, 18 * 60)),
                Set.of(Weather.SUNNY),
                Set.of("Forest River", "Mountain Lake")
        );
        new Fish(
                "Sturgeon",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER, Season.WINTER),
                Set.of(new TimeRange(6 * 60, 18 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Mountain Lake")
        );
        new Fish(
                "Midnight Carp",
                FishRarity.REGULAR,
                Set.of(Season.WINTER, Season.AUTUMN),
                Set.of(new TimeRange(0, 2 * 60), new TimeRange(20 * 60, 24 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Mountain Lake", "Pond")
        );
        new Fish(
                "Flounder",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER, Season.SPRING),
                Set.of(new TimeRange(6 * 60, 22 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Ocean")
        );
        new Fish(
                "Halibut",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER, Season.WINTER, Season.AUTUMN, Season.SPRING),
                Set.of(new TimeRange(6 * 60, 11 * 60), new TimeRange(19 * 60, 24 * 60), new TimeRange(0, 2 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Ocean")
        );
        new Fish(
                "Octopus",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER),
                Set.of(new TimeRange(6 * 60, 22 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Ocean")
        );
        new Fish(
                "Pufferfish",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER),
                Set.of(new TimeRange(0, 16 * 60)),
                Set.of(Weather.SUNNY),
                Set.of("Ocean")
        );
        new Fish(
                "Sardine",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER, Season.SPRING, Season.WINTER, Season.AUTUMN),
                Set.of(new TimeRange(6 * 60, 18 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Ocean")
        );
        new Fish(
                "Super Cucumber",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER, Season.WINTER, Season.AUTUMN),
                Set.of(new TimeRange(18 * 60, 24 * 60), new TimeRange(0, 2 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Ocean")
        );
        new Fish(
                "Catfish",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER, Season.SPRING, Season.AUTUMN),
                Set.of(new TimeRange(6 * 60, 22 * 60)),
                Set.of(Weather.RAINY),
                Set.of("Forest River", "Pond")
        );
        new Fish(
                "Salmon",
                FishRarity.REGULAR,
                Set.of(Season.AUTUMN),
                Set.of(new TimeRange(6 * 60, 18 * 60)),
                Set.of(Weather.RAINY, Weather.SUNNY),
                Set.of("Forest River")
        );

        //    LEGENDARY FISH
        new Fish(
                "Angler",
                FishRarity.LEGENDARY,
                Set.of(Season.AUTUMN),
                Set.of(new TimeRange(8 * 60, 20 * 60)),
                Set.of(Weather.RAINY, Weather.SUNNY),
                Set.of("Pond")
        );
        new Fish(
                "Crimsonfish",
                FishRarity.LEGENDARY,
                Set.of(Season.SUMMER),
                Set.of(new TimeRange(8 * 60, 20 * 60)),
                Set.of(Weather.RAINY, Weather.SUNNY),
                Set.of("Ocean")
        );
        new Fish(
                "Glacier Fish",
                FishRarity.LEGENDARY,
                Set.of(Season.WINTER),
                Set.of(new TimeRange(8 * 60, 20 * 60)),
                Set.of(Weather.RAINY, Weather.SUNNY),
                Set.of("Forest River")
        );
        new Fish(
                "Legend",
                FishRarity.LEGENDARY,
                Set.of(Season.SPRING),
                Set.of(new TimeRange(8 * 60, 20 * 60)),
                Set.of(Weather.RAINY),
                Set.of("Mountain Lake")
        );
    }
}
