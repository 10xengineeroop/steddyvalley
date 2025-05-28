package com.oop10x.steddyvalley.model.items;

import java.util.HashSet;
import java.util.Set;

public class Equipment extends Item {
    private static final Set<Equipment> equipmentSet = new HashSet<>();

    public Equipment(String name) {
        super(name,null, null);
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

    static {
        new Equipment("Hoe");
        new Equipment("Watering Can");
        new Equipment("Pickaxe");
        new Equipment("Fishing Rod");
    }
}
