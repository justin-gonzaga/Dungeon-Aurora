package dungeonmania;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONObject;

import dungeonmania.entities.Fighter;
import dungeonmania.entities.Fighter.FighterRelation;
import dungeonmania.entities.MovingEntity;
import dungeonmania.entities.StaticEntity;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.movings.Mercenary;
import dungeonmania.entities.movings.Player;
import dungeonmania.entities.movings.Spider;
import dungeonmania.entities.movings.ZombieToast;
import dungeonmania.entities.statics.Wall;
import dungeonmania.entities.statics.ZombieToastSpawner;
import dungeonmania.util.Direction;
import dungeonmania.util.Graph;
import dungeonmania.util.Vertex;

/**
 * Represents the map of the dungeon.
 * Stores each square of the map as a "Cell" class.
 */
public class DungeonMap {

    final private String PLAYER = " P ";
    final private String WALL = "###";
    final private String STATIC = " S ";
    final private String ENEMY = " E ";

    private List<List<Cell>> dungeonMap = new ArrayList<>();
    private int width;
    private int height;

    private Pos2d entry = null;
    
    public DungeonMap(JSONObject json) {
        this(json.getInt("width"), json.getInt("height"));
    }

    public DungeonMap(int width, int height) {
        this.width = width;
        this.height = height;
        // a grid of empty cells
        for (int y = 0; y < height; y++) {
            ArrayList<Cell> row = new ArrayList<>();
            for (int x = 0; x < width; x++) {
                row.add(new Cell(new Pos2d(x, y)));
            }
            dungeonMap.add(row);
        }
        resetDistances();
    }

    /**
     * Counts all the treasure remaining on the map
     */
    public Integer countTreasure() {
        int count = 0;
        for (List<Cell> row : dungeonMap) {
            for (Cell cell : row) {
                if (cell.getOccupants().stream().anyMatch(e -> e instanceof Treasure)) {
                    count++;
                } 
            }
        }
        return count;
    }

    /**
     * 
     * @return true if all floor switches on the map have been triggered.
     */
    public boolean allFloorSwitchesTriggered()  {
        for (List<Cell> row : dungeonMap) {
            for (Cell cell : row) {
                if (cell.hasDeactivatedFloorSwitch()) {
                    return false;
                } 
            }
        }
        return true;
    }

    /**
     * Count all entities that are either ZombieToastSpawners or have an ENEMY fighter relation.
     * @return
     */
    public Integer countEnemies() {
        int movingEnemyCount = (int) allEntities().stream().filter(e -> e instanceof MovingEntity && 
                                    ((Fighter) e).getFighterRelation() == FighterRelation.ENEMY).count();
        int enemyStructureCount = countSpawners();
    
        return movingEnemyCount + enemyStructureCount;
    }


    /**
     * Counts all cells with spawners remaining on the map
     */
    public Integer countSpawners() {
        return (int) allEntities().stream().filter(e -> e instanceof ZombieToastSpawner).count();
    }

    /**
     * Counts number of zombies remaining on the map
     */
    public Integer countZombieToasts() {
        return (int) allEntities().stream().filter(e -> e instanceof ZombieToast).count();
    }

    /**
     * Sets all player distances to the maximum and sets the cell with the player to 0.
     */
    private void resetDistances() {
        for (List<Cell> row : dungeonMap) {
            for (Cell cell : row) {
                if (cell.getOccupants().stream().anyMatch(e -> e instanceof Player)) {
                    cell.setPlayerDistance(0);
                } else {
                    cell.setPlayerDistance(width * height);
                }
            }
        }
    }
    
    /**
     * @return the Cell that represents the given position
     */
    public Cell getCell(Pos2d pos) {
        return this.getCell(pos.getX(), pos.getY());
    }

    /**
     * 
     * @return list of all entities inside the dungeon
     */
    public List<Entity> allEntities() {
        List<Entity> all = new ArrayList<Entity>();
        for (List<Cell> row : dungeonMap) {
            for (Cell cell : row) {
                all.addAll(cell.getOccupants());
            }
        }
        return all;
    }

    /**
     * @return the Cell that represents the given position
     */
    public Cell getCell(int x, int y) {
        if (y < 0 || y >= height) return null;
        if (x < 0 || x >= width) return null;

        return dungeonMap.get(y).get(x);
    }

    /**
     * Retreives Cell Player is Currently In
     * @return Cell
     */
    public Cell getPlayerCell() {
        for (List<Cell> row : dungeonMap) {
            for (Cell cell : row) {
                //Checks Where Cell is 0 blocks from player
                if (cell.hasPlayer()) {
                    return cell;
                }
            }
        }
        return null;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
    
    /**
     * Fills in all the distances from the player for all the cells. Calls 
     * setDistances() initially to reset values.
     */
    public void flood() {
        resetDistances();

        int explorationLevel = 0;
        int valuesChanged = 1;

        while (valuesChanged != 0) {
            valuesChanged = 0;

            // Look for cells with the current explorationLevel
            for (List<Cell> row : dungeonMap) {
                for (Cell cell : row) {
                    if (cell.getPlayerDistance() == explorationLevel) {
                        valuesChanged += this.propagateFrom(cell);
                    }
                }
            }

            explorationLevel++;
        }
    }

    /**
     * Uses Dijkstra's algorithm to find the shortest path between @param from
     * and @param to. Takes into account cell travel costs due to swamp blocks.
     * 
     * @param from the positions to start from
     * @param to the target position
     * @return a list of Cells in the order that they should be visited. Return 
     *  null if no path can be found.
     */
    public List<Cell> findPath(Cell from, Cell to) {
        // order with lowest cost first
        PriorityQueue<Vertex<Cell>> q = new PriorityQueue<Vertex<Cell>>(11, (a, b) -> a.getDistance()- b.getDistance());
        Map<Cell, Integer> visited = new HashMap<>();
        Graph<Cell> tree = new Graph<>();

        q.add(new Vertex<Cell>(from, 0));
        visited.put(from, 0);
        tree.addVertex(new Vertex<Cell>(from, 0));

        while (!q.isEmpty()) {
            Cell current = q.poll().getData();
            if (current.equals(to)) {
                // Target found, return path
                return tree.tracebackFrom(new Vertex<Cell>(current))
                    .stream().map(v -> v.getData()).collect(Collectors.toList());
            }

            getNeighbors(current).stream().filter(n -> !n.isBlocking()).forEach(n -> {
                Integer newCost = visited.get(current) + n.getTravelCost();
                if (visited.keySet().contains(n)) {
                    // resolve duplicate
                    if (newCost < visited.get(n)) {
                        visited.put(n, newCost);
                        // Remove old link and add the new faster one
                        tree.removeVertex(new Vertex<Cell>(n));
                        tree.addEdge(new Vertex<Cell>(current), new Vertex<Cell>(n));
                    }

                } else {
                    // visit
                    visited.put(n, newCost);
                    q.add(new Vertex<Cell>(n, newCost));
                    tree.addEdge(new Vertex<Cell>(current), new Vertex<Cell>(n));
                }
            });
        }

        return null;
    }

    /**
     * Direction.NONE returns the given cell
     * 
     * @param cell
     * @param d
     * @return the cell above, below, left or right of cell, depending on direction
     */
    public Cell getCellAround(Cell cell, Direction d) {
        Pos2d pos = cell.getPosition();
        if (d == Direction.UP) {
            if (pos.getY() == 0) {
                return null;
            }
            return getCell(pos.getX(), pos.getY() - 1);
        } else if (d == Direction.DOWN) {
            if (pos.getY() == getHeight() - 1) {
                return null;
            }
            return getCell(pos.getX(), pos.getY() + 1);
        } else if (d == Direction.LEFT) {
            if (pos.getX() == 0) {
                return null;
            }
            return getCell(pos.getX() - 1, pos.getY());
        } else if (d == Direction.RIGHT) {
            if (pos.getX() == getWidth() - 1) {
                return null;
            }
            return getCell(pos.getX() + 1, pos.getY());
        } else {
            return cell;
        }
    }

    /**
     * Direction.NONE returns the given cell
     * 
     * @param cell
     * @param d
     * @return the cell above, below, left or right of cell, depending on direction
     */
    public Cell getCellAround(Pos2d position, Direction d) {
        Cell cell = getCell(position);
        return getCellAround(cell, d);
    }


    /**
     * Note that it doesn't always return 4 cells. If you are on a top-most
     * cell, it will only return (left, bottom, right)
     * @return cells around the current cell
     */
    public Stream<Cell> getCellsAround(Cell base) {
        return Arrays.stream(Direction.values())
            .filter(d -> d != Direction.NONE)
            .map(direction -> this.getCellAround(base, direction))
            .filter(cell -> cell != null);
    }

    /**
     * help function to assist in calculating distance from player to cell
     * @param cell
     * @return
     */
    private int propagateFrom(Cell cell) {
        AtomicInteger changesMade = new AtomicInteger(0);

        Arrays.stream(Direction.values()).forEach(d -> {
            Cell neighbor = getCellAround(cell, d);
            if (neighbor != null) {
                if (!neighbor.isBlocking() && neighbor.getPlayerDistance() == width * height) {
                    changesMade.incrementAndGet();
                    neighbor.setPlayerDistance(cell.getPlayerDistance() + 1);
                }
            }
        });

        return changesMade.get();
    }

    /**
     * Returns the neighbors of cell Ignores blocks off the map.
     * 
     * @param cell
     * @return List of cells neigboring the given cell.
     */
    public List<Cell> getNeighbors(Cell cell)
    {
        return Arrays.stream(Direction.values())
            .filter(d -> getCellAround(cell, d) != null)
            .map(d -> getCellAround(cell, d))
            .collect(Collectors.toList());
    }

    /**
     * @return true if the entity was found (and removed) in the map
     */
    public boolean removeEntity(Entity e) {
        return this.getCell(e.getPosition()).removeOccupant(e);
    }


    @Override
    public String toString() {
        String result = "";
        for (List<Cell> row : dungeonMap) {
            for (Cell cell : row) {
                if (cell.getOccupants().stream().anyMatch(e -> e instanceof Player)) {
                    result += PLAYER;
                } else if (cell.getOccupants().stream().anyMatch(e -> e instanceof Wall)) {
                    result += WALL;
                } else if (cell.getOccupants().stream().anyMatch(e -> e instanceof Mercenary)) {
                    result += " EM";
                } else if (cell.getOccupants().stream().anyMatch(e -> e instanceof Spider)) {
                    result += " ES";
                } else if (cell.getOccupants().stream().anyMatch(e -> e instanceof ZombieToast)) {
                    result += " EZ";
                } else if (cell.getOccupants().stream().anyMatch(e -> e instanceof MovingEntity)) {
                    result += ENEMY;
                } else if (cell.getOccupants().stream().anyMatch(e -> e instanceof StaticEntity)) {
                    result += STATIC;
                } else {
                    result += "   ";
                    // int num = cell.getPlayerDistance();
                    // if (num < 10) result += " " + num + " ";
                    // else result += " " + num;
                }
            }
            result += "\n";
        }

        return result;
    }

    /**
     * Should only be called once, during construction
     * @param pos the entry position (see .getEntry())
     */
    public void setEntry(Pos2d pos) {
        assert this.entry == null : "set entry should only be called once, during construction";
        this.entry = pos;
    }

    /**
     * the entry of the map is where the player spawns (and the cell on which
     * mercenaries later spawn)
     * @return entry position
     */
    public Pos2d getEntry() {
        return this.entry;
    }

    /**
     * the entry of the map is where the player spawns (and the cell on which
     * mercenaries later spawn)
     * @return Cell that represents the entry position
     */
    public Cell getEntryCell() {
        return this.getCell(this.getEntry());
    }
}
