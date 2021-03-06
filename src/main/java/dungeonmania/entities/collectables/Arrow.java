// java doesn't support static folders, since it's a folder, so we use the plural
package dungeonmania.entities.collectables;

import dungeonmania.Dungeon;
import dungeonmania.Pos2d;
import dungeonmania.entities.CollectableEntity;

/**
 * Represents arrow that can be picked up by the player.
 * Used to construct buildable items.
 */
public class Arrow extends CollectableEntity {

    public static String STRING_TYPE = "arrow";

    public Arrow(Dungeon dungeon, Pos2d position) {
        super(dungeon, position);
    }

    @Override
    public String getTypeAsString() {
        return Arrow.STRING_TYPE;
    }

    @Override
    public boolean isInteractable() {
        return false; // i don't think so at least
    }

    @Override
    public void tick() {
    }
}
