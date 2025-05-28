package com.oop10x.steddyvalley.model;

import java.util.*;

import com.oop10x.steddyvalley.model.items.*;

public class Store {
  private static final Store instance;
  private List<Item> items = new ArrayList<>();

  static {
    instance = new Store();
  }
  private Store() {
    // Private constructor to prevent instantiation
    items.addAll(Seed.getSeedSet());
    items.addAll(Misc.getMiscset());
    items.addAll(Crop.getCropSet());
    items.addAll(Food.getFoodSet());
    items.removeIf(item -> item.getBuyPrice() == null);
  }
  public static Store getInstance() {
    return instance;
  }

  public List<Item> getItems() {
    return items;
  }

  public void buyItem(Player player, String itemName, Integer quantity) {
    if (player == null || itemName == null || quantity <= 0 || quantity == null) {
      return;
    }
    items.stream()
        .filter(item -> item.getName().equalsIgnoreCase(itemName))
        .findFirst()
        .ifPresent(item -> buyItem(player, item, quantity)); 
  }

  public void buyItem(Player player, Item item, Integer quantity) {
    if (player == null || item == null || quantity <= 0 || !items.contains(item)) {
      return;
    }
    int totalPrice = items.stream()
        .filter(i -> i.equals(item))
        .findFirst()
        .map(i -> i.getBuyPrice() * quantity)
        .orElse(0);
    if (player.getGold() >= totalPrice) {
      player.setGold(player.getGold() - totalPrice);
      player.getInventory().addItem(item, quantity);
    } else {
      System.out.println("Not enough gold to buy " + item.getName());
    }
  }
}