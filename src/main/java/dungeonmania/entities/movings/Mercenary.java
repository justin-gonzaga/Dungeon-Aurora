package dungeonmania.entities.movings;

import dungeonmania.Dungeon;
import dungeonmania.Entity;
import dungeonmania.Pos2d;
import dungeonmania.battlestrategies.BattleStrategy.BattleDirection;
import dungeonmania.entities.Fighter;
import dungeonmania.entities.MovingEntity;
import dungeonmania.movement.FollowMovementBehaviour;
import dungeonmania.movement.FriendlyMovementBehaviour;
import dungeonmania.movement.MovementBehaviour;

public class Mercenary extends MovingEntity implements Fighter {

    public static final String STRING_TYPE = "mercenary";
    public static final int SPAWN_EVERY_N_TICKS = 50;

    private float health = 6;
    private FighterRelation relationship = FighterRelation.ENEMY;

    private MovementBehaviour followMovementBehaviour;
    private MovementBehaviour friendlyMovementBehaviour;

    private int mindControlDuration = 0;

    public Mercenary(Dungeon dungeon, Pos2d position) {
        super(dungeon, position);
        this.followMovementBehaviour = new FollowMovementBehaviour(
            0, 
            dungeon.getMap(), 
            dungeon.getMap().getCell(position)
        );
        this.friendlyMovementBehaviour = new FriendlyMovementBehaviour(
            0, 
            dungeon.getMap(), 
            getCell()
        );
        this.addMovementBehaviour(this.followMovementBehaviour);
    }

    public void bribe() {
        mindControlDuration = -1;
        relationship = FighterRelation.ALLY;
        // order matters! add first, then remove
        this.addMovementBehaviour(this.friendlyMovementBehaviour);
        this.removeMovementBehaviour(this.followMovementBehaviour);
    }

    public void mindControl() {
        if (mindControlDuration < 0) {
            return; // already bribed, do nothing
        } else {
            bribe();
            mindControlDuration = 10;
        }
    }

    public void useMindControl() {
        if (mindControlDuration > 0) {
            mindControlDuration--;
        } else if (mindControlDuration == 0) {
            relationship = FighterRelation.ENEMY;
            this.addMovementBehaviour(this.followMovementBehaviour);
            this.removeMovementBehaviour(this.friendlyMovementBehaviour);
        }
    }

    @Override
    public String getTypeAsString() {
        return Mercenary.STRING_TYPE;
    }

    @Override
    public void tick() {
        useMindControl();
        this.move();
    }

    @Override
    public float getHealth() {
        return this.health;
    }

    @Override
    public void setHealth(float h) {
        this.health = h;
    }

    @Override
    public float getAttackDamage() {
        return 1;
    }

    @Override
    public float getDefenceCoef() {
        return 1;
    }

    @Override
    public void usedItemFor(BattleDirection d) {
        // mercenaries don't use items, so that doesn't matter
    }

    @Override
    public FighterRelation getFighterRelation() {
        return relationship;
    }

    @Override
    public Entity getEntity() {
        return this;
    }

    @Override
    public boolean isInteractable() {
        return relationship == FighterRelation.ENEMY;
    }
}
