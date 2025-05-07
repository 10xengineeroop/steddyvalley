package com.oop10x.steddyvalley.model.items;

import java.util.HashSet;
import java.util.Set;

public class Crop extends Item {
    private final Integer sellPrice;
    private final Integer cropPerHarvest;
    private static final Set<Crop> cropSet = new HashSet<>();
    public Crop(String name, Integer buyPrice, Integer sellPrice, Integer cropPerHarvest) {
        super(name,buyPrice);
        this.sellPrice = sellPrice;
        this.cropPerHarvest = cropPerHarvest;
        cropSet.add(this);
    }

    public static Crop getCropByName(String name){
        for(Crop crop : cropSet){
            if(crop.getName().equals(name)){
                return crop;
            }
        }
        throw new IllegalArgumentException("Crop not found");
    }

    public Integer getSellPrice() {
        return sellPrice;
    }

    public Integer getCropPerHarvest() {
        return cropPerHarvest;
    }

    public static void addCrop(Crop crop){
        cropSet.add(crop);
    }

    public static void removeCrop(Crop crop){
        cropSet.remove(crop);
    }
}
