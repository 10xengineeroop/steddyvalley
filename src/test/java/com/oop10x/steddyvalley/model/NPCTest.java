package com.oop10x.steddyvalley.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import com.oop10x.steddyvalley.model.items.Fish;
import com.oop10x.steddyvalley.model.items.Misc;
import com.oop10x.steddyvalley.model.items.Seed;
import com.oop10x.steddyvalley.utils.RelStatus;

public class NPCTest {
  @Test
  void testGetItem() {
    NPC emily = NPC.getNpcByName("Emily");
    assertNotEquals(null, emily, "Emily not found");
    assertEquals(0, emily.getHeartPoints(), "Initial heart points should be 0");
    emily.getItem(Seed.getSeedByName("Wheat Seeds"));
    assertEquals(25, emily.getHeartPoints(), "Heart points should be 25 after receiving Wheat Seed");
    emily.getItem(Fish.getFishbyName("Catfish"));
    assertEquals(45, emily.getHeartPoints(), "Heart points should be 45 after receiving Catfish");
    emily.getItem(Misc.getMisc("Coal"));
    assertEquals(20, emily.getHeartPoints(), "Heart points should be 20 after receiving Coal");
  }

  @Test
  void testPropose() {
    NPC abigail = NPC.getNpcByName("Abigail");
    assertNotEquals(null, abigail, "Abigail not found");
    assertEquals(RelStatus.SINGLE, abigail.getRelationshipStatus(), "Initial relationship status should be SINGLE");
    abigail.setHeartPoints(149);
    Player player = new Player(10, 10, 10, 10, 10);
    abigail.propose(player);
    assertEquals(RelStatus.SINGLE, abigail.getRelationshipStatus(), "Abigail should not accept proposal with less than 150 hearts");
    abigail.setHeartPoints(150);
    abigail.propose(player);
    assertEquals(RelStatus.FIANCE, abigail.getRelationshipStatus(), "Abigail should accept proposal with 150 hearts");
    abigail.propose(player);
    assertEquals(RelStatus.SPOUSE, abigail.getRelationshipStatus(), "Abigail should advance to SPOUSE after proposal");
  }

  @Test
  void testSetHeartPoints() {
    NPC mayorTadi = NPC.getNpcByName("Mayor Tadi");
    assertNotEquals(null, mayorTadi, "Mayor Tadi not found");
    mayorTadi.setHeartPoints(100);
    assertEquals(100, mayorTadi.getHeartPoints(), "Heart points should be set to 100");
    mayorTadi.setHeartPoints(200);
    assertEquals(150, mayorTadi.getHeartPoints(), "Heart points should not exceed 150");
    mayorTadi.setHeartPoints(-50);
    assertEquals(0, mayorTadi.getHeartPoints(), "Heart points should not be less than 0");
  }

  @Test
  void testChatNPC() {
    NPC emily = NPC.getNpcByName("Emily");
    assertNotEquals(null, emily, "Emily not found");
    int initialHeartPoints = emily.getHeartPoints();
    emily.chat();
    assertEquals(initialHeartPoints + 10, emily.getHeartPoints(), "Heart points should increase by 10 after chatting with Emily");
  }
}
