package com.oop10x.steddyvalley.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.oop10x.steddyvalley.model.items.*;

public class StoreTest {
  private static Store store;
  private static Player player = new Player(0, 0, 100, 100, 5);


  @BeforeAll 
  static void init() {
    store = Store.getInstance();
    assertNotEquals(null, store, "Store instance should not be null");
    assertNotEquals(null, player, "Player instance should not be null");
  }

  @BeforeEach
  void setUp() {
    assertNotEquals(null, store, "Store instance should not be null");
    player.setGold(1000);
    assertEquals(1000, player.getGold(), "Initial money should be 1000");
  }

  @Test
  void testStoreNotNull() {
    assertNotEquals(null, store.getItems(), "Store items should not be null");
    assertNotEquals(0, store.getItems().size(), "Store should have items");
  }

  @Test
  void testNoNullPrices() {
    List<Item> items = store.getItems();
    assertNotEquals(null, items);
    for (Item item : items) {
      assertNotEquals(null, item.getBuyPrice(), "Buy price should not be null for item: " + item.getName());
    }
  }

  @Test
  void testBuyString() {
    Item grape = Crop.getCropByName("Grape");
    assertNotEquals(null, grape, "Grape should not be null");
    store.buyItem(player, "Grape", 3);
    assertEquals(1000 - (grape.getBuyPrice() * 3), player.getGold(), "Player's gold should be reduced by the total price of the items bought");
    assertEquals(3, player.getInventory().countItem(grape), "Player should have 3 grapes in inventory");
  }

  @Test
  void testBuyCukup() {
    Item wine = Food.getFoodbyName("Wine");
    assertNotEquals(null, wine);
    store.buyItem(player, wine, 3);
    assertEquals(1000 - (wine.getBuyPrice() * 3), player.getGold(), "Player's gold should be reduced by the total price of the items bought");
    assertEquals(3, player.getInventory().countItem(wine), "Player should have 3 Wine in inventory");
  }

  @Test
  void testBuyKurang() {
    Item sashimi = Food.getFoodbyName("Sashimi");
    store.buyItem(player, sashimi, 4);
    assertEquals(1000, player.getGold(), "Player's gold should remain the same if not enough money");
    assertEquals(0, player.getInventory().countItem(sashimi), "Player should have 0 Sashimi in inventory if not enough money");
  }

  @Test
  void testBuyGaadaItem() {
    store.buyItem(player, new Item("NonExistentItem", 100), 1);
    assertEquals(1000, player.getGold(), "Player's gold should remain the same if item does not exist");
    assertEquals(0, player.getInventory().countItem("NonExistentItem"), "Player should have 0 NonExistentItem in inventory if item does not exist");
    store.buyItem(player, (String) null, 1);
    assertEquals(1000, player.getGold(), "Player's gold should remain the same if item is null");
    assertEquals(0, player.getInventory().countItem((Item) null), "Player should have 0 NonExistentItem in inventory if item is null");
  }

  @Test
  void testBuyKurangQuantity() {
    Item sashimi = Food.getFoodbyName("Sashimi");
    store.buyItem(player, sashimi, -1);
    assertEquals(1000, player.getGold(), "Player's gold should remain the same if quantity is negative");
    assertEquals(0, player.getInventory().countItem(sashimi), "Player should have 0 Sashimi in inventory if quantity is negative");
  }

  @Test
  void testGakAdaEquipment() {
    Set<Equipment> items = Equipment.getEquipmentSet();
    items.forEach(nullItem -> {
      store.buyItem(player, nullItem, 1);
      assertEquals(1000, player.getGold(), "Player's gold should remain the same if item is null");
      assertEquals(1, player.getInventory().countItem(nullItem), "Player should have 0 " + nullItem.getName() + " in inventory if item is null");
    });
  }
}
