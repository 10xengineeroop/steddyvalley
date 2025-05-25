package com.oop10x.steddyvalley.model.items;

import java.util.HashSet;
import java.util.Set;

public class Misc extends Item {
    private static final Set<Misc> miscSet = new HashSet<>();
    static {
        Misc coal = new Misc("Coal", 50,100);
        Misc firewood = new Misc("Firewood", 75,150);
    }
    private final Integer sellPrice;
    public Misc(String name, Integer sellPrice, Integer buyPrice) {
        super(name,buyPrice);
        this.sellPrice = sellPrice;
        miscSet.add(this);
    }

    public static Misc getMisc(String name) {
        for (Misc misc : miscSet) {
            if (misc.getName().equals(name)) {
                return misc;
            }
        }
        throw new IllegalArgumentException("Misc not found");
    }
}
