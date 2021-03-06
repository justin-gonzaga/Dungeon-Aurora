package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Random;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController.GameMode;
import dungeonmania.entities.collectables.Anduril;
import dungeonmania.entities.collectables.Armour;
import dungeonmania.entities.collectables.OneRing;
import dungeonmania.entities.movings.Player;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;

/**
 * Force battles on the player while constantly healing him up and check loot
 * distributions.
 */
public class TestLoot {
    DungeonManiaController dc;
    Dungeon dungeon;
    Player player;

    @BeforeEach
    public void setStartingPostition() throws IOException {
        String content = FileLoader.loadResourceFile("/dungeons/_tiny.json");
        dungeon = Dungeon.fromJSONObject(new Random(11), "name", GameMode.STANDARD, new JSONObject(content));
        dc = new DungeonManiaController(dungeon);
        player = TestUtils.getPlayer(dungeon);

        dungeon.getMap().flood();
    }

    @Test
    public void testZombieDrops() {
        DungeonResponse r = null;
        Integer armourCount = 0;
        Integer oldCount = 0;
        for (int i = 0; i < 100; i++) {
            TestUtils.spawnZombieToast(dungeon, 0, 0);
            r = dc.tick(null, Direction.NONE);
            if (TestUtils.countInventoryOfType(r, Armour.STRING_TYPE) > oldCount)
                armourCount++;
            oldCount = (int) TestUtils.countInventoryOfType(r, Armour.STRING_TYPE);
            player.setHealth(20);
        }

        // Integer armourCount = (int) TestUtils.countInventoryOfType(r,
        // Armour.STRING_TYPE);

        // Probability of failing on a valid solution = 0.00763
        assertTrue(armourCount > 5 && armourCount < 25);
    }

    @Test
    public void testMercenaryDrops() {
        DungeonResponse r = null;
        Integer armourCount = 0;
        Integer oldCount = 0;
        for (int i = 0; i < 100; i++) {
            TestUtils.spawnMercenary(dungeon, 0, 0);
            r = dc.tick(null, Direction.NONE);
            if (TestUtils.countInventoryOfType(r, Armour.STRING_TYPE) > oldCount)
                armourCount++;
            oldCount = (int) TestUtils.countInventoryOfType(r, Armour.STRING_TYPE);
            player.setHealth(20);
        }

        // Probability of failing on a valid solution = 0.00149
        assertTrue(armourCount > 15 && armourCount < 45);
    }

    @Test
    public void testAssassinDrops() {
        DungeonResponse r = null;
        Integer armourCount = 0;
        Integer oldCount = 0;
        for (int i = 0; i < 100; i++) {
            TestUtils.spawnAssassin(dungeon, 0, 0);
            player.setHealth(40);
            r = dc.tick(null, Direction.NONE);
            if (TestUtils.countInventoryOfType(r, Armour.STRING_TYPE) > oldCount)
                armourCount++;
            oldCount = (int) TestUtils.countInventoryOfType(r, Armour.STRING_TYPE);
        }

        // Probability of failing on a valid solution = 0.00149
        assertTrue(armourCount > 15 && armourCount < 45);
    }

    @Test
    public void testOneRingDrops() {
        DungeonResponse r = null;
        Integer itemCount = 0;
        Integer oldCount = 0;
        for (int i = 0; i < 100; i++) {
            TestUtils.spawnZombieToast(dungeon, 0, 0);
            player.setHealth(40);
            r = dc.tick(null, Direction.NONE);
            if (TestUtils.countInventoryOfType(r, OneRing.STRING_TYPE) > oldCount)
                itemCount++;
            oldCount = (int) TestUtils.countInventoryOfType(r, OneRing.STRING_TYPE);
        }

        System.out.println(itemCount);

        // Probability of failing on a valid solution = 0.01238
        assertTrue(itemCount > 3 && itemCount < 19);
    }

    @Test
    public void testAndurilDrops() {
        DungeonResponse r = null;
        Integer itemCount = 0;
        Integer oldCount = 0;
        for (int i = 0; i < 100; i++) {
            TestUtils.spawnZombieToast(dungeon, 0, 0);
            player.setHealth(40);
            r = dc.tick(null, Direction.NONE);
            if (TestUtils.countInventoryOfType(r, Anduril.STRING_TYPE) > oldCount)
                itemCount++;
            oldCount = (int) TestUtils.countInventoryOfType(r, Anduril.STRING_TYPE);
        }
        System.out.println(itemCount);

        // Probability of failing on a valid solution = 0.01176
        assertTrue(itemCount > 10 && itemCount < 31);
    }

}
