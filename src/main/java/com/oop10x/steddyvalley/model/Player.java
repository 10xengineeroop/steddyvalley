package com.oop10x.steddyvalley.model;

import com.oop10x.steddyvalley.model.items.*;
import com.oop10x.steddyvalley.utils.FishRarity;
import com.oop10x.steddyvalley.utils.Position;
import com.oop10x.steddyvalley.utils.RelStatus;

import java.util.*;


public class Player {
    private int gold;
    private int energy;
    private final Position position;
    private Item equippedItem;
    private final Inventory inventory = new Inventory();
    private int currentTime;
    private int speed;
    private RelStatus relationshipStatus = RelStatus.SINGLE;
    private String name = "Steddy";
    private String gender = "Helikopter";
    private String favoriteItem = "Anything!";
    private String farmName = "Steddy Farm";
    private NPC partnerNPC = null;

    // End Game Statistics
    private static int totalIncome = 0;
    private static int totalExpenditure = 0;
    private static int totalCropsHarvested = 0;
    private static int totalFishCaught = 0;
    private static Map<FishRarity, Integer> fishCaughtByRarity = new EnumMap<>(FishRarity.class);

    public String getName() {
        return name; 
    }
    public void setName(String name) { 
        this.name = name;
    }

    public String getFarmName(String name){
        return this.farmName;
    }

    public void setFarmName(String name){
        this.farmName = farmName;
    }

    public String getGender() { 
        return gender; 
    }
    public void setGender(String gender) { 
        this.gender = gender; notifyObservers(); 
    }

    public String getFavoriteItem() { 
        return favoriteItem; 
    }
    public void setFavoriteItem(String favoriteItem) { 
        this.favoriteItem = favoriteItem; notifyObservers(); 
    }

    public NPC getPartnerNPC() { 
        return partnerNPC; 
    }
    public void setPartnerNPC(NPC partnerNPC) {
        this.partnerNPC = partnerNPC;
        if (partnerNPC != null) {
            if (getRelationshipStatus().equals(RelStatus.FIANCE)) {
                this.relationshipStatus = RelStatus.FIANCE;
            }
            else {
                this.relationshipStatus = RelStatus.SPOUSE;
            }
        }
        notifyObservers();
    }

    public static int getTotalIncome() {
        return totalIncome;
    }
    public static void setTotalIncome(int totalIncome) {
        Player.totalIncome = totalIncome;
    }
    public static int getTotalExpenditure() {
        return totalExpenditure;
    }
    public static void setTotalExpenditure(int totalExpenditure) {
        Player.totalExpenditure = totalExpenditure;
    }

    public static int getIncomePerSeason() {
        return getTotalIncome() / SeasonManager.getInstance(TimeManager.getInstance()).getSeasonsPassed();
    }
    public static int getExpenditurePerSeason() {
        return getTotalExpenditure() / SeasonManager.getInstance(TimeManager.getInstance()).getSeasonsPassed();
    }

    public static int getTotalCropsHarvested() {
        return totalCropsHarvested;
    }
    public static void setTotalCropsHarvested(int totalCropsHarvested) {
        Player.totalCropsHarvested = totalCropsHarvested;
    }
    public static int getTotalFishCaught() {
        return totalFishCaught;
    }
    public static void setTotalFishCaught(int totalFishCaught) {
        Player.totalFishCaught = totalFishCaught;
    }
     public static void incrementFishCaughtByRarity(FishRarity rarity) {
        fishCaughtByRarity.put(rarity, fishCaughtByRarity.getOrDefault(rarity, 0) + 1);
        System.out.println("[Player DEBUG] Incremented " + rarity + " fish. New count: " + fishCaughtByRarity.get(rarity));
    }

    public static Map<FishRarity, Integer> getFishCaughtByRarity() {
        return Collections.unmodifiableMap(fishCaughtByRarity);
    }

    public RelStatus getRelationshipStatus() {
        return relationshipStatus;
    }
    public void setRelationshipStatus(RelStatus relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
        notifyObservers();
    }
    public static int getTotalDaysPlayed() {
        return TimeManager.getInstance().getTotalDaysPlayed();
    }

    public static List<String> getNPCsRelationshipDetails() {
        List<String> details = new ArrayList<>();
        Set<NPC> npcSet = NPC.getNpcSet(); 

        if (npcSet == null || npcSet.isEmpty()) {
            details.add("No NPC data available.");
            return details;
        }
        for (NPC npc : npcSet) {
            details.add(String.format("%s: %s (%d Hearts)",
                    npc.getName(),
                    npc.getRelationshipStatus().toString(), 
                    npc.getHeartPoints()));
        }
        return details;
    }
    public static List<String> getNPCChattingFrequencyDetails() {
        List<String> details = new ArrayList<>();
        Set<NPC> npcSet = NPC.getNpcSet();
        if (npcSet == null || npcSet.isEmpty()) {
            details.add("No NPC chat data available.");
            return details;
        }
        for (NPC npc : npcSet) {
            details.add(String.format("%s: Chatted %d times", npc.getName(), npc.getChatCountWithPlayer()));
        }
        return details;
    }
    public static List<String> getNPCGiftingFrequencyDetails() {
        List<String> details = new ArrayList<>();
        Set<NPC> npcSet = NPC.getNpcSet();
        if (npcSet == null || npcSet.isEmpty()) {
            details.add("No NPC gifting data available.");
            return details;
        }
        for (NPC npc : npcSet) {
            details.add(String.format("%s: Received %d gifts", npc.getName(), npc.getGiftsReceivedCount()));
        }
        return details;
    }

    public static List<String> getNPCVisitingFrequencyDetails() {
        List<String> details = new ArrayList<>();
        Set<NPC> npcSet = NPC.getNpcSet();
        if (npcSet == null || npcSet.isEmpty()) {
            details.add("No NPC visiting data available.");
            return details;
        }
        for (NPC npc : npcSet) {
            details.add(String.format("%s: Visited by player %d times", npc.getName(), npc.getTimesVisitedByPlayer()));
        }
        return details;
    }

    private final transient List<PlayerObserver> observers = new ArrayList<>();

    public Player(int x, int y, int gold, int energy, int speed) {
        this.position = new Position(x,y);
        this.gold = gold;
        this.energy = energy;
        this.currentTime = 0;
        this.speed = speed;
        Set<Equipment> initialEquipment = Equipment.getEquipmentSet();
        initialEquipment.forEach(equipment -> addItem(equipment));
        addItem(Seed.getSeedByName("Parsnip Seeds"), 15);
        setEquippedItem(initialEquipment.stream()
                .filter(equipment -> equipment.getName().equals("Hoe"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No Hoe equipment found")));
    }
    public void addObserver(PlayerObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }
    public void removeObserver(PlayerObserver observer) {
        observers.remove(observer);
    }
    private void notifyObservers() {
        List<PlayerObserver> observersCopy = new ArrayList<>(observers);
        for (PlayerObserver observer : observersCopy) {
            observer.onPlayerUpdated(this);
        }
    }
    public int getGold() {
        return gold;
    }
    public int getSpeed() {
        return speed;
    }

    public void setGold(int gold) {
        
        if (gold > this.gold) {
            totalIncome += (gold - this.gold);
        } else if (gold < this.gold) {
            totalExpenditure += (this.gold - gold);
        }
        this.gold = gold;
        notifyObservers();
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
        if (this.energy < -20) {
            this.energy = -20;
        } else if (this.energy > 100) {
            this.energy = 100;
        }
        notifyObservers();
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(int x, int y) {
        boolean positionChanged = false;
        if (this.position.getX() != x) {
            this.position.setX(x);
            positionChanged = true;
        }
        if (this.position.getY() != y) {
            this.position.setY(y);
            positionChanged = true;
        }
        if (positionChanged) {
            notifyObservers();
        }
    }
    public void move(int deltaX, int deltaY) {
        if (deltaX != 0 || deltaY != 0) {
            this.position.setX(this.position.getX() + deltaX);
            this.position.setY(this.position.getY() + deltaY);
            // Mungkin kurangi energi di sini juga jika bergerak mengurangi energi
            // setEnergy(this.energy - 1); // Ini akan memanggil notifyObservers() juga
            notifyObservers();
        }
    }

    public Item getEquippedItem() {
        return equippedItem;
    }

    public void setEquippedItem(Item equippedItem) {
        this.equippedItem = equippedItem;
        notifyObservers();
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
        notifyObservers();
    }

    public void addItem(Item item) {
        inventory.addItem(item);
    }

    public void addItem(Item item, Integer amount) {
        inventory.addItem(item, amount);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void eat(Item item) {
        if (item != null) {
            if (item instanceof Food food) {
                setEnergy(getEnergy() + food.getEnergy());
            } else if (item instanceof Fish) {
                setEnergy(getEnergy() + 1);
            } else if (item instanceof Crop) {
                setEnergy(getEnergy() + 3);
            }
        }
    }
}
