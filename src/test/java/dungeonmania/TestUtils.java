package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import dungeonmania.entities.movings.Mercenary;
import dungeonmania.entities.movings.Player;
import dungeonmania.entities.movings.Spider;
import dungeonmania.entities.movings.ZombieToast;
import dungeonmania.entities.statics.Boulder;
import dungeonmania.entities.statics.Wall;
import dungeonmania.entities.statics.ZombieToastSpawner;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Position;

public class TestUtils {
    public static Position getPlayerPosition(DungeonResponse resp) {
        for (EntityResponse e : resp.getEntities()) {
            if (Objects.equals(e.getType(), "player")) {
                return e.getPosition();
            }
        }
        throw new Error("player wasn't found");
    }

    public static Mercenary spawnMercenary(Dungeon dungeon, int x, int y) {
        Cell mercCell = dungeon.getMap().getCell(x, y);
        Mercenary merc = new Mercenary(dungeon, mercCell.getPosition());
        mercCell.addOccupant(merc);

        return merc;
    }

    public static Spider spawnSpider(Dungeon dungeon, int x, int y) {
        Cell spiderCell = dungeon.getMap().getCell(x, y);
        Spider spider = new Spider(dungeon, spiderCell.getPosition());
        spiderCell.addOccupant(spider);

        return spider;
    }

    public static Mercenary getMercenary(Dungeon dungeon) {
        return (Mercenary) dungeon.getMap().allEntities().stream()
        .filter(e -> e instanceof Mercenary)
        .findFirst().orElse(null);
    }

    public static Player getPlayer(Dungeon dungeon) {
        return (Player) dungeon.getMap().allEntities().stream()
        .filter(e -> e instanceof Player)
        .findFirst().orElse(null);
    }

    public static ZombieToast getZombieToast(Dungeon dungeon) {
        return (ZombieToast) dungeon.getMap().allEntities().stream()
        .filter(e -> e instanceof ZombieToast)
        .findFirst().orElse(null);
    }

    /**
     * asserts that as and bs contain the same element
     * @param <T>
     * @param as
     * @param bs
     */
    public static <T extends Comparable<T>> void assertEqualsUnordered(List<T> as, List<T> bs) {
        Collections.sort(as);
        Collections.sort(bs);
        assertEquals(as, bs);
    }


    public static boolean isBlocking(EntityResponse resp) {
        return List.of(Wall.STRING_TYPE, ZombieToastSpawner.STRING_TYPE).contains(resp.getType());
    }

    public static long countEntitiesOfType(DungeonResponse resp, String type) {
        return resp.getEntities().stream().filter(e -> Objects.equals(e.getType(), type)).count();
    }
    
    /**
     * puts boulders all around the position so that the damn thing can't move
     * and it makes testing easier
     * 
     * Can only be used in white box testing
     * 
     * @param map
     * @param center position that is going to be locked
     */
    public static void lockWithBoulders(Dungeon dungeon, Pos2d center) {
        DungeonMap map = dungeon.getMap();
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;

                Pos2d curr = center.plus(new Pos2d(dx, dy));

                map.getCell(curr).addOccupant(new Boulder(dungeon, curr));
            }
        }
    }
}
