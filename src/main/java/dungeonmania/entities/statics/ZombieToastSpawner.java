package dungeonmania.entities.statics;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dungeonmania.Cell;
import dungeonmania.Dungeon;
import dungeonmania.DungeonManiaController.GameMode;
import dungeonmania.Pos2d;
import dungeonmania.Utils;
import dungeonmania.entities.StaticEntity;
import dungeonmania.entities.movings.ZombieToast;
import dungeonmania.util.BlockingReason;
import dungeonmania.util.Semy;
import dungeonmania.util.Semy.Observer;

public class ZombieToastSpawner extends StaticEntity {

    public static String STRING_TYPE = "zombie toast spawner";
    private int tickCount = 0;

    private Semy<ZombieToast> onZombieSpawnSemy = new Semy<>();

    public ZombieToastSpawner(Dungeon dungeon, Pos2d position) {
        super(dungeon, position);
    }

    private static EnumMap<GameMode, Integer> spawnEveryNTicks = new EnumMap<>(Map.of(
        GameMode.PEACEFUL, 20,
        GameMode.STANDARD, 20,
        GameMode.HARD, 15
    ));

    @Override
    public BlockingReason isBlocking() {
        return BlockingReason.SPAWNER;
    }

    @Override
    public boolean isInteractable() {
        return false;
    }

    @Override
    public String getTypeAsString() {
        return ZombieToastSpawner.STRING_TYPE;
    }

    @Override
    public void tick() {
        this.tickCount++;
        if (this.tickCount % spawnEveryNTicks.get(this.dungeon.getGameMode()) != 0)
            return;


        // check cells where can spawn a zombie
        List<Cell> availableCells = this.getCellsAround()
            .filter(cell -> !cell.isBlocking())
            .collect(Collectors.toList());

        if (availableCells.size() == 0)
            return; // don't spawn anything

        // choose a random cell
        Cell cell = Utils.choose(availableCells);
        ZombieToast zombieToast = new ZombieToast(this.dungeon, cell.getPosition());
        cell.addOccupant(zombieToast);
        cell.onWalked(this.position, cell.getPosition());
        this.onZombieSpawnSemy.emit(zombieToast);
    }

    /**
     * 
     * @param o observer that will be called when a ZombieToast is spawned
     */
    public void onZombieToastSpawn(Observer<ZombieToast> o) {
        this.onZombieSpawnSemy.bind(o);
    }
}
