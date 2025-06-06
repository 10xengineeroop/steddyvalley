package com.oop10x.steddyvalley.model.items;

import java.util.HashSet;
import java.util.Set;

public class Misc extends Item {
    private static final Set<Misc> miscSet = new HashSet<>();

    public static Set<Misc> getMiscset() {
        return miscSet;
    }

    public Misc(String name, Integer sellPrice, Integer buyPrice) {
        super(name,buyPrice, sellPrice);
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

    static {
        new Misc("Coal", 50,100);
        new Misc("Firewood", 75,150);
        new Misc("Proposal Ring", null, 3000);
    }
}
