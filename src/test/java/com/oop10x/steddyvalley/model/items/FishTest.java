package com.oop10x.steddyvalley.model.items;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

public class FishTest {
  @Test
  void testFishBuyPrice() {
    Set<Fish> fishList = Fish.getFishSet();
    fishList.forEach(fish -> {
      assertEquals(null, fish.getBuyPrice(), "Buy price should be null for all Fish");
    });
  }
  @Test
  void testFishSellPrice() {
    Fish fish = Fish.getFishbyName("Carp");
    assertEquals("Carp", fish.getName(), "Fish name should be Carp");
    assertEquals(20, fish.getSellPrice(), "Sell price should be 100 for Carp");
  }
}
