package dungeonmania.movement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import dungeonmania.Cell;
import dungeonmania.DungeonMap;
import dungeonmania.Pos2d;
import dungeonmania.util.Direction;

/**
 * Represents the circle movement behaviour used by a spider.
 */
public class CircleMovementBehaviour extends MovementBehaviour {
    private DungeonMap map;

    public DungeonMap getMap() {
        return this.map;
    }
    private List<Pos2d> movementCycle = new ArrayList<>();
    private int step = 0;
    private int direction = 1;

    public CircleMovementBehaviour(int precedence, DungeonMap map, Cell initialCell)
    {
        super(precedence, initialCell);
        this.map = map;
        int initialX = initialCell.getPosition().getX();
        int initialY = initialCell.getPosition().getY();

        this.movementCycle.add(new Pos2d(
            initialX, 
            initialY - 1
        ));
        this.movementCycle.add(new Pos2d(
            initialX + 1, 
            initialY - 1
        ));
        this.movementCycle.add(new Pos2d(
            initialX + 1, 
            initialY
        ));
        this.movementCycle.add(new Pos2d(
            initialX + 1, 
            initialY + 1
        ));
        this.movementCycle.add(new Pos2d(
            initialX, 
            initialY + 1
        ));
        this.movementCycle.add(new Pos2d(
            initialX - 1, 
            initialY + 1
        ));
        this.movementCycle.add(new Pos2d(
            initialX - 1, 
            initialY
        ));
        this.movementCycle.add(new Pos2d(
            initialX - 1, 
            initialY - 1
        ));
    }

    public Cell move()
    {   
        // if the spider is completely blocked, don't move
        Predicate<Pos2d> isBlocking = p -> {
            Cell c = map.getCell(p);
            if (c == null) return true; // it's blocking
            return c.hasBoulder();
        };
        if (movementCycle.stream().allMatch(isBlocking)) {
            return getCurrentCell();
        }

        Cell nextCell = map.getCell(movementCycle.get(step));
        if (nextCell == null || nextCell.hasBoulder())
        {
            // The case where the spider spawns right under the boundry
            if (step == 0 && nextCell == null) {
                step++;
                setCurrentCell(map.getCellAround(getCurrentCell(), Direction.DOWN));
                nextCell = map.getCell(movementCycle.get(0));
                super.setCurrentCell(nextCell);
                return nextCell;
            }
            direction *= -1;
            step += direction;

            // Checking this here covers edge cases.
            if (step > 7) step = 0;
            if (step < 0) step = 7;

            nextCell = map.getCell(movementCycle.get(step));
        }
        step += direction;

        if (step > 7) step = 0; 
        if (step < 0) step = 7; 

        super.setCurrentCell(nextCell);

        return nextCell;
    }

    /**
     * Updates the position but also moves the movement circle along.
     */
    @Override
    public void setCurrentCell(Cell cell) {
        int xDiff = getCurrentCell().getPosition().getX() - cell.getPosition().getX();
        int yDiff = getCurrentCell().getPosition().getY() - cell.getPosition().getY();

        super.setCurrentCell(cell);

        movementCycle.stream().forEach(pos -> {
            pos.setX(pos.getX() - xDiff);
            pos.setY(pos.getY() - yDiff);
        });
    }
    
}
