package com.oop10x.steddyvalley.model.items;

import java.util.HashSet;
import java.util.Set;

public class Equipment extends Item {
    private static final Set<Equipment> equipmentSet = new HashSet<>();
    static {
        Equipment hoe = new Equipment("Hoe");
        Equipment wateringCan = new Equipment("Watering Can");
        Equipment pickaxe = new Equipment("Pickaxe");
        Equipment fishingRod = new Equipment("Fishing Rod");
    }

    public Equipment(String name) {
        super(name,null);
        equipmentSet.add(this);
    }

    public Equipment getEquipment(String name) {
        for (Equipment equipment : equipmentSet) {
            if (equipment.getName().equals(name)) {
                return equipment;
            }
        }
        throw new IllegalArgumentException("No equipment with name " + name);
    }
}
