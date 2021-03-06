package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController.GameMode;
import dungeonmania.entities.movings.Spider;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestSpider {

    @Test
    public void testSpiderSpawn() {
        DungeonManiaController ctr = new DungeonManiaController();
        DungeonResponse resp = ctr.newGame("maze", GameMode.PEACEFUL.getValue());
        ctr.setSeed(1);
        for (int i = 0 ; i < Spider.SPAWN_EVERY_N_TICKS; i++) {
            assertFalse(resp.getEntities().stream().anyMatch(x -> x.getType().equals(Spider.STRING_TYPE)));
            resp = ctr.tick(null, Direction.NONE);
        }
        assertTrue(resp.getEntities().stream().anyMatch(x -> x.getType().equals(Spider.STRING_TYPE)));
    }

    /**
     * Regression test: spiders use to spawn randomly, even when you specified a
     * position on the map. Instead, 
     */
    @Test
    public void testSpiderSpawnFromMap() {
        DungeonManiaController ctr = new DungeonManiaController();
        DungeonResponse resp = ctr.newGame("_spider_on_map", GameMode.PEACEFUL.getValue());
        ctr.setSeed(1);
        Position p = resp.getEntities().stream().filter(e -> e.getType().equals(Spider.STRING_TYPE)).findFirst().get().getPosition();
        assertEquals(6, p.getX());
        assertEquals(5, p.getY());
        System.out.println(
            resp.getEntities().stream().filter(e -> e.getType().equals(Spider.STRING_TYPE)).map(e -> e.getId()).collect(Collectors.toList())
        );
        assertEquals(1, resp.getEntities().stream().filter(e -> e.getType().equals(Spider.STRING_TYPE)).count());
    }

    @Test
    public void testSpiderMovement() {
        DungeonManiaController ctr = new DungeonManiaController();
        DungeonResponse resp = ctr.newGame("_simple", GameMode.PEACEFUL.getValue());
        ctr.setSeed(1);

        for (int i = 0; i < Spider.SPAWN_EVERY_N_TICKS; i++)
            resp = ctr.tick(null, Direction.NONE);
        assertTrue(resp.getEntities().stream().anyMatch(x -> x.getType().equals(Spider.STRING_TYPE)));

        Map<String, Pos2d> positions = new HashMap<>();

        for (int i = 1; i < 50; i++) {
            resp = ctr.tick(null, Direction.NONE);
            for (EntityResponse spider : resp.getEntities()) {
                if (spider.getType() != Spider.STRING_TYPE) {
                    continue;
                }
                // make sure it only moved one cell, horizontally or vertically
                Pos2d prev = positions.get(spider.getId());
                Pos2d curr = Pos2d.from(spider.getPosition());
                if (prev != null) {
                    int distance = prev.squareDistance(curr);
                    assertTrue(distance == 1);
                }
                    
                positions.put(spider.getId(), curr);
                
            }
        }
    }

    @Test
    public void testSpiderId() {
        DungeonManiaController ctr = new DungeonManiaController();
        DungeonResponse resp = ctr.newGame("_spider_on_map", GameMode.STANDARD.getValue());
        ctr.setSeed(1);

        for (int i = 0; i < 100; i++) {
            resp = ctr.tick(null, Direction.DOWN);
            List<String> idList = resp.getEntities().stream().map(e -> e.getId()).collect(Collectors.toList());
            Set<String> idSet = new HashSet<String>(idList);
            assertTrue(idList.size() == idSet.size());
        }
    }


    @Test
    public void testNullPointerError() {
        DungeonManiaController ctr = new DungeonManiaController();
        ctr.newGame("maze", GameMode.PEACEFUL.getValue());
        ctr.setSeed(1);
    
        for (int i = 0; i < 50; i++) {
            ctr.tick(null, Direction.NONE);
        }
    }

    @Test
    public void testSpiderSpawnLimit() {
        DungeonManiaController ctr = new DungeonManiaController();
        DungeonResponse resp = ctr.newGame("maze", GameMode.PEACEFUL.getValue());
        ctr.setSeed(1);

        for (int i = 0; i < 10; i ++) {
            resp = ctr.tick(null, Direction.NONE);
        }
        long numSpiders = resp.getEntities().stream().filter(x -> x.getType().equals(Spider.STRING_TYPE)).count();
        assertTrue(numSpiders <= 5);
    }

}
