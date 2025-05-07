package com.oop10x.steddyvalley.model.items;

public class CropStarter {
    public static void start() {
        Crop parsnip = new Crop(
                "Parsnip",
                50,
                35,
                1
        );
        Crop cauliflower = new Crop(
                "Cauliflower",
                200,
                150,
                1
        );
        Crop potato = new Crop(
                "Potato",
                null,
                80,
                1
        );
        Crop wheat = new Crop(
                "Wheat",
                50,
                30,
                3
        );
        Crop blueberry = new Crop(
                "Blueberry",
                150,
                40,
                3
        );
        Crop tomato = new Crop(
                "Tomato",
                90,
                60,
                1
        );
        Crop hotPepper = new Crop(
                "Hot Pepper",
                null,
                40,
                1
        );
        Crop melon = new Crop(
                "Melon",
                null,
                250,
                1
        );
        Crop cranberry = new Crop(
                "Cranberry",
                null,
                25,
                10
        );
        Crop pumpkin = new Crop(
                "Pumpkin",
                300,
                250,
                1
        );
        Crop grape = new Crop(
                "Grape",
                100,
                10,
                20
        );
    }
}
