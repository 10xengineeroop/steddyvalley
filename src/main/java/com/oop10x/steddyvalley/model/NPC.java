package com.oop10x.steddyvalley.model;

import com.oop10x.steddyvalley.model.items.Item;
import com.oop10x.steddyvalley.utils.RelStatus;

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

        if (item.getName().equals("Proposal Ring")) {
            propose(item.getOwner()); // Cek apakah item adalah Proposal Ring
        }
    }

    public boolean propose(Player player) {
        // Cek apakah player memiliki Proposal Ring
        if (player == null || player.getInventory().getItemByName("Proposal Ring") != null) {
            return false; // Tidak ada Proposal Ring
        } else if (heartPoints == 150 && (player.getRelationshipStatus().equals(RelStatus.SINGLE) || player.getRelationshipStatus().equals(RelStatus.FIANCE))) {
            // Jika heart points cukup dan status hubungan player adalah SINGLE
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
            return false; // Heart points tidak cukup untuk menikah
        }
        
    }

    public void addNpc(NPC npc) {
        npcSet.add(npc);
    }

    public void removeNpc(NPC npc) {
        npcSet.remove(npc);
    }
}
