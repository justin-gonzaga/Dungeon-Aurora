// java doesn't support static folders, since it's a folder, so we use the plural
package dungeonmania.entities.statics;

import dungeonmania.entities.StaticEntity;

public class Wall extends StaticEntity {

    @Override
    public boolean isBlocking() {
        return true;
    }

    @Override
    public String getTypeAsString() {
        return "wall";
    }

    @Override
    public boolean isInteractable() {
        return false;
    }
}
