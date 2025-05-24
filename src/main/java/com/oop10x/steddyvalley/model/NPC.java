package com.oop10x.steddyvalley.model;

import com.oop10x.steddyvalley.model.items.Item;
import com.oop10x.steddyvalley.model.map.Actionable;
import com.oop10x.steddyvalley.utils.RelStatus;

import java.util.HashSet;
import java.util.Set;

public class NPC implements Actionable {
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
    }


    @Override
    public void onPlayerAction(Player player) {
        // TODO atur ini
    }

    public void addNpc(NPC npc) {
        npcSet.add(npc);
    }

    public void removeNpc(NPC npc) {
        npcSet.remove(npc);
    }
}
