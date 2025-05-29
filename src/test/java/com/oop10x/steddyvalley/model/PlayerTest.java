package com.oop10x.steddyvalley.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.oop10x.steddyvalley.model.items.*;

public class PlayerTest {
  private Player player = new Player(0, 0, 0, 100, 0);

  @Test
  void testEatNull() {
    player.eat(null);
    assertEquals(0, player.getEnergy(), "Energy should remain 0 after eating null");
  }

  @Test
  void testEatFood() {
    Food fishNChips = Food.getFoodbyName("Fish n' Chips");
    assertNotEquals(null, fishNChips, "Fish n' Chips not found");
    player.eat(Food.getFoodbyName("Fish n' Chips"));
    assertEquals(50, player.getEnergy(), "Energy should be 50 after eating Fish n' Chips");
  }

  @Test
  void testEatFish() {
    Fish catfish = Fish.getFishbyName("Catfish");
    assertNotEquals(null, catfish, "Catfish not found");
    player.eat(catfish);
    assertEquals(1, player.getEnergy(), "Energy should be 1 after eating Catfish");
  }

  @Test
  void testEatCrop() {
    Crop parsnip = Crop.getCropByName("Parsnip");
    assertNotEquals(null, parsnip, "Parsnip not found");
    player.eat(parsnip);
    assertEquals(3, player.getEnergy(), "Energy should be 3 after eating Parsnip");
  }  

  @BeforeEach
  void setUp() {
    player.setEnergy(0);
    assertEquals(0, player.getEnergy(), "Initial energy should be 0");
  }

  @Test
  void testSetEnergy() {
    player.setEnergy(50);
    assertEquals(50, player.getEnergy(), "Energy should be set to 50");
    
    player.setEnergy(200);
    assertEquals(100, player.getEnergy(), "Energy should not exceed 100, should be capped at 100");
    
    player.setEnergy(-30);
    assertEquals(-20, player.getEnergy(), "Energy should not be less than -20, should be capped at -20. Karena bakal forceSleep() di - 20");
  }

  @Test
  void testInitialInventory() {
    assertNotEquals(null, player.getInventory(), "Player's inventory should not be null");
    assertNotEquals(0, player.getInventory().getAllItems().size(),"Player's inventory should not be empty at initialization");
    Set<Equipment> equipmentSet = Equipment.getEquipmentSet();
    equipmentSet.forEach(equipment -> {
      assertNotEquals(null, equipment, "Equipment should not be null");
      assertEquals(1, player.getInventory().countItem(equipment), "Player should have initial equipment in inventory: " + equipment.getName());
    });
    assertEquals(15, player.getInventory().countItem(Seed.getSeedByName("Parsnip Seeds")), "Player should have 15 Parsnip Seeds in inventory");
  }
}
