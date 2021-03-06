package dungeonmania.goal;

import dungeonmania.Dungeon;
import dungeonmania.DungeonMap;

/**
 * Represents an enemies goal.
 * A dungeon game is won when there are no enemies left in the dungeon.
 */
public class EnemiesGoal extends Goal {

    public static String STRING_TYPE = "enemies";
    public EnemiesGoal() {
        super();
    }

    @Override
    public boolean isCompleted(Dungeon dungeon) {
        // Count all enemies and spawners
        DungeonMap map = dungeon.getMap();
        if (map.countSpawners() == 0 && map.countEnemies() == 0) {
            return true;
        }

        return false;
    }

    @Override
    public String asString() {
        return "destroy all enemies and spawners";
    }
    
}
