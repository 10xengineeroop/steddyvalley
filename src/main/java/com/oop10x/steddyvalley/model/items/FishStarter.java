package com.oop10x.steddyvalley.model.items;

import com.oop10x.steddyvalley.utils.FishRarity;
import com.oop10x.steddyvalley.utils.Season;
import com.oop10x.steddyvalley.utils.TimeRange;
import com.oop10x.steddyvalley.utils.Weather;

import java.util.Set;

public class FishStarter {

    public void start() {
        //    COMMON FISH
        Fish bullhead = new Fish(
                "Bullhead",
                FishRarity.COMMON,
                Set.of(Season.SPRING,Season.SUMMER,Season.AUTUMN,Season.WINTER),
                Set.of(new TimeRange(0,24*60)),
                Set.of(Weather.RAINY,Weather.SUNNY),
                Set.of("Mountain Lake")
        );
        Fish carp = new Fish(
                "Carp",
                FishRarity.COMMON,
                Set.of(Season.SPRING,Season.SUMMER,Season.AUTUMN,Season.WINTER),
                Set.of(new TimeRange(0,24*60)),
                Set.of(Weather.RAINY,Weather.SUNNY),
                Set.of("Mountain Lake", "Pond")
        );
        Fish chub = new Fish(
                "Chub",
                FishRarity.COMMON,
                Set.of(Season.SPRING,Season.SUMMER,Season.AUTUMN,Season.WINTER),
                Set.of(new TimeRange(0,24*60)),
                Set.of(Weather.RAINY,Weather.SUNNY),
                Set.of("Forest River", "Mountain Lake")
        );

    //    REGULAR FISH
        Fish largemouth_bass = new Fish(
                "Largemouth Bass",
                FishRarity.REGULAR,
                Set.of(Season.SPRING,Season.SUMMER,Season.AUTUMN,Season.WINTER),
                Set.of(new TimeRange(6 * 60, 18 * 60)),
                Set.of(Weather.RAINY,Weather.SUNNY),
                Set.of("Mountain Lake")
        );
        Fish rainbow_trout = new Fish(
                "Rainbow Trout",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER),
                Set.of(new TimeRange(6 * 60, 18 * 60)),
                Set.of(Weather.SUNNY),
                Set.of("Forest River", "Mountain Lake")
        );
        Fish sturgeon = new Fish(
                "Sturgeon",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER, Season.WINTER),
                Set.of(new TimeRange(6 * 60, 18 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Mountain Lake")
        );
        Fish midnightCarp = new Fish(
                "Midnight Carp",
                FishRarity.REGULAR,
                Set.of(Season.WINTER, Season.AUTUMN),
                Set.of(new TimeRange(0, 2 * 60), new TimeRange(20 * 60, 24 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Mountain Lake", "Pond")
        );
        Fish flounder = new Fish(
                "Flounder",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER, Season.SPRING),
                Set.of(new TimeRange(6 * 60, 22 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Ocean")
        );
        Fish halibut = new Fish(
                "Halibut",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER, Season.WINTER, Season.AUTUMN, Season.SPRING),
                Set.of(new TimeRange(6 * 60, 11 * 60), new TimeRange(19 * 60, 24 * 60), new TimeRange(0, 2 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Ocean")
        );
        Fish octopus = new Fish(
                "Octopus",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER),
                Set.of(new TimeRange(6 * 60, 22 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Ocean")
        );
        Fish pufferfish = new Fish(
                "Pufferfish",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER),
                Set.of(new TimeRange(0, 16 * 60)),
                Set.of(Weather.SUNNY),
                Set.of("Ocean")
        );
        Fish sardine = new Fish(
                "Sardine",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER, Season.SPRING, Season.WINTER, Season.AUTUMN),
                Set.of(new TimeRange(6 * 60, 18 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Ocean")
        );
        Fish superCucumber = new Fish(
                "Super Cucumber",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER, Season.WINTER, Season.AUTUMN),
                Set.of(new TimeRange(18 * 60, 24 * 60), new TimeRange(0, 2 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Ocean")
        );
        Fish catfish = new Fish(
                "Catfish",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER, Season.SPRING, Season.AUTUMN),
                Set.of(new TimeRange(6 * 60, 22 * 60)),
                Set.of(Weather.RAINY),
                Set.of("Forest River", "Pond")
        );
        Fish salmon = new Fish(
                "Salmon",
                FishRarity.REGULAR,
                Set.of(Season.AUTUMN),
                Set.of(new TimeRange(6 * 60, 18 * 60)),
                Set.of(Weather.RAINY, Weather.SUNNY),
                Set.of("Forest River")
        );

        //    LEGENDARY FISH
        Fish angler = new Fish(
                "Angler",
                FishRarity.LEGENDARY,
                Set.of(Season.AUTUMN),
                Set.of(new TimeRange(8 * 60, 20 * 60)),
                Set.of(Weather.RAINY, Weather.SUNNY),
                Set.of("Pond")
        );
        Fish crimsonFish = new Fish(
                "Crimson Fish",
                FishRarity.LEGENDARY,
                Set.of(Season.SUMMER),
                Set.of(new TimeRange(8 * 60, 20 * 60)),
                Set.of(Weather.RAINY, Weather.SUNNY),
                Set.of("Ocean")
        );
        Fish glacierFish = new Fish(
                "Glacier Fish",
                FishRarity.LEGENDARY,
                Set.of(Season.WINTER),
                Set.of(new TimeRange(8 * 60, 20 * 60)),
                Set.of(Weather.RAINY, Weather.SUNNY),
                Set.of("Forest River")
        );
        Fish legend = new Fish(
                "Legend",
                FishRarity.LEGENDARY,
                Set.of(Season.SPRING),
                Set.of(new TimeRange(8 * 60, 20 * 60)),
                Set.of(Weather.RAINY),
                Set.of("Mountain Lake")
        );
    }
}
