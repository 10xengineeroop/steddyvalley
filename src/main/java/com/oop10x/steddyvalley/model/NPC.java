package com.oop10x.steddyvalley.model;

import com.oop10x.steddyvalley.model.items.Crop;
import com.oop10x.steddyvalley.model.items.Equipment;
import com.oop10x.steddyvalley.model.items.Item;
import com.oop10x.steddyvalley.utils.RelStatus;
import com.oop10x.steddyvalley.model.items.Fish;
import com.oop10x.steddyvalley.model.items.Misc;
import com.oop10x.steddyvalley.model.items.Seed;
import com.oop10x.steddyvalley.model.items.Food;

import java.util.HashSet;
import java.util.Set;

public class NPC {
    private final String name;
    private int heartPoints;
    private final Set<Item> lovedItems;
    private final Set<Item> likedItems;
    private final Set<Item> hatedItems;
    private RelStatus relationshipStatus;
    private static Set<NPC> npcSet = new HashSet<>();

    private int chatCountWithPlayer = 0; 
    private int giftsReceivedCount = 0; 
    private int timesVisitedByPlayer = 0; 
    // CONSTRUCTOR
    public NPC(String name, Set<Item> lovedItems, Set<Item> likedItems, Set<Item> hatedItems) {
        this.name = name;
        this.heartPoints = 0;
        this.lovedItems = lovedItems;
        this.likedItems = likedItems;
        this.hatedItems = hatedItems;
        this.relationshipStatus = RelStatus.SINGLE;
        npcSet.add(this);
    }

    // GETTER DAN SETTER
    public String getName() {
        return name;
    }
    public int getHeartPoints() {
        return heartPoints;
    }
    public Set<Item> getLovedItems() {
        return lovedItems;
    }
    public Set<Item> getLikedItems() {
        return likedItems;
    }
    public Set<Item> getHatedItems() {
        return hatedItems;
    }
    public RelStatus getRelationshipStatus() {
        return relationshipStatus;
    }

    public void setRelationshipStatus(RelStatus relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public void setHeartPoints(int heartPoints) {
        this.heartPoints = heartPoints;
        if (this.heartPoints > 150) {
            this.heartPoints = 150; // Maksimal heart points
        } else if (this.heartPoints < 0) {
            this.heartPoints = 0; // Minimal heart points
        }
    }

    // LAINNYA
    public void getItem(Item item) {
        if (lovedItems.contains(item)) {
            setHeartPoints(getHeartPoints() + 25);
        } else if (likedItems.contains(item)) {
            setHeartPoints(getHeartPoints() + 20);
        } else if (hatedItems.contains(item)) {
            setHeartPoints(getHeartPoints() - 25);
        }

        if (getHeartPoints() > 150) {
            setHeartPoints(150); // Maksimal heart points
        } else if (getHeartPoints() < 0) {
            setHeartPoints(0); // Minimal heart points
        }
        this.giftsReceivedCount++;
    }

    public boolean propose(Player player) {
        if (player == null || player.getInventory().getItemByName("Proposal Ring") == null) {
            return false;
        } else if (heartPoints == 150 && (player.getRelationshipStatus().equals(RelStatus.SINGLE) || player.getRelationshipStatus().equals(RelStatus.FIANCE))) {
            if (relationshipStatus == RelStatus.SINGLE) {
                relationshipStatus = RelStatus.FIANCE;
                player.setRelationshipStatus(RelStatus.FIANCE);
                return true; // Proposal diterima
            } else {
                relationshipStatus = RelStatus.SPOUSE;
                player.setRelationshipStatus(RelStatus.SPOUSE);
                return true; // Pernikahan terjadi
            }
        } else {
            return false; // Heart points TIDAK cukup untuk menikah
        }
        
    }

    public void addNpc(NPC npc) {
        npcSet.add(npc);
    }

    public void removeNpc(NPC npc) {
        npcSet.remove(npc);
    }

    public void chat() {
        setHeartPoints(getHeartPoints() + 10);
        chatCountWithPlayer++;
    }

    public static Set<NPC> getNpcSet() {
        return npcSet;
    }

    public static NPC getNpcByName(String name) {
        for (NPC npc : npcSet) {
            if (npc.getName().equals(name)) {
                return npc;
            }
        }
        throw new NullPointerException("No NPC with name " + name);
    }
    static {
        Set<Item> hatedItems = new HashSet<>();
        hatedItems.addAll(Fish.getFishSet());
        hatedItems.addAll(Misc.getMiscset());
        hatedItems.addAll(Crop.getCropSet());
        hatedItems.addAll(Seed.getSeedSet());
        hatedItems.addAll(Food.getFoodSet());
        hatedItems.addAll(Equipment.getEquipmentSet());

        NPC mayted = new NPC("Mayor Tadi", Set.of(Fish.getFishbyName("Legend")), Set.of(Fish.getFishbyName("Angler"), Fish.getFishbyName("Crimsonfish"), Fish.getFishbyName("Glacierfish")), hatedItems);
        mayted.getHatedItems().removeAll(mayted.getLovedItems());
        mayted.getHatedItems().removeAll(mayted.getLikedItems());
        new NPC("Caroline", Set.of(Misc.getMisc("Firewood"), Misc.getMisc("Coal")), Set.of(Crop.getCropByName("Potato"), Crop.getCropByName("Wheat")), Set.of(Crop.getCropByName("Hot Pepper")));
        new NPC("Perry", Set.of(Crop.getCropByName("Cranberry"), Crop.getCropByName("Blueberry")), Set.of(Food.getFoodbyName("Wine")), new HashSet<>(Fish.getFishSet()));
        new NPC("Dasco",Set.of(Food.getFoodbyName("The Legends of Spakbor"), Food.getFoodbyName("Cooked Pig's Head"), Food.getFoodbyName("Wine"), Food.getFoodbyName("Fugu"), Food.getFoodbyName("Spakbor Salad")),Set.of(Food.getFoodbyName("Fish Sandwich"), Food.getFoodbyName("Fish Stew"), Food.getFoodbyName("Baguette"), Food.getFoodbyName("Fish n' Chips")),Set.of(Fish.getFishbyName("Legend"), Crop.getCropByName("Grape"), Crop.getCropByName("Cauliflower"), Crop.getCropByName("Wheat"), Fish.getFishbyName("Pufferfish"), Fish.getFishbyName("Salmon")));
        new NPC("Emily", new HashSet<>(Seed.getSeedSet()), Set.of(Fish.getFishbyName("Catfish"), Fish.getFishbyName("Salmon"), Fish.getFishbyName("Sardine")), Set.of(Misc.getMisc("Coal"), Misc.getMisc("Firewood")));
        new NPC("Abigail", Set.of(Crop.getCropByName("Blueberry"), Crop.getCropByName("Melon"), Crop.getCropByName("Pumpkin"), Crop.getCropByName("Grape"), Crop.getCropByName("Cranberry")), Set.of(Food.getFoodbyName("Baguette"), Food.getFoodbyName("Pumpkin Pie"), Food.getFoodbyName("Wine")), Set.of(Crop.getCropByName("Hot Pepper"), Crop.getCropByName("Cauliflower"), Crop.getCropByName("Parsnip"), Crop.getCropByName("Wheat")));
        new NPC("LittleLucy", new HashSet<>(Food.getFoodSet()), Set.of(), Set.of());
    }
    public int getChatCountWithPlayer() { return chatCountWithPlayer; }
    public int getGiftsReceivedCount() { return giftsReceivedCount; }
    public int getTimesVisitedByPlayer() { return timesVisitedByPlayer; }
    public void recordPlayerVisit() {
        timesVisitedByPlayer++;
    }


}
