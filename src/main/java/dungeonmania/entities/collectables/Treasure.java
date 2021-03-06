// java doesn't support static folders, since it's a folder, so we use the plural
package dungeonmania.entities.collectables;

import dungeonmania.Dungeon;
import dungeonmania.Pos2d;
import dungeonmania.entities.CollectableEntity;

/**
 * Represents a treasure.
 * A treasure can be collected by the player.
 * Can be used for bribing or building items.
 */
public class Treasure extends CollectableEntity {

    public static String STRING_TYPE = "treasure";

    public Treasure(Dungeon dungeon, Pos2d position) {
        super(dungeon, position);
    }

    @Override
    public String getTypeAsString() {
        return Treasure.STRING_TYPE;
    }

    @Override
    public boolean isInteractable() {
        return false; // i don't think so at least
    }

    @Override
    public void tick() {
    }
}
